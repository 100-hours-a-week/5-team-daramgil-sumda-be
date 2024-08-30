package com.example.sumda.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "squirrel_type")
public class SquirrelType {

    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "sqr_type")
    private String sqrType;

}
