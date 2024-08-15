package com.example.sumda.controller;

import com.example.sumda.DTO.StationDTO;
import com.example.sumda.DTO.TMDTO;
import com.example.sumda.service.MsrstnListService;
import com.example.sumda.service.NearbyMsrstnListService;
import com.example.sumda.service.TMStdrCrdntService;
import com.example.sumda.service.StationService;
import com.example.sumda.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stations")
public class StationController {
    private final MsrstnListService msrstnListService;
    private final TMStdrCrdntService tmStdrCrdntService;
    private final NearbyMsrstnListService nearbyMsrstnListService;
    private final StationService stationService;

    @GetMapping
    public ResponseEntity<?> getAllStation(@RequestParam String stationName, @RequestParam int page, @RequestParam int size) {
        return ResponseUtils.createResponse(HttpStatus.OK, "Success", stationService.getStationContains(stationName, page, size));
    }

    // 측정소 리스트 확인
    @GetMapping("/msrstn-list")
    public ResponseEntity<List<StationDTO>> getMsrstnList() {
        try {
            List<StationDTO> stationList = msrstnListService.getMsrstnList();
            return ResponseEntity.ok(stationList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // '동'으로 검색해서 tm 값 확인
    @GetMapping("/tm-crdnt")
    public ResponseEntity<List<TMDTO>> getTMStdrCrdnt(@RequestParam("umdName") String umdName) {
        // /tm-crdnt?umdName=혜화동
        try {
            List<TMDTO> tmList = tmStdrCrdntService.getTMStdrCrdnt(umdName);
            return ResponseEntity.ok(tmList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // 근처 측정소 tm 값으로 확인
    @GetMapping("/nearbyMsrstn")
    public ResponseEntity<List<StationDTO>> getNearbyMsrstnList(@RequestParam("tmX") Double tmX, @RequestParam("tmY") Double tmY) {
        try {
            List<StationDTO> stationList = nearbyMsrstnListService.getNearbyMsrstnList(tmX,tmY);
            return ResponseEntity.ok(stationList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

}
