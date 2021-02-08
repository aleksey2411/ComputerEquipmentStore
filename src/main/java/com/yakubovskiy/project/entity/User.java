package com.yakubovskiy.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yakubovskiy.project.enums.UserRole;
import com.yakubovskiy.project.enums.UserStatus;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue
    @Type(type = "pg-uuid")
    @Column(name = "user_id")
    private UUID id;

    @Column(name = "user_name")
    private String name;

    @Column(name = "user_surname")
    private String surname;

    @Column(name = "user_patronymic")
    private String patronymic;

    @Column(name = "user_email")
    private String email;

    @JsonIgnore
    @Column(name = "user_password")
    private String password;

    @Column(name = "user_role")
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "user_status")
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "user_balance")
    private double balance;

    @JsonIgnore
    @ToString.Exclude
    @OneToOne(mappedBy = "user")
    private Cart cart;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "user")
    private Set<Order> orderSet;
}

