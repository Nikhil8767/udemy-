package com.lms.user.repository;

import com.lms.user.entity.UserProfile;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserProfileRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("lms_user")
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
    private UserProfileRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldFindByAuthUserId() {
        UUID authUserId = UUID.randomUUID();
        UserProfile profile = UserProfile.builder()
                .authUserId(authUserId)
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("1234567890")
                .build();
        repository.save(profile);

        Optional<UserProfile> found = repository.findByAuthUserId(authUserId);
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
    }

    @Test
    void shouldCheckExistsByAuthUserId() {
        UUID authUserId = UUID.randomUUID();
        UserProfile profile = UserProfile.builder()
                .authUserId(authUserId)
                .firstName("John")
                .lastName("Doe")
                .build();
        repository.save(profile);

        assertThat(repository.existsByAuthUserId(authUserId)).isTrue();
        assertThat(repository.existsByAuthUserId(UUID.randomUUID())).isFalse();
    }

    @Test
    void shouldCheckExistsByPhoneNumber() {
        UserProfile profile = UserProfile.builder()
                .authUserId(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("1234567890")
                .build();
        repository.save(profile);

        assertThat(repository.existsByPhoneNumber("1234567890")).isTrue();
        assertThat(repository.existsByPhoneNumber("0987654321")).isFalse();
    }

    @Test
    void shouldCheckExistsByPhoneNumberAndAuthUserIdNot() {
        UUID authId1 = UUID.randomUUID();
        UUID authId2 = UUID.randomUUID();

        UserProfile profile = UserProfile.builder()
                .authUserId(authId1)
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("1234567890")
                .build();
        repository.save(profile);

        assertThat(repository.existsByPhoneNumberAndAuthUserIdNot("1234567890", authId2)).isTrue();
        assertThat(repository.existsByPhoneNumberAndAuthUserIdNot("1234567890", authId1)).isFalse();
    }

    @Test
    void shouldFindAllByAuthUserIdIn() {
        UUID authId1 = UUID.randomUUID();
        UUID authId2 = UUID.randomUUID();

        UserProfile profile1 = UserProfile.builder().authUserId(authId1).firstName("John").lastName("Doe").build();
        UserProfile profile2 = UserProfile.builder().authUserId(authId2).firstName("Jane").lastName("Doe").build();
        
        repository.saveAll(List.of(profile1, profile2));

        List<UserProfile> found = repository.findAllByAuthUserIdIn(List.of(authId1, authId2));
        assertThat(found).hasSize(2);
    }
}
