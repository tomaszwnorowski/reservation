package codem.ipsum.rental.model;

import java.time.LocalDateTime;

public record ReservationCriteria(
        LocalDateTime from,
        LocalDateTime to,
        CarType type
) {
}
