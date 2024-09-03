package com.example.sumda.repository;

import com.example.sumda.entity.SquirrelType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SquirrelTypeRepository extends JpaRepository<SquirrelType, Long> {

    Optional<SquirrelType> findBySqrType(String sqrType);

}
