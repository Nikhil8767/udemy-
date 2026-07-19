package com.lms.user.service;

import com.lms.user.dto.request.SettingsUpdateRequest;
import com.lms.user.entity.SystemSetting;
import java.util.Map;

public interface SystemSettingService {
    Map<String, String> getAllSettings();
    Map<String, String> updateSettings(SettingsUpdateRequest request);
    void initializeDefaultSettings();
}
