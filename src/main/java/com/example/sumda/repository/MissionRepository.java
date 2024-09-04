package com.example.sumda.repository;

import com.example.sumda.entity.Missions;
import com.example.sumda.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface MissionRepository extends JpaRepository<Missions, Long> {

    Optional<Missions> findByUserIdAndDate(User user, LocalDate date);

}
