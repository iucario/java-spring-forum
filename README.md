# Java Spring Forum

![workflow](https://github.com/iucario/java-spring-forum/actions/workflows/gradle.yml/badge.svg)
[![codecov](https://codecov.io/gh/iucario/java-spring-forum/branch/main/graph/badge.svg?token=BCPV9T9XSW)](https://codecov.io/gh/iucario/java-spring-forum)

A forum API demo.

Features: Creating user, posting, commenting and uploading files.

Authentication: Filter and JWT

Database: PostgreSQL

Cache: Redis

Technologies: JPA, Hibernate, Spring

API docs: https://iucario.github.io/java-spring-forum/

Simple frontend: https://github.com/iucario/forum-frontend

![](Demo%20Diagram.png)

## Getting Started

### Start server

```shell
docker run --rm -d -p 6379:6379 redis
docker run --rm -e POSTGRES_PASSWORD=mypassword -e POSTGRES_USER=myuser -e POSTGRES_DB=demo -p 5432:5432 -d postgres
./gradlew bootrun
```

## API

OpenAPI docs: http://localhost:8080/swagger

- `/user/register` post
- `/user/login` post
- `/user/me` get
- `/api/post` post, get, put, delete
- `/api/comment` post, get, put, delete
- `/file/{}` post, get, delete

### API Tests

install newman
`npm install -g newman`

cd to this project. Then run `newman run scripts/Spring.postman_collection.json`
