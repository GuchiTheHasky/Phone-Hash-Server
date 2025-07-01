# PhoneHashServer

- Java-сервіс для хешування телефонних номерів та пошуку за хешем. 
- Сервіс підтримує паралельне хешування та завантаження даних у Redis.

# Авторизація

- Authorization: Basic token
- token example: R3VjaGk6VGhlSHVza3k=

# API

- GET http://localhost:8080/hash?phone=... - повертає хеш для переданого номера телефону
- phone example: 380671231231

- GET http://localhost:8080/phone?hash=... - повертає номер телефону для переданого хеша
- hash example: e89dc265143944f92219d8ae979c59306e220fe26895f521e56bc2ef6bd38e7d

# Тестування

- Для запуску тестів:

```bash
docker compose run --rm tests
```

# Запуск

- Для запуску застосунку

```bash
docker compose up --build
```
