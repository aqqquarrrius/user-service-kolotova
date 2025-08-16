# User Service — консольное Java приложение

## Описание
Приложение для работы с пользователями (CRUD) на Java с использованием Hibernate и PostgreSQL.

## Используемые технологии
- Java 17
- Hibernate ORM
- PostgreSQL
- Maven
- JUnit
- Mockito
- Testcontainers (для IT)
- Docker (для запуска тестовой базы)

## Настройка и запуск
1. Установить PostgreSQL и создать базу данных (или использовать Docker).
2. Настроить доступ к БД в HibernateUtil.java (или использовать Testcontainers для тестов).
3. Сборка проекта через Maven:
   `bash
   mvn clean install


## Структура проекта
- src/main/java — исходный код
- src/main/resources — конфигурационные файлы
- hibernate.cfg.xml — конфигурация Hibernate
- src/test/java — юнит-тесты и интеграционные тесты