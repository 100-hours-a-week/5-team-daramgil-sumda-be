package com.example.sumda.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Table(name = "quizzes")
@Getter
@Setter
@NoArgsConstructor
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false, length = 1)
    private String answer;

    @Column(nullable = false)
    private String explanation;
}
