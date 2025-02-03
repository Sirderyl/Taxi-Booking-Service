package uk.ac.newcastle.enterprisemiddleware.flight;

import java.time.OffsetDateTime;

public class FlightBookingGetDTO {

    private Long bookingId;
    private FlightCustomer customer;
    private Flight flight;
    private OffsetDateTime date;

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public FlightCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(FlightCustomer customer) {
        this.customer = customer;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public OffsetDateTime getDate() {
        return date;
    }

    public void setDate(OffsetDateTime date) {
        this.date = date;
    }
}
