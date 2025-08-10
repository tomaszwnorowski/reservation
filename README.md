# Getting Started

Demo application of a car reservation system.

The primary area of focus was proper configuration of an application that depends on flyway and
jooq (including codegen) and solving the problem by relying on locking in the database.

The most interesting part of the solution is in JooqReservationRepository which:
* allow accepting various criteria for filtering a desired car that can be easily extended
* handle race conditions by locking a particular car until the transaction completes
* gives a lot of freedom in terms of optimizing the query or changing the approach (e.g., optimistic locking)

```java
jooq.select(CAR.ID)
  .from(CAR)
  // more criteria can be added here
  .where(CAR.TYPE.eq(criteria.type().name()))
  .andNotExists(
    jooq.selectOne()
      .from(RESERVATION)
      .where(RESERVATION.CAR_ID.eq(CAR.ID))
      // check whether attempted reservation is overlaping with existing for this car
      .and(RESERVATION.FROM.lessOrEqual(criteria.to()))
      .and(RESERVATION.TO.greaterOrEqual(criteria.from()))
   )
   // select first one matching criteria and not overlapgin with existing reservation
   .limit(1)
   // lock the car
   .forUpdate()
   // make other queries running at the same time skip this car and look for another not locked one
   .skipLocked()
   .fetchOptional();
```

# Requirements

To generate jooq classes, postgres instance must be running in the background. It can be started
via docker or podman (example for podman in build.gradle)

For running the integration tests, docker needs to be available in the PATH

# Out of scope

* REST API for creating the reservation (POST method with criteria payload)
* Exposing performance metrics
* Observability
* Exception handling
* ...
