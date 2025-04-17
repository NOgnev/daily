package com.klaxon.diary.controller;

import com.klaxon.diary.dto.Question;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.UUID.randomUUID;

@RestController
public class QuestionController {

    @GetMapping("/questions")
    public List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();
        questions.add(new Question(randomUUID(), "What is your name?"));
        questions.add(new Question(randomUUID(), "What is your email?"));
        questions.add(new Question(randomUUID(), "What is your phone number?"));
        questions.add(new Question(randomUUID(), "What is your address?"));
        questions.add(new Question(randomUUID(), "What is your occupation?"));
        return questions;
    }


    @PostMapping("/api/submit")
    public String submitForm(@RequestBody Map<String, String> formData) {
        // Здесь можно добавить логику обработки данных
        System.out.println("Received data: " + formData);
        return "Data received successfully!";
    }
}
