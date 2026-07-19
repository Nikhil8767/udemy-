package com.lms.ai.controller;

import com.lms.ai.dto.AiChatRequest;
import com.lms.ai.dto.AiChatResponse;
import com.lms.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody AiChatRequest request) {
        
        AiChatResponse response = aiService.processChatMessage(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/chat/history")
    public ResponseEntity<Void> clearHistory(@RequestHeader("X-User-Id") UUID userId) {
        aiService.clearConversation(userId);
        return ResponseEntity.ok().build();
    }
}
