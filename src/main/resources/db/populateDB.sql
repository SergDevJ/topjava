DELETE FROM user_roles;
DELETE FROM meals;
DELETE FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals(description, dateTime, calories, user_id)
VALUES ('Завтрак', to_timestamp('2000-01-30 10:00', 'YYYY-MM-DD HH24:MI'), 500, 100000),
       ('Обед', to_timestamp('2000-01-30 13:00', 'YYYY-MM-DD HH24:MI'), 1000, 100000),
       ('Ужин', to_timestamp('2000-01-30 20:00', 'YYYY-MM-DD HH24:MI'), 500, 100000),
       ('Еда на граничное значение', to_timestamp('2000-01-31 00:00', 'YYYY-MM-DD HH24:MI'), 100, 100000),
       ('Завтрак', to_timestamp('2000-01-31 10:00', 'YYYY-MM-DD HH24:MI'), 1000, 100000),
       ('Обед', to_timestamp('2000-01-31 13:00', 'YYYY-MM-DD HH24:MI'), 500, 100000),
       ('Ужин', to_timestamp('2000-01-31 20:00', 'YYYY-MM-DD HH24:MI'), 410, 100000),
       ('Админ ланч', to_timestamp('2015-06-01 14:00', 'YYYY-MM-DD HH24:MI'), 510, 100001),
       ('Админ ужин', to_timestamp('2015-06-01 21:00', 'YYYY-MM-DD HH24:MI'), 1500, 100001);