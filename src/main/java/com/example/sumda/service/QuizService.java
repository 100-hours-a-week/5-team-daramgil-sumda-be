package com.example.sumda.service;

import com.example.sumda.entity.Quiz;
import com.example.sumda.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    public Quiz getRandomQuiz() {
        List<Quiz> quizzes = quizRepository.findAll();
        if (quizzes.isEmpty()) {
            throw new RuntimeException("퀴즈 데이터가 없습니다.");
        }
        Random random = new Random();
        return quizzes.get(random.nextInt(quizzes.size()));
    }
}
