package com.example.sumda.repository;

import com.example.sumda.entity.GameType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameTypeRepository extends JpaRepository<GameType, Long> {
}
