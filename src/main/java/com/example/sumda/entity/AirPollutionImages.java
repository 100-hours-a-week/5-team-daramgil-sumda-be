package com.example.sumda.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AirPollutionImages {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "inform_code")
    private String informCode;

    @Column(name = "image_url")
    private String imageUrl;

}
