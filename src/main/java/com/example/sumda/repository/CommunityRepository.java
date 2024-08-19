package com.example.sumda.repository;

import com.example.sumda.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityRepository extends JpaRepository<Community, String> {
    List<Community> findAll();

}
