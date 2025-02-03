package uk.ac.newcastle.enterprisemiddleware.hotel;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class HotelBookingPostDTO {

    @NotNull
    private Long customerId;

    @NotNull
    private Long hotelId;

    @NotNull
    @Future(message = "The booking date must be in the future")
    private LocalDate bookingDate;

    public @NotNull Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(@NotNull Long customerId) {
        this.customerId = customerId;
    }

    public @NotNull Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(@NotNull Long hotelId) {
        this.hotelId = hotelId;
    }

    public @NotNull @Future(message = "The booking date must be in the future") LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(@NotNull @Future(message = "The booking date must be in the future") LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }
}
