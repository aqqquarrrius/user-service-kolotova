package com.example.userservice;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserServiceConsoleApp {

    private static final UserDao userDao = new UserDaoImpl();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> createUser();
                case "2" -> getUserById();
                case "3" -> getAllUsers();
                case "4" -> updateUser();
                case "5" -> deleteUser();
                case "0" -> {
                    System.out.println("Выход...");
                    HibernateUtil.shutdown();
                    return;
                }
                default -> System.out.println("Неверный выбор");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n=== User Service Menu ===");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Получить пользователя по ID");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    private static void createUser() {
        System.out.print("Имя: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Возраст: ");
        int age = Integer.parseInt(scanner.nextLine());

        User user = new User(name, email, age);
        try {
            userDao.create(user);
            System.out.println("Пользователь создан с ID: " + user.getId());
        } catch (Exception e) {
            System.out.println("Ошибка создания пользователя: " + e.getMessage());
        }
    }

    private static void getUserById() {
        System.out.print("Введите ID: ");
        Long id = Long.parseLong(scanner.nextLine());
        Optional<User> user = userDao.getById(id);
        user.ifPresentOrElse(
                u -> System.out.println("Пользователь: " + u.getName() + ", email: " + u.getEmail()),
                () -> System.out.println("Пользователь не найден")
        );
    }

    private static void getAllUsers() {
        List<User> users = userDao.getAll();
        if (users.isEmpty()) {
            System.out.println("Нет пользователей");
        } else {
            users.forEach(u -> System.out.printf("ID: %d, Имя: %s, Email: %s, Возраст: %d%n",
                    u.getId(), u.getName(), u.getEmail(), u.getAge()));
        }
    }

    private static void updateUser() {
        System.out.print("Введите ID пользователя для обновления: ");
        Long id = Long.parseLong(scanner.nextLine());
        Optional<User> optionalUser = userDao.getById(id);
        if (optionalUser.isEmpty()) {
            System.out.println("Пользователь не найден");
            return;
        }
        User user = optionalUser.get();

        System.out.print("Новое имя (Enter, чтобы оставить '" + user.getName() + "'): ");
        String name = scanner.nextLine();
        if (!name.isBlank()) user.setName(name);

        System.out.print("Новый email (Enter, чтобы оставить '" + user.getEmail() + "'): ");
        String email = scanner.nextLine();
        if (!email.isBlank()) user.setEmail(email);

        System.out.print("Новый возраст (Enter, чтобы оставить '" + user.getAge() + "'): ");
        String ageInput = scanner.nextLine();
        if (!ageInput.isBlank()) {
            try {
                int age = Integer.parseInt(ageInput);
                user.setAge(age);
            } catch (NumberFormatException e) {
                System.out.println("Возраст не изменён — введено неверное число");
            }
        } try {
            userDao.update(user);
            System.out.println("Пользователь обновлён");
        } catch (Exception e) {
            System.out.println("Ошибка обновления: " + e.getMessage());
        }
    }

    private static void deleteUser() {
        System.out.print("Введите ID пользователя для удаления: ");
        Long id = Long.parseLong(scanner.nextLine());
        try {
            userDao.delete(id);
            System.out.println("Пользователь удалён (если существовал)");
        } catch (Exception e) {
            System.out.println("Ошибка удаления: " + e.getMessage());
        }
    }
}