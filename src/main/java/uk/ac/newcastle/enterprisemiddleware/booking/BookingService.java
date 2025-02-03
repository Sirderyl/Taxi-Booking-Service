package uk.ac.newcastle.enterprisemiddleware.booking;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.logging.Logger;

@Dependent
public class BookingService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    BookingValidator bookingValidator;

    @Inject
    BookingRepository bookingRepository;

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    List<Booking> getBookingsByCustomerId(Long customerId) {
        return bookingRepository.findAllByCustomer(customerId);
    }

    public Booking createBooking(Booking booking) {
        log.info("BookingService.createBooking() - Creating " + booking.toString());
        bookingValidator.validateBooking(booking);
        return bookingRepository.create(booking);
    }

    public void deleteBooking(Booking booking) {
        log.info("BookingService.deleteBooking() - Deleting " + booking.toString());

        if (booking.getId() != null) {
            bookingRepository.delete(booking);
        } else {
            log.info("BookingService.deleteBooking() - No id found");
        }
    }
}
