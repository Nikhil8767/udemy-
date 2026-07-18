package com.lms.enrollment.repository;

import com.lms.enrollment.entity.Enrollment;
import com.lms.enrollment.entity.LessonProgress;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LessonProgressRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("lms_enrollment")
            .withUsername("postgres")
            .withPassword("password");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private LessonProgressRepository lessonProgressRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Test
    void findByEnrollmentIdAndLessonId_ShouldReturnProgress() {
        Enrollment enrollment = Enrollment.builder()
                .studentId(UUID.randomUUID())
                .courseId(UUID.randomUUID())
                .build();
        enrollment = enrollmentRepository.save(enrollment);

        UUID lessonId = UUID.randomUUID();
        LessonProgress progress = LessonProgress.builder()
                .enrollment(enrollment)
                .lessonId(lessonId)
                .completed(true)
                .build();
        lessonProgressRepository.save(progress);

        Optional<LessonProgress> result = lessonProgressRepository.findByEnrollmentIdAndLessonId(enrollment.getId(), lessonId);

        assertThat(result).isPresent();
        assertThat(result.get().isCompleted()).isTrue();
    }
}
