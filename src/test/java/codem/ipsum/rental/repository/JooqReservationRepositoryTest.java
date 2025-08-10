package codem.ipsum.rental.repository;

import codem.ipsum.rental.config.RentalConfiguration;
import codem.ipsum.rental.model.Car;
import codem.ipsum.rental.model.CarType;
import codem.ipsum.rental.model.LicensePlate;
import codem.ipsum.rental.model.ReservationCriteria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JooqTest
@Import({TestcontainersConfiguration.class, RentalConfiguration.class})
class JooqReservationRepositoryTest {

    @Autowired
    CarRepository carRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Test
    public void whenNonConflictingReservationAttemptThenSuccess() {
        // given
        var car = new Car(CarType.SUV, new LicensePlate("GD12345"));
        var criteria = new ReservationCriteria(
                LocalDateTime.parse("2025-08-20T08:00:00"),
                LocalDateTime.parse("2025-08-21T08:00:00"),
                CarType.SUV
        );

        // when
        carRepository.store(car);
        reservationRepository.store(criteria);

        // then
        var reservationCount = reservationRepository.reservationCount();

        // and
        assertThat(reservationCount).isEqualTo(1);
    }

    @Test
    public void whenNoCarMatchingCriteriaAvailableThenFailure() {
        // given
        var criteria = new ReservationCriteria(
                LocalDateTime.parse("2025-08-20T08:00:00"),
                LocalDateTime.parse("2025-08-21T08:00:00"),
                CarType.VAN
        );

        // then
        assertThatThrownBy(() -> reservationRepository.store(criteria));
    }

    @Test
    public void whenConflictingReservationAttemptThenFailure() {
        // given
        var car = new Car(CarType.SEDAN, new LicensePlate("GD78901"));
        var criteria = new ReservationCriteria(
                LocalDateTime.parse("2025-08-20T08:00:00"),
                LocalDateTime.parse("2025-08-21T08:00:00"),
                CarType.SEDAN
        );

        // when
        carRepository.store(car);
        reservationRepository.store(criteria);

        // the first attempt should succeed
        assertThat(reservationRepository.reservationCount()).isEqualTo(1);
        // the second attempt should fail
        assertThatThrownBy(() -> reservationRepository.store(criteria));
    }
}