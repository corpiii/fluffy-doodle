package com.clean_light.server.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class User {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String loginId;

    private String password;

    private String email;

    private String nickName;
}
