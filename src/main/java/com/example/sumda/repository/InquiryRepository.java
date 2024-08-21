package com.example.sumda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sumda.entity.Inquiry;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

}
