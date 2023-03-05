package com.jp.pssior.assignment.repo;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@AutoConfigureTestDatabase
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:show_book_test;DB_CLOSE_ON_EXIT=TRUE",
        "spring.flyway.enabled=true"
})
public class RepositoryTest {
}
