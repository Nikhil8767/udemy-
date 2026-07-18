package com.lms.content.repository;

import com.lms.content.entity.Lesson;
import com.lms.content.entity.Section;
import org.junit.jupiter.api.BeforeEach;
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
class LessonRepositoryTest {

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
    private LessonRepository lessonRepository;

    @Autowired
    private SectionRepository sectionRepository;

    private Section section;

    @BeforeEach
    void setUp() {
        sectionRepository.deleteAll();
        lessonRepository.deleteAll();

        section = Section.builder().courseId(UUID.randomUUID()).title("Section 1").displayOrder(1).build();
        section = sectionRepository.save(section);
    }

    @Test
    void findAllBySectionIdOrderByDisplayOrderAsc_ShouldReturnOrdered() {
        Lesson l1 = Lesson.builder().section(section).title("L1").displayOrder(2).build();
        Lesson l2 = Lesson.builder().section(section).title("L2").displayOrder(1).build();

        lessonRepository.saveAll(List.of(l1, l2));

        List<Lesson> results = lessonRepository.findAllBySectionIdOrderByDisplayOrderAsc(section.getId());

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getTitle()).isEqualTo("L2");
        assertThat(results.get(1).getTitle()).isEqualTo("L1");
    }

    @Test
    void existsBySectionIdAndDisplayOrder_ShouldCheckExistence() {
        Lesson l1 = Lesson.builder().section(section).title("L1").displayOrder(1).build();
        lessonRepository.save(l1);

        assertThat(lessonRepository.existsBySectionIdAndDisplayOrder(section.getId(), 1)).isTrue();
        assertThat(lessonRepository.existsBySectionIdAndDisplayOrder(section.getId(), 2)).isFalse();
    }
}
