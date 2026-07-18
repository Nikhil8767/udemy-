package com.lms.frontend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.common.dto.response.ApiResponse;
import com.lms.frontend.client.AuthServiceClient;
import com.lms.frontend.dto.JwtResponse;
import com.lms.frontend.dto.LoginRequest;
import com.lms.frontend.dto.RegisterRequest;
import com.lms.frontend.dto.UserProfileResponse;
import com.lms.frontend.client.UserServiceClient;
import com.lms.frontend.security.JwtAuthenticationToken;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthServiceClient authServiceClient;
    private final UserServiceClient userServiceClient;

    @GetMapping("/login")
    public String showLoginForm(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        
        if (error != null && !model.containsAttribute("errorMessage")) {
            model.addAttribute("errorMessage", "Invalid username and password.");
        }
        if (logout != null && !model.containsAttribute("successMessage")) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequest());
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String performLogin(@Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
                               BindingResult result,
                               HttpServletRequest request,
                               HttpServletResponse httpResponse,
                               RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "auth/login";
        }

        try {
            log.info("=== Frontend: Calling authServiceClient.login() for email: {} ===", loginRequest.getEmail());
            ApiResponse<JwtResponse> response = authServiceClient.login(loginRequest);
            log.info("=== Frontend: Feign call returned. Response: success={}, message={}, hasData={} ===",
                    response != null ? response.isSuccess() : "null",
                    response != null ? response.getMessage() : "null",
                    response != null && response.getData() != null);
            
            if (response != null && response.isSuccess() && response.getData() != null) {
                JwtResponse jwtResponse = response.getData();
                log.info("=== Frontend: JWT received. Role={}, Email={}, TokenLength={} ===",
                        jwtResponse.getRole(), jwtResponse.getEmail(),
                        jwtResponse.getAccessToken() != null ? jwtResponse.getAccessToken().length() : 0);
                
                // Construct Authentication Token
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(jwtResponse.getRole());
                JwtAuthenticationToken auth = new JwtAuthenticationToken(
                        jwtResponse.getEmail(),
                        jwtResponse.getAccessToken(),
                        Collections.singletonList(authority)
                );

                // Save in SecurityContext
                SecurityContext sc = SecurityContextHolder.createEmptyContext();
                sc.setAuthentication(auth);
                SecurityContextHolder.setContext(sc);
                
                // Save context using SecurityContextRepository for Spring Security 6
                SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
                securityContextRepository.saveContext(sc, request, httpResponse);
                
                // Keep session attributes for easy Thymeleaf access
                HttpSession session = request.getSession(true);

                // Set session attributes for easy Thymeleaf access
                session.setAttribute("userEmail", jwtResponse.getEmail());
                session.setAttribute("userRole", jwtResponse.getRole());

                // Fetch Profile and set display attributes
                try {
                    ApiResponse<UserProfileResponse> profileRes = userServiceClient.getCurrentUserProfile();
                    if (profileRes != null && profileRes.isSuccess() && profileRes.getData() != null) {
                        UserProfileResponse p = profileRes.getData();
                        String displayName = p.getDisplayName() != null && !p.getDisplayName().isEmpty() ? p.getDisplayName() : 
                                             (p.getFirstName() != null ? p.getFirstName() + " " + p.getLastName() : "");
                        session.setAttribute("userDisplayName", displayName);
                        session.setAttribute("userProfileImageUrl", p.getProfileImageUrl());
                    }
                } catch (Exception ex) {
                    log.warn("Could not fetch profile during login for session caching.", ex);
                }

                // Redirect based on role
                String redirect = switch (jwtResponse.getRole()) {
                    case "ROLE_ADMIN" -> "redirect:/admin/dashboard";
                    case "ROLE_TUTOR" -> "redirect:/tutor/dashboard";
                    default -> "redirect:/student/dashboard";
                };
                log.info("=== Frontend: Login SUCCESS. Redirecting to: {} ===", redirect);
                return redirect;
            } else {
                log.warn("=== Frontend: Response was null, not successful, or missing data ===");
                redirectAttributes.addFlashAttribute("errorMessage", "Authentication failed.");
                return "redirect:/login";
            }
        } catch (FeignException e) {
            log.error("=== Frontend: Feign error. Status={}, Body={} ===", e.status(), e.contentUTF8());
            String errorMessage = extractErrorMessage(e);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            redirectAttributes.addFlashAttribute("loginRequest", loginRequest);
            return "redirect:/login";
        } catch (Exception e) {
            log.error("=== Frontend: Unexpected login error: {} ===", e.getClass().getName(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Service unavailable.");
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String performRegister(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        try {
            ApiResponse<Void> response = authServiceClient.register(registerRequest);
            if (response != null && response.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", "Registration completed successfully. Please sign in.");
                return "redirect:/login";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Registration failed. Please try again.");
                return "redirect:/register";
            }
        } catch (FeignException e) {
            log.error("Registration Feign error: {}", e.status());
            String errorMessage = extractErrorMessage(e);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            redirectAttributes.addFlashAttribute("registerRequest", registerRequest);
            return "redirect:/register";
        } catch (Exception e) {
            log.error("Registration error: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Service unavailable.");
            return "redirect:/register";
        }
    }

    private String extractErrorMessage(FeignException e) {
        String errorMessage = "An error occurred.";
        if (e.contentUTF8() != null && !e.contentUTF8().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(e.contentUTF8());
                if (root.has("message")) {
                    errorMessage = root.get("message").asText();
                }
            } catch (Exception ex) {
                log.error("Failed to parse FeignException body", ex);
            }
        }
        return errorMessage;
    }
}
