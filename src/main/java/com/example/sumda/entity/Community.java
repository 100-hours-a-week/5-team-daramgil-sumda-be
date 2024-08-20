package com.example.sumda.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "community")
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_id")
    private long communityId; // 게시글 id

    @Column(name = "user_id")
    private long userId; // 작성자 id

    private String address; // 주소

    private int likes; // 좋아요

    @Column(name = "image_url")
    private String imageUrl; // 이미지 경로

    @Column(name = "created_at")
    private Timestamp createdAt; // 생성일자

}
