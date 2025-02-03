package uk.ac.newcastle.enterprisemiddleware.booking;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class BookingValidator {

    @Inject
    Validator validator;

    @Inject
    BookingRepository bookingRepository;

    void validateBooking(Booking booking) {
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        if (bookingForTaxiAndDateExists(booking.getTaxi().getId(), booking.getDate())) {
            throw new UniqueBookingException("Unique booking violation");
        }
    }

    boolean bookingForTaxiAndDateExists(Long taxiId, LocalDate bookingDate) {
        Booking booking = null;

        try {
            booking = bookingRepository.findByTaxiAndDate(taxiId, bookingDate);
        } catch (NoResultException e) {
            return false;
        }

        return booking != null;
    }
}
