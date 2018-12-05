package com.example.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

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
