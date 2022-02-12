# DB Migrations

Postgres database migrations are done with [Flyway](https://flywaydb.org/).

Migrations are named according the naming scheme [specified by Flyway](https://flywaydb.org/documentation/concepts/migrations#naming).

Command reference:
- `docker-compose run --rm flyway info` - Get information about migrations.
- `docker-compose run --rm flyway migrate` - Applies migrations to the DB.
- `docker-compose run --rm flyway baseline` - Setup new DB.