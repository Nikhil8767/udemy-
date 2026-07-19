package com.lms.user.service.impl;

import com.lms.user.dto.request.SettingsUpdateRequest;
import com.lms.user.entity.SystemSetting;
import com.lms.user.repository.SystemSettingRepository;
import com.lms.user.service.SystemSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemSettingServiceImpl implements SystemSettingService {

    private final SystemSettingRepository settingRepository;

    @PostConstruct
    public void init() {
        initializeDefaultSettings();
    }

    @Override
    public Map<String, String> getAllSettings() {
        return settingRepository.findAll().stream()
                .collect(Collectors.toMap(SystemSetting::getKey, SystemSetting::getValue));
    }

    @Override
    @Transactional
    public Map<String, String> updateSettings(SettingsUpdateRequest request) {
        if (request.getSettings() != null) {
            request.getSettings().forEach((key, value) -> {
                SystemSetting setting = settingRepository.findByKey(key)
                        .orElseGet(() -> SystemSetting.builder().key(key).build());
                setting.setValue(value);
                settingRepository.save(setting);
            });
        }
        return getAllSettings();
    }

    @Override
    @Transactional
    public void initializeDefaultSettings() {
        Map<String, String> defaults = new HashMap<>();
        defaults.put("platform_name", "Enterprise LMS");
        defaults.put("support_email", "support@enterpriselms.com");
        defaults.put("default_language", "en");
        defaults.put("pagination_size", "10");
        defaults.put("maintenance_mode", "false");
        defaults.put("student_registration_enabled", "true");
        defaults.put("tutor_registration_enabled", "true");
        defaults.put("course_approval_required", "true");

        defaults.forEach((key, value) -> {
            if (settingRepository.findByKey(key).isEmpty()) {
                settingRepository.save(SystemSetting.builder()
                        .key(key)
                        .value(value)
                        .description("Default setting for " + key)
                        .build());
            }
        });
    }
}
