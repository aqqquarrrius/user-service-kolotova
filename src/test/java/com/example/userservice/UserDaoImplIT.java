package com.example.userservice;

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
            session.createQuery("DELETE FROM User").executeUpdate();
            tx.commit();
        }
    }

    @AfterAll
    void teardown() {
        HibernateTestUtil.shutdown();
        postgresContainer.stop();
    }

    @Test
    void createUser_success() {
        User user = new User("Aqua", "aqua@example.com", 25);
        User saved = userDao.create(user);

        assertNotNull(saved.getId());
        assertEquals("Aqua", saved.getName());
    }

    @Test
    void getById_success() {
        User user = userDao.create(new User("Bob", "bob@example.com", 30));
        Optional<User> found = userDao.getById(user.getId());

        assertTrue(found.isPresent());
        assertEquals("Bob", found.get().getName());
    }

    @Test
    void getAll_success() {
        userDao.create(new User("Charlie", "charlie@example.com", 22));
        userDao.create(new User("Diana", "diana@example.com", 28));

        List<User> users = userDao.getAll();
        assertEquals(2, users.size());
    }

    @Test
    void updateUser_success() {
        User user = userDao.create(new User("Eve", "eve@example.com", 35));
        user.setName("Evelyn");
        userDao.update(user);

        Optional<User> updated = userDao.getById(user.getId());
        assertEquals("Evelyn", updated.get().getName());
    }

    @Test
    void deleteUser_success() {
        User user = userDao.create(new User("Frank", "frank@example.com", 40));
        userDao.delete(user.getId());

        Optional<User> deleted = userDao.getById(user.getId());
        assertTrue(deleted.isEmpty());
    }
}