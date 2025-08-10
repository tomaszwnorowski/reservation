package codem.ipsum.rental.repository;

import codem.ipsum.rental.model.ReservationCriteria;

public interface ReservationRepository {

    void store(ReservationCriteria reservation);

    long reservationCount();

}
