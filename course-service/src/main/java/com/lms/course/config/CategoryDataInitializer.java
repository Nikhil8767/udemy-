package com.lms.course.config;

import com.lms.course.entity.Category;
import com.lms.course.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CategoryDataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            log.info("No categories found. Initializing default categories...");
            List<Category> defaultCategories = List.of(
                    Category.builder().name("Programming & Tech").description("Software development, IT, and emerging tech").isActive(true).build(),
                    Category.builder().name("Business & Finance").description("Finance, accounting, and entrepreneurship").isActive(true).build(),
                    Category.builder().name("Design & UX").description("Graphic design, user experience, and illustration").isActive(true).build(),
                    Category.builder().name("Marketing").description("Digital marketing, SEO, and social media").isActive(true).build()
            );
            categoryRepository.saveAll(defaultCategories);
            log.info("Successfully initialized {} default categories.", defaultCategories.size());
        }
    }
}
