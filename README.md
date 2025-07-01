# PhoneHashServer

- Сервіс реалізовано на Java без використання Spring, з підтримкою Basic Auth, 
- паралельним завантаженням 60 млн номерів у Redis, а швидким доступом до хешів і номерів абонентів.

- Особливості:
- Продуктивність понад 6 000 RPS згідно з JMeter-тестами.
- Запускається однією командою: docker compose up
- Розділення на три контейнери: app, redis, tests.

- Підтримка масштабування та незалежного запуску тестів.

# Пропозиції щодо покращення

- Додати health-check ендпойнт;
- Додати Swagger документацію;
- Замінити Basic Auth на JWT з обмеженим TTL;
- Додати ендпойнт для перегляду сторінки номерів за телефонним префіксом (38067, 38097...);
- Додати ендпойнт для перегляду списку номерів || списку хешів;
- Додати ендпойнт експорт/імпорт списків у вигляді файлів;

# Авторизація

- Authorization: Basic HTTP
- HTTP example: R3VjaGk6VGhlSHVza3k=

# API

- Повертає хеш для переданого номера телефону

curl --location 'http://localhost:8080/hash?phone=380671231231' \
--header 'Authorization: Basic R3VjaGk6VGhlSHVza3k='

- Повертає номер телефону для переданого хеша

curl --location 'localhost:8080/phone?hash=e89dc265143944f92219d8ae979c59306e220fe26895f521e56bc2ef6bd38e7d' \
--header 'Authorization: Basic R3VjaGk6VGhlSHVza3k='

# Тестування

- Для запуску тестів:

```bash
docker compose run tests
```

# Запуск

- Для запуску застосунку

```bash
docker compose up app redis
```

# JMeter

![img.png](jmeter/jmeter_report.png)

![img.png](jmeter/time_grauph.png)
