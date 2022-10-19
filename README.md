# Java Spring CRUD Demo

An API demo.

Authentication: Filter and JWT

Database: PostgreSQL

Technologies: JPA, Hibernate, Spring

![](Demo%20Diagram.png)

## Getting Started

- `/user/register`

  POST

    ```json
    {
      "name": "username",
      "password": "password"
    }
    ```
  Response

  status: 200 OK body: User created

- `/user/login`

  POST

    ```json
    {
      "name": "username",
      "password": "password"
    }
    ```

  Response

    ```json
    {
      "token": "JWT token"
    }
    ```

- `/user/me` get
- `/api/post` post, get, put, delete

