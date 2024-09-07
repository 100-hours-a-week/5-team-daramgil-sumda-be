package com.example.sumda.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "game_type")
public class GameType {

    @Id
    @Column(name = "id")
    private Long id; // 게임 종류 id

    @Column(name = "game_name")
    private String gameName; // 게임명
}
