package com.example.userservice;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class Main {
    public static void main(String[] args) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            User user = new User("Alice", "alice@example.com", 25);
            session.persist(user);

            tx.commit();
            System.out.println("✅ Пользователь добавлен: " + user.getId());
        }
    }
}