Locations access service for MET API
=======================================

This module implements a service for reading metadata about locations. Currently,
this is a database created for the METAPI service.

# Run

To be able to use the system, you will usually want to modify the
configuration files. For development purposes, you can instead create a file
`conf/development.conf` with the following entries:
```
db.locations.driver = org.postgresql.Driver
db.locations.url = "jdbc:postgresql://localhost:5432/locations"
db.locations.username = <your-user-name>
db.locations.password = ""
db.locations.logStatements = true
play.http.router = locations.Routes
mail.override.address = "<your-email>"
play.evolutions.db.authorization.autoApply=true
auth.active=false
```

## Tests

To run the tests, do: `activator test`. To run tests with coverage report,
use: `activator coverage test coverageReport`.

## Running with Mock

To run the application with mock database connections, do: `activator run`

## Running in Test Production

To run the application in test production, you will need a working database
for the system to interact with.

A simple approach, on Ubuntu, is to install a Postgres server on localhost,
set it up for local connections (listen_addresses = '*' in postgresql.conf),
set it to trust local connections (in pg_hba.conf), and then create a local
database elements with `createdb locations`.

Alternatively, you can point your url at a test instance of locdb.

Once the database is set up, you can run test production using `activator testProd`.

If you are running against your own database on localhost, use:
`activator "testProd -Dplay.evolutions.db.locations.autoApply=true"` to evolve
the database first.
 
 
