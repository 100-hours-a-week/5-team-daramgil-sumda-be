package com.example.sumda.repository;

import com.example.sumda.entity.GameLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameLogRepository extends JpaRepository<GameLog, Long> {
}
