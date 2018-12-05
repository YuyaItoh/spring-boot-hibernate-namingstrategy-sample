# Spring Boot + Hibernate Naming Strategy サンプル

## 概要

Spring Boot で Hibernate を利用するときの Entity と テーブル名、カラム名のマッピングについてのサンプル。
Hibernate 5.x では エンティティ名とそのプロパティをそれぞれテーブル名とカラム名にマッピングする Naming Strategy が存在する。

NamingStrategy には Implicit Naming Strategy と Physical Naming Strategy のふたつがある

- Implicit Naming Strategy
    - エンティティのクラス名およびプロパティ名から、それに対応する論理名を取得する
    - `@Column` および `@Table` が付与されていない場合はクラス名、プロパティ名から取得する
    - `@Column` および `@Table` が付与されている場合はそのアノテーションで指定された値が論理名になる
- Physical Naming Strategy
    - 論理名から、それに対応する実際のテーブル名、カラム名を取得する

[Hibernate 5 Naming Strategy Configuration](https://www.baeldung.com/hibernate-naming-strategy) が参考になる。

## Spring Boot における Naming Strategy

[Spring Boot Reference Guide : 84.6 Configure Hibernate Naming Strategy](https://docs.spring.io/spring-boot/docs/2.1.1.RELEASE/reference/htmlsingle/#howto-configure-hibernate-naming-strategy) によると、
Spring Boot では、 Implicit Naming Strategy と Physical Naming Strategy にはそれぞれ [SpringImplicitNamingStrategy](https://github.com/spring-projects/spring-boot/blob/v2.1.1.RELEASE/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/orm/jpa/hibernate/SpringImplicitNamingStrategy.java) と [SpringPhysicalNamingStrategy](https://github.com/spring-projects/spring-boot/blob/v2.1.1.RELEASE/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/orm/jpa/hibernate/SpringPhysicalNamingStrategy.java) が利用される。

`SpringImplicitNamingStrategy` は [ImplicitNamingStrategyJpaCompliantImpl](https://github.com/hibernate/hibernate-orm/blob/5.3.7/hibernate-core/src/main/java/org/hibernate/boot/model/naming/ImplicitNamingStrategyJpaCompliantImpl.java) とほとんどの場合は同じ挙動を行い、論理名はそのままクラス名やプロパティ名となる。あるいは、 `@Column` や `@Table` を付与した場合はその値が論理名となる。

`SpringPhysicalNamingStrategy` はリファレンスに記述の通り、論理名を小文字の snake_case に変更する。
つまり Spring Boot のデフォルト設定では、作成されるテーブル名やカラム名はすべて小文字の snake_case になり、 camelCase のカラムやテーブルは作成できない。

デフォルトの設定を変更するには、以下のような設定を `application.properties` に加えれば良い

```bash
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
```

[PhysicalNamingStrategyStandardImpl](https://github.com/hibernate/hibernate-orm/blob/5.3.7/hibernate-core/src/main/java/org/hibernate/boot/model/naming/PhysicalNamingStrategyStandardImpl.java) は論理名をそのままテーブル名、カラム名にする Naming Strategy 。

## サンプル

`./gradlew bootRun` で実行し、その後 `http://localhost:8080/h2-console` にアクセスすると H2 によるコンソールが表示される。

```java
/*
 * 何も付与しない
 * ImplicitNamingStrategy -> UserInfo
 * PhysicalNamingStrategy -> user_info
 */
@Data
@Entity
public class UserInfo {
    @Id
    private Long id;

    /*
     * case 1: 何も付与しない
     * ImplicitNamingStrategy -> userName
     * PhysicalNamingStrategy -> user_name
     */
    private String userName;

    /*
     * case 2: @Column で snake_case 指定
     * ImplicitNamingStrategy -> user_address
     * PhysicalNamingStrategy -> user_address
     */
    @Column(name = "user_address")
    private String userAddress;

    /*
     * case 3: @Column で camelCalse 指定
     * ImplicitNamingStrategy -> userAge
     * PhysicalNamingStrategy -> user_age
     */
    @Column(name = "userAge")
    private Integer userAge;
}
```

このエンティティをもとに Hibernate でテーブルを作成すると、以下のような DDL が実行される。

```sql
create table user_info (
    id bigint not null,
    user_address varchar(255),
    user_age integer,
    user_name varchar(255),
    primary key (id)
);
```

## 参考サイト

- [Spring Boot Reference Guide : 84.6 Configure Hibernate Naming Strategy](https://docs.spring.io/spring-boot/docs/2.1.1.RELEASE/reference/htmlsingle/#howto-configure-hibernate-naming-strategy)
- [Hibernate ORM 5.3.7.Final User Guide : 2.2. Naming strategies](https://docs.jboss.org/hibernate/orm/5.3/userguide/html_single/Hibernate_User_Guide.html#naming)
- [Hibernate 5 Naming Strategy Configuration](https://www.baeldung.com/hibernate-naming-strategy)
