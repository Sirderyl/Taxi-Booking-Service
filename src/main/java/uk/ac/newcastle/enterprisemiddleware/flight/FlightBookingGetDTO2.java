package uk.ac.newcastle.enterprisemiddleware.flight;

import java.time.LocalDate;

public class FlightBookingGetDTO2 {

    private Long bookingId;
    private FlightCustomer customer;
    private Flight flight;
    private LocalDate date;

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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
