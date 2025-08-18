package com.example.userservice;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserDaoImplIT {

    private static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("postgres");

    private SessionFactory sessionFactory;
    private UserDao userDao;

    @BeforeAll
    void setupContainer() {
        postgresContainer.start();
        HibernateTestUtil.configure(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword()
        );
        sessionFactory = HibernateTestUtil.getSessionFactory();
        userDao = new UserDaoImpl(sessionFactory);
    }

    @BeforeEach
    void cleanDatabase() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            tx.commit();
        }
    }

    @AfterAll
    void teardown() {
        HibernateTestUtil.shutdown();
        postgresContainer.stop();
    }

    private User createTestUser(String name, String email, int age) {
        return new User(name, email, age);
    }

    @Test
    void createUser_success() {
        String name = "Aqua";
        String email = "aqua@example.com";
        int age = 25;
        User user = createTestUser(name, email, age);

        User saved = userDao.create(user);

        assertAll(
                () -> assertNotNull(saved.getId()),
                () -> assertEquals(name, saved.getName()),
                () -> assertEquals(email, saved.getEmail()),
                () -> assertEquals(age, saved.getAge())
        );
    }

    @Test
    void getById_success() {
        String name = "Bob";
        String email = "bob@example.com";
        int age = 30;
        User user = userDao.create(createTestUser(name, email, age));

        Optional<User> found = userDao.getById(user.getId());

        assertAll(
                () -> assertTrue(found.isPresent()),
                () -> assertEquals(name, found.get().getName()),
                () -> assertEquals(email, found.get().getEmail()),
                () -> assertEquals(age, found.get().getAge())
        );
    }

    @Test
    void getAll_success() {
        userDao.create(createTestUser("Charlie", "charlie@example.com", 22));
        userDao.create(createTestUser("Diana", "diana@example.com", 28));

        List<User> users = userDao.getAll();

        assertAll(
                () -> assertEquals(2, users.size()),
                () -> assertTrue(users.stream().anyMatch(u-> u.getName().equals("Charlie"))),
                () -> assertTrue(users.stream().anyMatch(u-> u.getName().equals("Diana")))
        );


    }

    @Test
    void updateUser_success() {
        String newName = "Evelyn";
        User user = userDao.create(createTestUser("Eve", "eve@example.com", 35));
        user.setName(newName);
        userDao.update(user);

        Optional<User> updated = userDao.getById(user.getId());

        assertAll(
                () -> assertTrue(updated.isPresent()),
                () -> assertEquals(newName, updated.get().getName())
        );
    }

    @Test
    void deleteUser_success() {
        User user = userDao.create(createTestUser("Frank", "frank@example.com", 40));
        userDao.delete(user.getId());

        Optional<User> deleted = userDao.getById(user.getId());
        assertTrue(deleted.isEmpty());
    }
}