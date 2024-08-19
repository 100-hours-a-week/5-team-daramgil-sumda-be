package com.example.sumda.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommunityDTO {

    private long communityId;

    private long userId;

    private String address;

    private int likes;

    private String imageUrl;

}
