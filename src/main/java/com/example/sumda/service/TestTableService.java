package com.example.sumda.service;


import com.example.sumda.entity.TestTable;
import com.example.sumda.repository.TestTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestTableService {

    private final TestTableRepository testTableRepository;

    @Autowired
    public TestTableService(TestTableRepository testTableRepository) {
        this.testTableRepository = testTableRepository;
    }

    public List<TestTable> findAll() {
        return testTableRepository.findAll();
    }
}
