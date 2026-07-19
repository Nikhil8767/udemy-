package com.lms.user.dto.request;

import lombok.Data;
import java.util.Map;

@Data
public class SettingsUpdateRequest {
    private Map<String, String> settings;
}
