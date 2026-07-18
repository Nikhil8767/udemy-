package com.lms.content.repository;

import com.lms.content.entity.Lesson;
import com.lms.content.entity.Resource;
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
class ResourceRepositoryTest {

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
    private ResourceRepository resourceRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private SectionRepository sectionRepository;

    private Lesson lesson;

    @BeforeEach
    void setUp() {
        resourceRepository.deleteAll();
        lessonRepository.deleteAll();
        sectionRepository.deleteAll();

        Section section = Section.builder().courseId(UUID.randomUUID()).title("S").displayOrder(1).build();
        section = sectionRepository.save(section);

        lesson = Lesson.builder().section(section).title("L").displayOrder(1).build();
        lesson = lessonRepository.save(lesson);
    }

    @Test
    void findAllByLessonId_ShouldReturnList() {
        Resource r1 = Resource.builder().lesson(lesson).title("R1").fileUrl("url").resourceType("pdf").build();
        resourceRepository.save(r1);

        List<Resource> results = resourceRepository.findAllByLessonId(lesson.getId());

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("R1");
    }
}
