package com.example.sumda.repository;

import com.example.sumda.entity.UserSquirrel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSquirrelRepository extends JpaRepository<UserSquirrel, Long> {

    List<UserSquirrel> findByUserId(Long userId);

    // 유저가 현재 키우고 있는 다람쥐 정보
    Optional<UserSquirrel> findByUserIdAndEndDateIsNull(Long UserId);
}
