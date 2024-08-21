package com.example.sumda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sumda.entity.Official;
import org.springframework.stereotype.Repository;

@Repository
public interface OfficialRepository extends JpaRepository<Official, Long>{
}

