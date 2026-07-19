package com.lms.frontend.client;

import com.lms.frontend.dto.ai.AiChatRequest;
import com.lms.frontend.dto.ai.AiChatResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "api-gateway", contextId = "aiServiceClient", path = "/api/v1/ai")
public interface AiServiceClient {

    @PostMapping("/chat")
    AiChatResponse chat(@RequestBody AiChatRequest request);

    @DeleteMapping("/chat/history")
    void clearHistory();
}
