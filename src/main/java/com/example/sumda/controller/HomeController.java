package com.example.sumda.controller;

import com.example.sumda.entity.TestTable;
import com.example.sumda.service.TestTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class HomeController {
    private final TestTableService testTableService;

    @GetMapping("/api")
    public String home() {
        return "Hello, Spring Boot!";
    }

    @GetMapping("/api/test")
    public String test() {
        List<TestTable> tables = testTableService.findAll();

        String tablesName = tables.stream()
                .map(TestTable::getName)
                .reduce("", (a, b) -> a + b + " ");

        return tablesName;
    }


}
