package com.lms.common.handler;

import com.lms.common.exception.*;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GlobalExceptionHandlerTest.DummyController.class)
@ContextConfiguration(classes = {GlobalExceptionHandler.class, GlobalExceptionHandlerTest.DummyController.class})
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @RestController
    static class DummyController {
        @GetMapping("/not-found")
        public void throwNotFound() { throw new ResourceNotFoundException("Not found"); }

        @GetMapping("/duplicate")
        public void throwDuplicate() { throw new DuplicateResourceException("Duplicate"); }

        @GetMapping("/business")
        public void throwBusiness() { throw new BusinessException("Business error"); }

        @GetMapping("/unauthorized")
        public void throwUnauthorized() { throw new UnauthorizedException("Unauthorized"); }

        @GetMapping("/forbidden")
        public void throwForbidden() { throw new ForbiddenException("Forbidden"); }

        @GetMapping("/illegal")
        public void throwIllegal() { throw new IllegalArgumentException("Illegal argument"); }

        @GetMapping("/validation")
        public void throwValidation() { throw new ValidationException("Validation failed"); }

        @GetMapping("/generic")
        public void throwGeneric() { throw new RuntimeException("Generic error"); }
    }

    @Test
    void shouldHandleResourceNotFoundException() throws Exception {
        mockMvc.perform(get("/not-found").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Not found"));
    }

    @Test
    void shouldHandleDuplicateResourceException() throws Exception {
        mockMvc.perform(get("/duplicate").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Duplicate"));
    }

    @Test
    void shouldHandleBusinessException() throws Exception {
        mockMvc.perform(get("/business").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Business error"));
    }

    @Test
    void shouldHandleUnauthorizedException() throws Exception {
        mockMvc.perform(get("/unauthorized").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized"));
    }

    @Test
    void shouldHandleForbiddenException() throws Exception {
        mockMvc.perform(get("/forbidden").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Forbidden"));
    }

    @Test
    void shouldHandleIllegalArgumentException() throws Exception {
        mockMvc.perform(get("/illegal").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Illegal argument"));
    }

    @Test
    void shouldHandleValidationException() throws Exception {
        mockMvc.perform(get("/validation").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void shouldHandleGenericException() throws Exception {
        mockMvc.perform(get("/generic").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred: Generic error"));
    }
}
