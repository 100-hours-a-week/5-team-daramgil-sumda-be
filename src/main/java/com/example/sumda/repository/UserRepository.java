package com.example.sumda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sumda.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
}
