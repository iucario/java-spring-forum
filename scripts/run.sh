docker stop demodb || true
docker run --rm \
    -e POSTGRES_PASSWORD=mypassword \
    -e POSTGRES_USER=myuser \
    -e POSTGRES_DB=demo \
    -p 5432:5432 \
    --name demodb \
    -d postgres


PGPASSWORD=mypassword psql -h localhost -U myuser -d demo -f schema.sql
PGPASSWORD=mypassword psql -h localhost -U myuser -d demo -f data.sql