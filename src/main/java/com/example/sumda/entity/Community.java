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

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "community")
public class Community {

    @Id
    @Column(name = "id")
    private long communityId; // 게시글 id

    @Column(name = "user_id")
    private long userId; // 작성자 id

    private String address; // 주소

    private int likes; // 좋아요

    @Column(name = "image_url")
    private String imageURl; // 이미지 경로

    @Column(name = "created_at")
    private Timestamp createdAt; // 생성일자

    @Column(name = "updated_at")
    private Timestamp updateAt; // 수정일자

    @Column(name = "deleted_at")
    private Timestamp deletedAt; // 삭제일자

}
