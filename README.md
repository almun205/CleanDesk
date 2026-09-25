# CleanDesk

CleanDesk uses PostgreSQL for the application database. The schema is created
automatically when the application starts.

## First-time setup

Follow [SETUP.md](SETUP.md) for requirements, PostgreSQL creation, configuration,
IntelliJ connection instructions, and troubleshooting.

After completing the database steps, the short version is:

```bash
cp .env.example .env
# Edit .env and enter the PostgreSQL password.
./scripts/install_all.sh
./scripts/run.sh
```

Windows users can run:

```bat
copy .env.example .env
scripts\install_all.bat
scripts\run.bat
```

## Tests

```bash
mvn verify
```

See [SETUP.md](SETUP.md#optional-postgresql-integration-tests) to enable the
PostgreSQL repository integration tests.
