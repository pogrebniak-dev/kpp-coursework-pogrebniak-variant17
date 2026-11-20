DROP DATABASE IF EXISTS agrmonitor_db;
CREATE DATABASE agrmonitor_db;
USE agrmonitor_db;

-- 1. Таблиця полів
CREATE TABLE field (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(150) NOT NULL,
                       area DOUBLE NOT NULL,
                       crop_type VARCHAR(100) NOT NULL
);

-- 2. Таблиця спостережень (тут є зовнішній ключ field_id)
CREATE TABLE observation (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             field_id BIGINT NOT NULL,
                             obs_date VARCHAR(30) NOT NULL,
                             f1 DOUBLE NOT NULL,
                             f2 DOUBLE NOT NULL,
                             f3 DOUBLE NOT NULL,
                             FOREIGN KEY (field_id) REFERENCES field(id)
);

-- 3. Таблиця проблем (об'єкт життєвого циклу, посилається на спостереження)
CREATE TABLE problem (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         observation_id BIGINT NOT NULL,
                         state VARCHAR(50) NOT NULL,
                         risk_index DOUBLE NOT NULL,
                         FOREIGN KEY (observation_id) REFERENCES observation(id)
);

-- 4. Таблиця заходів обробки (посилається на проблему)
CREATE TABLE treatment_action (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  problem_id BIGINT NOT NULL,
                                  description VARCHAR(255) NOT NULL,
                                  planned_date VARCHAR(30) NOT NULL,
                                  FOREIGN KEY (problem_id) REFERENCES problem(id)
);

-- Додаємо трохи тестових даних для перевірки
INSERT INTO field (name, area, crop_type) VALUES
                                              ('Південне поле', 45.5, 'Пшениця'),
                                              ('Сонячна долина', 30.0, 'Кукурудза');

INSERT INTO observation (field_id, obs_date, f1, f2, f3) VALUES
                                                             (1, '2026-10-15', 25.0, 40.0, 60.0),
                                                             (2, '2026-10-16', 10.0, 20.0, 30.0);