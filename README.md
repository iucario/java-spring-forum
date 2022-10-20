# Java Spring Forum

![workflow](https://github.com/iucario/java-spring-forum/actions/workflows/gradle.yml/badge.svg)
[![codecov](https://codecov.io/gh/iucario/java-spring-forum/branch/main/graph/badge.svg?token=BCPV9T9XSW)](https://codecov.io/gh/iucario/java-spring-forum)

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
