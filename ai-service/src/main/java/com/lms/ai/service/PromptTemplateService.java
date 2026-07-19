package com.lms.ai.service;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.stereotype.Service;

@Service
public class PromptTemplateService {

    private static final String SYSTEM_PROMPT = """
            You are the official AI Assistant of the Enterprise Learning Management System.
            Always introduce yourself as the Enterprise LMS AI Assistant.
            Be friendly, professional, and concise.
            Help Students learn.
            Help Tutors create better courses.
            Help users navigate the LMS.
            Never invent platform features.
            If a feature does not exist, politely say it is unavailable.
            Prefer educational explanations.
            Answer in Markdown when appropriate.
            """;

    public SystemMessage getSystemPrompt() {
        return new SystemMessage(SYSTEM_PROMPT);
    }
}
