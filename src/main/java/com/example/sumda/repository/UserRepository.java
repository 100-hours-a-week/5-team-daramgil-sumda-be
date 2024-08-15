package com.example.sumda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sumda.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
}
