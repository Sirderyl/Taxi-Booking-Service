package uk.ac.newcastle.enterprisemiddleware.booking;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class BookingDTO {

    @NotNull
    private Long customerId;

    @NotNull
    private Long taxiId;

    @NotNull
    @Future(message = "Booking date must be in the future")
    private LocalDate bookingDate;

    public @NotNull Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(@NotNull Long customerId) {
        this.customerId = customerId;
    }

    public @NotNull Long getTaxiId() {
        return taxiId;
    }

    public void setTaxiId(@NotNull Long taxiId) {
        this.taxiId = taxiId;
    }

    public @NotNull @Future(message = "Booking date must be in the future") LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(@NotNull @Future(message = "Booking date must be in the future") LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }
}
