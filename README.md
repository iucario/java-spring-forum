# Java Spring Forum

An forum API demo.

Features: Creating user, posting, commenting and uploading files.

Authentication: Filter and JWT

Database: PostgreSQL

Technologies: JPA, Hibernate, Spring

![](Demo%20Diagram.png)

## Getting Started

`docker run --rm -e POSTGRES_PASSWORD=mypassword -e POSTGRES_USER=myuser -e POSTGRES_DB=demo -p 5432:5432 -d postgres`

## API

OpenAPI docs: https://localhost:8080/swagger

- `/user/register` post
- `/user/login` post
- `/user/me` get
- `/api/post` post, get, put, delete
- `/api/comment` post, get, put, delete
- `/file/{}` post, get, delete 