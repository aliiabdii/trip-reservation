## Trip Reservation

This is a simple Trip reservation service developed with Kotlin and [Micronaut](https://guides.micronaut.io) framework. It
provides simple APIs to reserve certain amount of seats in specific trips for a customer, and is designed to handle the race
condition and concurrency issues for a huge load of concurrent requests.

To run the project, first you need to set the `datasources.default.url` property inside `application.yml` with the address of
a running Postgres instance (or alternatively create your own configuration file). Then you can start the server with the 
following command: `./gradlew run`. Once the server is up and running, you can access the APIs in the following address: http://localhost:8080/swagger-ui.

Please note that in order to run the Integration tests, you would need to have a running docker engine in your system as 
It's using [Testcontainers](https://github.com/testcontainers/testcontainers-java) for the JUnit database.

