# Java Spring CRUD Demo

An API demo.

Authentication: Filter and JWT

Database: PostgreSQL

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
- `/api/item` post, get, put, delete

