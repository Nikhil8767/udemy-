package com.lms.auth.repository;

import com.lms.auth.entity.UserCredential;
import com.lms.common.enums.AccountStatus;
import com.lms.common.enums.Role;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
class UserCredentialRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("lms_auth")
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
    private UserCredentialRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldFindUserByEmail() {
        UserCredential user = UserCredential.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_STUDENT)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        repository.save(user);

        assertThat(repository.findByEmail("test@example.com")).isPresent();
        assertThat(repository.existsByEmail("test@example.com")).isTrue();
    }

    @Test
    void shouldCountByRoleAndStatus() {
        UserCredential student = UserCredential.builder().email("s@test.com").password("pass").role(Role.ROLE_STUDENT).accountStatus(AccountStatus.ACTIVE).build();
        UserCredential tutor = UserCredential.builder().email("t@test.com").password("pass").role(Role.ROLE_TUTOR).accountStatus(AccountStatus.PENDING).build();
        repository.save(student);
        repository.save(tutor);

        assertThat(repository.countByRole(Role.ROLE_STUDENT)).isEqualTo(1);
        assertThat(repository.countByRole(Role.ROLE_TUTOR)).isEqualTo(1);
        assertThat(repository.countByAccountStatus(AccountStatus.PENDING)).isEqualTo(1);
    }

    @Test
    void shouldFindIdsByRoleAndStatusWithPagination() {
        UserCredential student1 = UserCredential.builder().email("s1@test.com").password("pass").role(Role.ROLE_STUDENT).accountStatus(AccountStatus.ACTIVE).build();
        UserCredential student2 = UserCredential.builder().email("s2@test.com").password("pass").role(Role.ROLE_STUDENT).accountStatus(AccountStatus.ACTIVE).build();
        UserCredential student3 = UserCredential.builder().email("s3@test.com").password("pass").role(Role.ROLE_STUDENT).accountStatus(AccountStatus.SUSPENDED).build();
        
        student1 = repository.save(student1);
        student2 = repository.save(student2);
        repository.save(student3);

        Page<UUID> result = repository.findIdsByRoleAndStatus(Role.ROLE_STUDENT, AccountStatus.ACTIVE, PageRequest.of(0, 10));
        
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).containsExactlyInAnyOrder(student1.getId(), student2.getId());
    }
}
