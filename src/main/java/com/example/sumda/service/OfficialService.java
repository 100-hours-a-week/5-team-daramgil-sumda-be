package com.example.sumda.service;

import com.example.sumda.entity.Official;
import com.example.sumda.exception.CustomException;
import com.example.sumda.exception.ErrorCode;
import com.example.sumda.repository.OfficialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OfficialService {

    @Autowired
    private OfficialRepository officialRepository;

    // 공지사항 목록 가져오기
    public List<Official> getAllOfficials() {
        try {
            List<Official> officials = officialRepository.findAll();

            if (officials.isEmpty()) {
                // 데이터가 없으면 404 Not Found로 처리
                throw new CustomException(ErrorCode.OFFICIAL_NOT_FOUND);
            }

            return officials;  // 200 OK
        } catch (Exception e) {
            // 서버 오류 시 500 Internal Server Error
            throw new CustomException(ErrorCode.SEVER_ERROR);
        }
    }


    // TODO: 반환형 전부 DTO 형태로 변경
//    // 특정 공지사항 ID로 가져오기
//    public ResponseEntity<Official> getOfficialById(Long id) {
//        try {
//            Optional<Official> official = officialRepository.findById(id);
//
//            if (!official.isPresent()) {
//                // 요청한 데이터가 없으면 404 Not Found
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(null);
//            }
//
//            return ResponseEntity.ok(official.get());  // 200 OK
//        } catch (Exception e) {
//            // 서버 오류 시 500 Internal Server Error
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(null);
//        }
//    }
//
//    // 공지사항 생성하기
//    public ResponseEntity<Official> createOfficial(Official official) {
//        try {
//            official.setCreatedAt(LocalDateTime.now());
//            official.setUpdatedAt(LocalDateTime.now());
//            Official savedOfficial = officialRepository.save(official);
//
//            return ResponseEntity.status(HttpStatus.CREATED).body(savedOfficial);  // 201 Created
//        } catch (Exception e) {
//            // 서버 오류 시 500 Internal Server Error
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(null);
//        }
//    }
//
//    // 공지사항 수정하기
//    public ResponseEntity<Official> updateOfficial(Long id, Official officialDetails) {
//        try {
//            Optional<Official> official = officialRepository.findById(id);
//
//            if (!official.isPresent()) {
//                // 요청한 데이터가 없으면 404 Not Found
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(null);
//            }
//
//            Official existingOfficial = official.get();
//            existingOfficial.setTitle(officialDetails.getTitle());
//            existingOfficial.setContent(officialDetails.getContent());
//            existingOfficial.setUpdatedAt(LocalDateTime.now());
//
//            Official updatedOfficial = officialRepository.save(existingOfficial);
//            return ResponseEntity.ok(updatedOfficial);  // 200 OK
//        } catch (Exception e) {
//            // 서버 오류 시 500 Internal Server Error
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(null);
//        }
//    }
//
//    // 공지사항 삭제하기
//    public ResponseEntity<Void> deleteOfficial(Long id) {
//        try {
//            Optional<Official> official = officialRepository.findById(id);
//
//            if (!official.isPresent()) {
//                // 요청한 데이터가 없으면 404 Not Found
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .build();
//            }
//
//            officialRepository.deleteById(id);
//            return ResponseEntity.noContent().build();  // 204 No Content
//        } catch (Exception e) {
//            // 서버 오류 시 500 Internal Server Error
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .build();
//        }
//    }
}