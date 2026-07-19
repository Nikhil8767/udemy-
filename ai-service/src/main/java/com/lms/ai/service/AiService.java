package com.lms.ai.service;

import com.lms.ai.dto.AiChatRequest;
import com.lms.ai.dto.AiChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class AiService {

    private final ChatClient chatClient;
    private final ConversationService conversationService;
    private final PromptTemplateService promptTemplateService;

    public AiService(ChatClient.Builder chatClientBuilder, ConversationService conversationService, PromptTemplateService promptTemplateService) {
        this.chatClient = chatClientBuilder.build();
        this.conversationService = conversationService;
        this.promptTemplateService = promptTemplateService;
    }

    public AiChatResponse processChatMessage(UUID userId, AiChatRequest request) {
        try {
            UserMessage userMessage = new UserMessage(request.getMessage());
            
            // Add user message to history
            conversationService.addMessage(userId, userMessage);
            
            // Build full prompt
            List<Message> allMessages = new ArrayList<>();
            allMessages.add(promptTemplateService.getSystemPrompt());
            allMessages.addAll(conversationService.getConversationHistory(userId));
            
            Prompt prompt = new Prompt(allMessages);
            
            // Call AI
            var response = chatClient.prompt(prompt).call().chatResponse();
            var aiMessage = response.getResult().getOutput();
            
            // Add AI response to history
            conversationService.addMessage(userId, aiMessage);
            
            return AiChatResponse.builder()
                    .response(aiMessage.getContent())
                    .build();
                    
        } catch (Exception e) {
            log.error("Error calling AI service for user: {}", userId, e);
            return AiChatResponse.builder()
                    .response("I'm temporarily unavailable. Please try again later.")
                    .build();
        }
    }
    
    public void clearConversation(UUID userId) {
        conversationService.clearConversation(userId);
    }
}
