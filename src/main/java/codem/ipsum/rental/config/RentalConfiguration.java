package codem.ipsum.rental.config;

import codem.ipsum.rental.repository.CarRepository;
import codem.ipsum.rental.repository.JooqCarRepository;
import codem.ipsum.rental.repository.JooqReservationRepository;
import codem.ipsum.rental.repository.ReservationRepository;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RentalConfiguration {

    @Bean
    public ReservationRepository reservationRepository(DSLContext jooq) {
        return new JooqReservationRepository(jooq);
    }

    @Bean
    public CarRepository carRepository(DSLContext jooq) {
        return new JooqCarRepository(jooq);
    }
}
