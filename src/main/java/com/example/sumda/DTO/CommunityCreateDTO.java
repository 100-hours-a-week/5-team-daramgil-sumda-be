package com.example.sumda.DTO;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommunityCreateDTO {

    private long userId;

    private String address;

    private String imageUrl;
}
