package com.example.sumda.controller;

import com.example.sumda.service.MsrstnListService;
import com.example.sumda.service.NearbyMsrstnListService;
import com.example.sumda.service.TMStdrCrdntService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {
    private final MsrstnListService msrstnListService;
    private final TMStdrCrdntService tmStdrCrdntService;
    private final NearbyMsrstnListService nearbyMsrstnListService;

    @GetMapping("/msrstn-list")
    public String getMsrstnList() {
        try {
            return msrstnListService.getMsrstnList();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/tm-crdnt")
    public String getTMStdrCrdnt(@RequestParam("umdName") String umdName) {
        // /tm-crdnt?umdName=혜화동
        try {
            return tmStdrCrdntService.getTMStdrCrdnt(umdName);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/nearbyMsrstn")
    public String getNearbyMsrstnList(@RequestParam("tmX") String tmX, @RequestParam("tmY") String tmY) {
        try {
            return nearbyMsrstnListService.getNearbyMsrstnList(tmX,tmY);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

}
