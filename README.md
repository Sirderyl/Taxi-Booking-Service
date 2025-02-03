# Taxi Booking Service REST API

This system manages bookings for a taxi service. It is a complete implementation with a web UI, back-end, and database mappings.
The API supports CRUD operations on customer data, taxi details, and bookings. To create a booking, a valid customer
and taxi need to be associated with it. Guest bookings are also available, where a new customer and a booking can be
created with a single HTTP request. The system also includes a travel agent service. With this, a customer can book
a flight, a hotel, and a taxi with a single HTTP request. Flights and hotels are managed through external APIs this
system interacts with.

This project serves as a learning ground for enterprise-level application development. It is developed using the
service layer design pattern style. Each service has separate layers for the entity, data access, business logic, and
controller. It uses a number of enterprise technologies and middleware, such as Quarkus, JPA (Hibernate),
transaction management through JTA, bean validation, JUnit with REST Assured, JAX-RS, Maven, and more. This app was
also containerized and deployed on Red Hat's Cloud platform - OpenShift.

## Running the application locally (in dev mode)

Before running for the first time, clean and rebuild the project:

```shell script
mvn clean
mvn package
```

To run in dev mode:

```shell
./mvnw compile quarkus:dev
```

---

> The web UI can now be accessed at http://localhost:8080/q/swagger-ui/
