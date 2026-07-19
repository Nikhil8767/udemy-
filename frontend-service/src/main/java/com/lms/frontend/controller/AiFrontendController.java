package com.lms.frontend.controller;

import com.lms.frontend.client.AiServiceClient;
import com.lms.frontend.dto.ai.AiChatRequest;
import com.lms.frontend.dto.ai.AiChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Slf4j
public class AiFrontendController {

    private final AiServiceClient aiServiceClient;

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(@RequestBody AiChatRequest request) {
        try {
            AiChatResponse response = aiServiceClient.chat(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to communicate with AI Service", e);
            return ResponseEntity.ok(AiChatResponse.builder()
                    .response("I'm temporarily unavailable. Please try again later.")
                    .build());
        }
    }

    @DeleteMapping("/chat/history")
    public ResponseEntity<Void> clearHistory() {
        try {
            aiServiceClient.clearHistory();
        } catch (Exception e) {
            log.error("Failed to clear AI history", e);
        }
        return ResponseEntity.ok().build();
    }
}
