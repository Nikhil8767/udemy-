package com.lms.frontend.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.frontend.client.UserServiceClient;
import com.lms.frontend.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.lms.frontend.dto.ProfileRequest;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final UserServiceClient userServiceClient;

    @GetMapping
    public String viewProfile(Model model) {
        try {
            ApiResponse<UserProfileResponse> response = userServiceClient.getCurrentUserProfile();
            if (response != null && response.isSuccess() && response.getData() != null) {
                model.addAttribute("profile", response.getData());
            } else {
                model.addAttribute("errorMessage", "Could not load profile data.");
            }
        } catch (feign.FeignException.NotFound e) {
            model.addAttribute("profile", new UserProfileResponse());
        } catch (Exception e) {
            log.error("Error fetching profile", e);
            model.addAttribute("errorMessage", "Service unavailable.");
        }
        return "profile/view";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        try {
            ApiResponse<UserProfileResponse> response = userServiceClient.getCurrentUserProfile();
            if (response != null && response.isSuccess() && response.getData() != null) {
                model.addAttribute("profile", response.getData());
            } else {
                model.addAttribute("errorMessage", "Could not load settings.");
            }
        } catch (feign.FeignException.NotFound e) {
            model.addAttribute("profile", new UserProfileResponse());
        } catch (Exception e) {
            log.error("Error fetching settings", e);
            model.addAttribute("errorMessage", "Service unavailable.");
        }
        return "profile/settings";
    }

    @PostMapping("/settings")
    public String updateProfile(@Valid @ModelAttribute("profile") ProfileRequest profileRequest,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest request,
                               org.springframework.security.core.Authentication authentication) {
        if (result.hasErrors()) {
            return "profile/settings";
        }
        
        String redirectUrl = "redirect:/student/dashboard";
        if (authentication != null) {
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                redirectUrl = "redirect:/admin/dashboard";
            } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TUTOR"))) {
                redirectUrl = "redirect:/tutor/dashboard";
            }
        }
        
        try {
            ApiResponse<Void> response = userServiceClient.updateMyProfile(profileRequest);
            if (response != null && response.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
                
                // Update Session
                HttpSession session = request.getSession(false);
                if (session != null) {
                    String displayName = (profileRequest.getDisplayName() != null && !profileRequest.getDisplayName().isEmpty()) ? profileRequest.getDisplayName() : 
                                         (profileRequest.getFirstName() != null ? profileRequest.getFirstName() + " " + profileRequest.getLastName() : "");
                    session.setAttribute("userDisplayName", displayName);
                    session.setAttribute("userProfileImageUrl", profileRequest.getProfileImageUrl());
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile.");
            }
        } catch (FeignException e) {
            log.error("Feign Error updating profile: {}", e.contentUTF8());
            redirectAttributes.addFlashAttribute("errorMessage", extractErrorMessage(e, "Failed to update profile. Please check your inputs."));
        } catch (Exception e) {
            log.error("Error updating profile", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Service unavailable.");
            return "redirect:/profile/settings";
        }
        return redirectUrl;
    }
    
    private String extractErrorMessage(FeignException e, String defaultMessage) {
        try {
            com.fasterxml.jackson.databind.JsonNode root = new com.fasterxml.jackson.databind.ObjectMapper().readTree(e.contentUTF8());
            if (root.has("message")) {
                return root.get("message").asText();
            }
        } catch (Exception ex) {
            log.warn("Could not parse Feign exception body", ex);
        }
        return defaultMessage;
    }
}
