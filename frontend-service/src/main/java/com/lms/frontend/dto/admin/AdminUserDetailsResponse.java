package com.lms.frontend.dto.admin;

import lombok.Data;
import java.util.UUID;

@Data
public class AdminUserDetailsResponse {
    private UUID authUserId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profileImageUrl;
    private String country;
    private String state;
    private String city;
}
