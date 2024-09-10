package com.example.sumda.controller;

import com.example.sumda.entity.Quiz;
import com.example.sumda.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/oxgames")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping
    public Quiz getRandomQuiz() {
        return quizService.getRandomQuiz();
    }
}