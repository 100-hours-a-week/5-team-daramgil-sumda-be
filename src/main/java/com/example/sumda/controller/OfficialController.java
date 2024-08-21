package com.example.sumda.controller;

import com.example.sumda.entity.Official;
import com.example.sumda.service.OfficialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/official")
public class OfficialController {

    @Autowired
    private OfficialService officialService;

    // 공지사항 목록 가져오기
    @GetMapping
    public ResponseEntity<List<Official>> getAllOfficials() {
        return officialService.getAllOfficials();
    }

    // 특정 공지사항 ID로 가져오기
    @GetMapping("/{id}")
    public ResponseEntity<Official> getOfficialById(@PathVariable Long id) {
        return officialService.getOfficialById(id);
    }

    // 공지사항 생성하기
    @PostMapping
    public ResponseEntity<Official> createOfficial(@RequestBody Official official) {
        return officialService.createOfficial(official);
    }

    // 공지사항 수정하기
    @PutMapping("/{id}")
    public ResponseEntity<Official> updateOfficial(@PathVariable Long id, @RequestBody Official officialDetails) {
        return officialService.updateOfficial(id, officialDetails);
    }

    // 공지사항 삭제하기
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOfficial(@PathVariable Long id) {
        return officialService.deleteOfficial(id);
    }
}
