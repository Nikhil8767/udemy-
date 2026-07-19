package com.lms.ai.service;

import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    private final ConcurrentHashMap<UUID, Deque<Message>> userConversations = new ConcurrentHashMap<>();
    private static final int MAX_MESSAGES = 20;

    public void addMessage(UUID userId, Message message) {
        userConversations.compute(userId, (key, deque) -> {
            if (deque == null) {
                deque = new ConcurrentLinkedDeque<>();
            }
            deque.addLast(message);
            if (deque.size() > MAX_MESSAGES) {
                deque.pollFirst();
            }
            return deque;
        });
    }

    public List<Message> getConversationHistory(UUID userId) {
        Deque<Message> deque = userConversations.get(userId);
        if (deque == null) {
            return List.of();
        }
        return deque.stream().collect(Collectors.toList());
    }

    public void clearConversation(UUID userId) {
        userConversations.remove(userId);
    }
}
