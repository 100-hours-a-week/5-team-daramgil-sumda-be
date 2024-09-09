package com.example.sumda.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "game_log")
public class GameLog {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "game_type_id", referencedColumnName = "id")
    private GameType gameType; // 게임 종류 id

    @Column(name = "game_name")
    private String gameName; // 게임명

    @Column(name = "score")
    private int score; // 게임 점수

    @Column(name = "get_acorns")
    private int getAcorns; // 게임으로 얻은 도토리 수

    @Column(name = "start_time")
    private Timestamp startTime; // 게임 시작 시간

    @Column(name = "end_time")
    private Timestamp endTime; // 게임 종료 시간
}
