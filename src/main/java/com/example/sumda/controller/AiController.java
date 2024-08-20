package com.example.sumda.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AiController {

    private final ChatClient chatClient;

    public AiController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }


    @GetMapping("/ai/simple")
    public Map<String, String> completion(@RequestParam(value = "message") String message) {
        System.out.println("message: " + message);
        return Map.of("completion", chatClient.prompt().user("한국어로"+ message).call().content());
    }
}
