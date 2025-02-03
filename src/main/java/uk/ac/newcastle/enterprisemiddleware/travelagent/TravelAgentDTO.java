package uk.ac.newcastle.enterprisemiddleware.travelagent;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class TravelAgentDTO {

    @NotNull
    private Long customerId;

    @NotNull
    private Long taxiId;

    @NotNull
    private Long flightId;

    @NotNull
    private Long hotelId;

    @NotNull
    @Future(message = "The booking date must be in the future")
    private LocalDate taxiBookingDate;

    @NotNull
    @Future(message = "The booking date must be in the future")
    private LocalDate flightBookingDate;

    @NotNull
    @Future(message = "The booking date must be in the future")
    private LocalDate hotelBookingDate;

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

    public @NotNull Long getFlightId() {
        return flightId;
    }

    public void setFlightId(@NotNull Long flightId) {
        this.flightId = flightId;
    }

    public @NotNull Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(@NotNull Long hotelId) {
        this.hotelId = hotelId;
    }

    public @NotNull @Future(message = "The booking date must be in the future") LocalDate getTaxiBookingDate() {
        return taxiBookingDate;
    }

    public void setTaxiBookingDate(@NotNull @Future(message = "The booking date must be in the future") LocalDate taxiBookingDate) {
        this.taxiBookingDate = taxiBookingDate;
    }

    public @NotNull @Future(message = "The booking date must be in the future") LocalDate getFlightBookingDate() {
        return flightBookingDate;
    }

    public void setFlightBookingDate(@NotNull @Future(message = "The booking date must be in the future") LocalDate flightBookingDate) {
        this.flightBookingDate = flightBookingDate;
    }

    public @NotNull @Future(message = "The booking date must be in the future") LocalDate getHotelBookingDate() {
        return hotelBookingDate;
    }

    public void setHotelBookingDate(@NotNull @Future(message = "The booking date must be in the future") LocalDate hotelBookingDate) {
        this.hotelBookingDate = hotelBookingDate;
    }
}
