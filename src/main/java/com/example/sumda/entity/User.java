package com.example.sumda.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "user")
public class User {

    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "user_acorns")
    private int userAcorn;

    @Column(name = "created_at")
    private Timestamp createdAt;

    public static User createUser(Long id, String userName, String userEmail) {

        return new User(id, userName, userEmail, 0, Timestamp.valueOf(LocalDateTime.now()));
    }
}
