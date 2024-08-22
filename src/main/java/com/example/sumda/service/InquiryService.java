package com.example.sumda.service;

import com.example.sumda.dto.inquiry.request.InquiryRequestDto;
import com.example.sumda.entity.Inquiry;
import com.example.sumda.repository.InquiryRepository;
import org.springframework.stereotype.Service;

@Service
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    public InquiryService(InquiryRepository inquiryRepository) {
        this.inquiryRepository = inquiryRepository;
    }

    public void createInquiry(InquiryRequestDto inquiryRequestDTO) {
        // DTO에서 엔티티로 변환
        Inquiry inquiry = inquiryRequestDTO.toEntity();
        // 저장
        inquiryRepository.save(inquiry);
    }

}