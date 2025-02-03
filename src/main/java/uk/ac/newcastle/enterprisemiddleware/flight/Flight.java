package uk.ac.newcastle.enterprisemiddleware.flight;

import uk.ac.newcastle.enterprisemiddleware.booking.Booking;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Flight implements Serializable {

    private Long flightId;
    private String flightNumber;
    private String departure;
    private String destination;
    private List<Booking> bookings;

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Flight)) return false;

        Flight flight = (Flight) o;
        return getFlightId().equals(flight.getFlightId()) && getFlightNumber().equals(flight.getFlightNumber()) && getDeparture().equals(flight.getDeparture()) && getDestination().equals(flight.getDestination()) && Objects.equals(getBookings(), flight.getBookings());
    }

    @Override
    public int hashCode() {
        int result = getFlightId().hashCode();
        result = 31 * result + getFlightNumber().hashCode();
        result = 31 * result + getDeparture().hashCode();
        result = 31 * result + getDestination().hashCode();
        result = 31 * result + Objects.hashCode(getBookings());
        return result;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "flightId=" + flightId +
                ", flightNumber='" + flightNumber + '\'' +
                ", departure='" + departure + '\'' +
                ", destination='" + destination + '\'' +
                '}';
    }
}
