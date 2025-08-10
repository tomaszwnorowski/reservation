package codem.ipsum.rental.repository;

import codem.ipsum.jooq.generated.tables.records.ReservationRecord;
import codem.ipsum.rental.model.ReservationCriteria;
import org.jooq.DSLContext;
import org.springframework.transaction.annotation.Transactional;

import static codem.ipsum.jooq.generated.tables.Car.CAR;
import static codem.ipsum.jooq.generated.tables.Reservation.RESERVATION;

public class JooqReservationRepository implements ReservationRepository {

    private final DSLContext jooq;

    public JooqReservationRepository(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Override
    @Transactional
    public void store(ReservationCriteria criteria) {
        var matchedCar = jooq.select(CAR.ID)
                .from(CAR)
                .where(CAR.TYPE.eq(criteria.type().name()))
                .andNotExists(
                        jooq.selectOne()
                                .from(RESERVATION)
                                .where(RESERVATION.CAR_ID.eq(CAR.ID))
                                .and(RESERVATION.FROM.lessOrEqual(criteria.to()))
                                .and(RESERVATION.TO.greaterOrEqual(criteria.from()))
                )
                .limit(1)
                .forUpdate()
                .skipLocked()
                .fetchOptional();

        matchedCar.map(
                id -> new ReservationRecord(null, id.value1(), criteria.from(), criteria.to())
        ).ifPresentOrElse(
                record -> jooq.newRecord(RESERVATION, record).store(),
                () -> {
                    // TODO - business logic exception
                    throw new IllegalStateException("Unable to complete reservation with defined criteria");
                });
    }

    @Override
    public long reservationCount() {
        return jooq.fetchCount(RESERVATION);
    }
}
