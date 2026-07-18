package com.lms.content.repository;

import com.lms.content.entity.Section;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
class SectionRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("lms_content")
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
    private SectionRepository repository;

    @Test
    void findAllByCourseIdOrderByDisplayOrderAsc_ShouldReturnOrderedList() {
        UUID courseId = UUID.randomUUID();
        Section s1 = Section.builder().courseId(courseId).title("S1").displayOrder(2).build();
        Section s2 = Section.builder().courseId(courseId).title("S2").displayOrder(1).build();
        
        repository.saveAll(List.of(s1, s2));

        List<Section> results = repository.findAllByCourseIdOrderByDisplayOrderAsc(courseId);
        
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getTitle()).isEqualTo("S2"); // order 1
        assertThat(results.get(1).getTitle()).isEqualTo("S1"); // order 2
    }

    @Test
    void existsByCourseIdAndDisplayOrder_ShouldCheckExistence() {
        UUID courseId = UUID.randomUUID();
        Section section = Section.builder().courseId(courseId).title("S1").displayOrder(1).build();
        repository.save(section);

        assertThat(repository.existsByCourseIdAndDisplayOrder(courseId, 1)).isTrue();
        assertThat(repository.existsByCourseIdAndDisplayOrder(courseId, 2)).isFalse();
    }
}
