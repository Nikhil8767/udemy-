package com.lms.common.constant;

public final class AppConstants {
    private AppConstants() {}

    // API Paths
    public static final String API_V1 = "/api/v1";
    public static final String AUTH_API = API_V1 + "/auth";
    public static final String USERS_API = API_V1 + "/users";
    public static final String COURSES_API = API_V1 + "/courses";

    // Security Constants
    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    // Validation Constants
    public static final String PHONE_REGEX = "^\\+?[1-9]\\d{6,14}$";
    public static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
}
