package uk.ac.newcastle.enterprisemiddleware.flight;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class FlightBookingPostDTO {

    @NotNull
    private Long customerId;

    @NotNull
    private Long flightId;

    @NotNull
    @Future(message = "The booking date must be in the future")
    private LocalDate date;

    public @NotNull Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(@NotNull Long customerId) {
        this.customerId = customerId;
    }

    public @NotNull Long getFlightId() {
        return flightId;
    }

    public void setFlightId(@NotNull Long flightId) {
        this.flightId = flightId;
    }

    public @NotNull @Future(message = "The booking date must be in the future") LocalDate getDate() {
        return date;
    }

    public void setDate(@NotNull @Future(message = "The booking date must be in the future") LocalDate date) {
        this.date = date;
    }
}
