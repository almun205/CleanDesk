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

## Start an existing installation (macOS)

Open a terminal in the project directory. Start PostgreSQL and check that it is
accepting connections before launching CleanDesk:

```bash
brew services start postgresql@14
pg_isready -h localhost -p 5432
./scripts/run.sh
```

The run script loads the existing `.env` automatically. Keep its configured
database password; there is no need to copy `.env.example` again.

### PostgreSQL: connection refused or Homebrew service error

If CleanDesk reports `Connection to localhost:5432 refused`, PostgreSQL is not
accepting connections. If the Homebrew service also fails, check the owner of
the database directory:

```bash
ls -ld /opt/homebrew/var/postgresql@14
```

On the current local setup, this directory belongs to the macOS user `ahmed`,
while the logged-in user is `almun`. PostgreSQL must run as the directory owner.
The following command successfully started the existing database:

```bash
sudo -u ahmed /opt/homebrew/opt/postgresql@14/bin/pg_ctl -D /opt/homebrew/var/postgresql@14 -l /opt/homebrew/var/log/postgresql@14.log start
```

Enter your Mac administrator password when prompted; no characters are shown
while typing. The username and `/opt/homebrew` paths above are specific to this
local installation; use your actual database-directory owner and Homebrew paths
on another machine.

Then verify the server and start CleanDesk:

```bash
pg_isready -h localhost -p 5432
./scripts/run.sh
```

The check should report `accepting connections`. This manual PostgreSQL start
may need to be repeated after restarting the Mac.

## Tests

```bash
mvn verify
```

See [SETUP.md](SETUP.md#optional-postgresql-integration-tests) to enable the
PostgreSQL repository integration tests.
