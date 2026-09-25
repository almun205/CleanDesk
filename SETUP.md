# CleanDesk setup guide

This guide takes a new developer from a fresh checkout to a running CleanDesk
application. CleanDesk uses PostgreSQL only; no H2 database is required.

## Requirements

- Java Development Kit (JDK) 23
- Maven 3.9 or newer
- PostgreSQL 14 or newer
- Git, if the project is being cloned from a repository

Check the installed versions:

```bash
java -version
mvn -version
psql --version
```

The `mvn -version` output must report Java 23. Maven downloads all Java library
dependencies declared in `pom.xml`; there is no Java equivalent of a Python
`requirements.txt` needed for this project.

## 1. Start PostgreSQL

On macOS with Homebrew:

```bash
brew services start postgresql@14
pg_isready -h localhost -p 5432
```

If the readiness check already reports `accepting connections`, PostgreSQL is
running and does not need to be started again.

Some Homebrew installations fail with a service-metadata error such as
`undefined method 'stop_timeout'`. In that case, start PostgreSQL directly:

```bash
PG14_PREFIX="$(brew --prefix postgresql@14)"
BREW_ROOT_FOR_SETUP="$(brew --prefix)"
"${PG14_PREFIX}/bin/pg_ctl" \
  -D "${BREW_ROOT_FOR_SETUP}/var/postgresql@14" \
  -l "${BREW_ROOT_FOR_SETUP}/var/log/postgresql@14.log" start
pg_isready -h localhost -p 5432
```

This fallback starts the same PostgreSQL server without the Homebrew service
wrapper. It may need to be run again after restarting the computer.

On Linux, the service is normally started with:

```bash
sudo systemctl start postgresql
pg_isready -h localhost -p 5432
```

On Windows, start the PostgreSQL service from the **Services** application.

The readiness check should report `accepting connections` before continuing.

## 2. Create the application database

Open PostgreSQL as an administrator. On macOS/Homebrew this is usually:

```bash
psql postgres
```

On Linux or Windows, use the administrator account if required:

```bash
psql -U postgres
```

At the PostgreSQL prompt, run the following statements. Replace `change-me`
with a local password and keep it for the next step.

```sql
CREATE USER cleandesk WITH PASSWORD 'change-me';
CREATE DATABASE cleandesk OWNER cleandesk;
\q
```

These commands only need to be run once. If the user and database already
exist, do not create them again.

Verify the new connection:

```bash
psql -h localhost -U cleandesk -d cleandesk -W
```

Enter the password chosen above. Run `\q` to leave PostgreSQL after the
connection succeeds.

## 3. Create the local configuration

From the project directory, copy the example configuration:

macOS or Linux:

```bash
cp .env.example .env
```

Windows Command Prompt:

```bat
copy .env.example .env
```

Open `.env` and replace `change-me` with the same PostgreSQL password. The
`.env` file is ignored by Git so a developer's password is not committed.

## 4. Build and run

macOS or Linux:

```bash
./scripts/install_all.sh
./scripts/run.sh
```

Windows:

```bat
scripts\install_all.bat
scripts\run.bat
```

The run scripts automatically load `.env`. On first startup, CleanDesk creates
the `board` and `card` tables in PostgreSQL.

## IntelliJ IDEA

To browse the database in IntelliJ, create a PostgreSQL data source with:

- Host: `localhost`
- Port: `5432`
- User: `cleandesk`
- Password: the value from `.env`
- Database: `cleandesk`
- URL: `jdbc:postgresql://localhost:5432/cleandesk`

Click **Test Connection**, then **Apply** and **OK**. This data-source connection
is only for browsing the database; the application itself reads `.env` when it
is launched with the supplied run scripts.

## Optional PostgreSQL integration tests

Normal unit tests run during setup. Repository integration tests require a
separate disposable PostgreSQL database so they cannot modify application data.
Create `cleandesk_test`, then uncomment the three `CLEANDESK_TEST_DB_*` entries
in `.env`. Run them through the script so `.env` is loaded:

```bash
./scripts/install_all.sh
```

Without `CLEANDESK_TEST_DB_URL`, these three tests are safely skipped.

## Common errors

- **Connection refused:** PostgreSQL is not running. Return to step 1.
- **Password authentication failed:** the password in `.env` does not match the
  password assigned to the `cleandesk` PostgreSQL user.
- **Database does not exist:** create `cleandesk` as shown in step 2.
- **Release version 23 not supported:** Maven is using an older Java version.
  Check `mvn -version`, configure `JAVA_HOME` for JDK 23, and try again.
