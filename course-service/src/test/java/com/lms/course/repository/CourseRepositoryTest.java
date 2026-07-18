package com.lms.course.repository;

import com.lms.common.enums.CourseLevel;
import com.lms.common.enums.CourseStatus;
import com.lms.course.entity.Category;
import com.lms.course.entity.Course;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CourseRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("lms_course")
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
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;
    private UUID instructorId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        categoryRepository.deleteAll();

        category = new Category();
        category.setName("Programming");
        category = categoryRepository.save(category);
    }

    @Test
    void shouldCheckExistsByTitle() {
        Course course = Course.builder()
                .title("Java 21")
                .courseLevel(CourseLevel.BEGINNER)
                .category(category)
                .courseStatus(CourseStatus.DRAFT)
                .instructorId(instructorId)
                .createdBy(instructorId)
                .updatedBy(instructorId)
                .build();
        courseRepository.save(course);

        assertThat(courseRepository.existsByTitle("Java 21")).isTrue();
        assertThat(courseRepository.existsByTitle("Python")).isFalse();
    }

    @Test
    void searchCourses_ShouldFilterByStatusAndCategory() {
        Course c1 = Course.builder().title("C1").category(category).courseLevel(CourseLevel.BEGINNER).courseStatus(CourseStatus.PUBLISHED).instructorId(instructorId).createdBy(instructorId).updatedBy(instructorId).build();
        Course c2 = Course.builder().title("C2").category(category).courseLevel(CourseLevel.INTERMEDIATE).courseStatus(CourseStatus.DRAFT).instructorId(instructorId).createdBy(instructorId).updatedBy(instructorId).build();
        courseRepository.save(c1);
        courseRepository.save(c2);

        Page<Course> result = courseRepository.searchCourses(CourseStatus.PUBLISHED, category.getId(), null, PageRequest.of(0, 10));
        
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("C1");
    }
}
