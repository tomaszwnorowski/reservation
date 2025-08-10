package codem.ipsum.rental.repository;

import codem.ipsum.jooq.generated.tables.records.CarRecord;
import codem.ipsum.rental.model.Car;
import org.jooq.DSLContext;

import static codem.ipsum.jooq.generated.tables.Car.CAR;

public class JooqCarRepository implements CarRepository {

    private final DSLContext jooq;

    public JooqCarRepository(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Override
    public void store(Car car) {
        jooq.newRecord(CAR,
                new CarRecord(null, car.type().name(), car.licensePlate().value())
        ).store();
    }
}
