package com.example.sumda.repository;

import com.example.sumda.entity.GameLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameLogRepository extends JpaRepository<GameLog, Long> {

    //특정 유저의 게임결과 중 가장 높은 점수 찾기
    @Query("SELECT g FROM GameLog g WHERE g.user.id = :userId AND g.gameType.id = :gameTypeId ORDER BY g.score DESC")
    List<GameLog> findTopByUserIdAndGameTypeIdOrderByScoreDesc(@Param("userId") Long userId, @Param("gameTypeId") Long gameTypeId);
}
