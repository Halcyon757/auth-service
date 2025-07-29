# Запуск
```
docker-compose up --build
```

---
# Swagger UI

Полная документация по всем эндпойнтам доступна в Swagger:
```
http://localhost:8080/swagger-ui/index.html
```
---
## Контакт: @pfishek (TG)
## Эндпойнты

### 1. Регистрация пользователя
POST
```
http://localhost:8080/auth/signup
```  
Регистрация нового пользователя, хеширование пароля, выдача токенов.

Request:
```
{
  "login": "ivan",
  "email": "ivan@example.com",
  "password": "qwerty123",
  "premium": false
}
```

Response 200 OK:
```
{
  "accessToken": "eyJhbGci…",
  "refreshToken": "d290f1ee-6c54-…",
  "tokenType": "Bearer",
  "expiresIn": 900000
}
```

Ошибки:  
400 Bad Request – login/email уже заняты или не прошли валидацию.

---
### 2. Авторизация (вход)
POST
```
http://localhost:8080/auth/signin
```  
Проверка логина/пароля, выдача новых токенов (ротация refresh‑токена).

Request:
```
{
  "login": "ivan",
  "password": "qwerty123"
}
```

Response 200 OK:
```
{
  "accessToken": "…",
  "refreshToken": "…",
  "tokenType": "Bearer",
  "expiresIn": 900000
}
```

Ошибки:  
400 Bad Request – неверный логин или пароль.

---
### 3. Обновление access‑токена
POST
```
http://localhost:8080/auth/refreshtoken
```  
Проверка и ротация refresh‑токена, выдача нового access‑токена.

Request:
```
{
  "refreshToken": "d290f1ee-6c54-…"
}
```

Response 200 OK:
```
{
  "accessToken": "…",
  "refreshToken": "…",
  "tokenType": "Bearer",
  "expiresIn": 900000
}
```

Ошибки:  
403 Forbidden – токен не найден или истёк.

---
### 4. Выход (отзыв refresh‑токена)
POST
```
http://localhost:8080/auth/signout
```  
Удаление указанного refresh‑токена из БД.

Request:
```
{
  "refreshToken": "d290f1ee-6c54-…"
}
```

Response 200 OK  
(пустой ответ)

---
### 5. Продвижение в PREMIUM (только для роли ADMIN)
POST
```
http://localhost:8080/auth/promote/{login}
```  
Назначение роли PREMIUM_USER пользователю с данным логином.

Пример:
POST
```
http://localhost:8080/auth/promote/ivan 
``` 
Authorization: Bearer <adminAccessToken>

Response 200 OK  
(пустой ответ)

Ошибки:  
401 Unauthorized – нет или неверен Bearer‑токен  
403 Forbidden – нет роли ADMIN  
404 Not Found – пользователь не найден  
400 Bad Request – пользователь уже PREMIUM_USER
