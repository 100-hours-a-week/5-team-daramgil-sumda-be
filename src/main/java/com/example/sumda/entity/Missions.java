package com.example.sumda.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "missions")
public class Missions {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private LocalDate date; // 날짜

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User userId;

    @Column(name = "attendance")
    private boolean attendance; // 출석완료

    @Column(name = "check_air")
    private boolean checkAir; // 대기오염조회

    @Column(name = "talk_with_squirrel")
    private boolean talkWithSquirrel; // 다람쥐와 대화하기

    @Column(name = "quiz")
    private boolean quiz; // ox퀴즈

}
