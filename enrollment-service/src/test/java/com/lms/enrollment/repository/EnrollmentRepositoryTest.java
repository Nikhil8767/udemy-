package com.lms.enrollment.repository;

import com.lms.enrollment.entity.Enrollment;
import com.lms.enrollment.enums.EnrollmentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EnrollmentRepositoryTest {

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
    private EnrollmentRepository repository;

    @Test
    void searchEnrollments_ShouldFilterByStatus() {
        Enrollment e1 = Enrollment.builder()
                .studentId(UUID.randomUUID())
                .courseId(UUID.randomUUID())
                .status(EnrollmentStatus.ENROLLED)
                .build();
        Enrollment e2 = Enrollment.builder()
                .studentId(UUID.randomUUID())
                .courseId(UUID.randomUUID())
                .status(EnrollmentStatus.COMPLETED)
                .build();
        
        repository.save(e1);
        repository.save(e2);

        Page<Enrollment> result = repository.searchEnrollments(EnrollmentStatus.ENROLLED, PageRequest.of(0, 10));
        
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(EnrollmentStatus.ENROLLED);
    }

    @Test
    void countByStatusIn_ShouldReturnCorrectCount() {
        Enrollment e1 = Enrollment.builder()
                .studentId(UUID.randomUUID())
                .courseId(UUID.randomUUID())
                .status(EnrollmentStatus.ENROLLED)
                .build();
        Enrollment e2 = Enrollment.builder()
                .studentId(UUID.randomUUID())
                .courseId(UUID.randomUUID())
                .status(EnrollmentStatus.COMPLETED)
                .build();
        Enrollment e3 = Enrollment.builder()
                .studentId(UUID.randomUUID())
                .courseId(UUID.randomUUID())
                .status(EnrollmentStatus.DROPPED)
                .build();
        
        repository.saveAll(List.of(e1, e2, e3));

        long count = repository.countByStatusIn(List.of(EnrollmentStatus.ENROLLED, EnrollmentStatus.COMPLETED));
        
        assertThat(count).isEqualTo(2);
    }
}
