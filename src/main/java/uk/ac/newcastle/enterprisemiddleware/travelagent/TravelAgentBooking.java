package uk.ac.newcastle.enterprisemiddleware.travelagent;

import uk.ac.newcastle.enterprisemiddleware.booking.Booking;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.taxi.Taxi;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@NamedQueries({
        @NamedQuery(name = TravelAgentBooking.FIND_BY_CUSTOMER, query = "SELECT t FROM TravelAgentBooking t WHERE t.customer.id = :customerId"),
        @NamedQuery(name = TravelAgentBooking.FIND_ALL, query = "SELECT t FROM TravelAgentBooking t"),
        @NamedQuery(name = TravelAgentBooking.FIND_BY_TAXI_AND_DATE, query = "SELECT t FROM TravelAgentBooking t WHERE t.taxi.id = :taxiId AND t.taxiBookingDate = :date"),
        @NamedQuery(name = TravelAgentBooking.FIND_BY_FLIGHT_AND_DATE, query = "SELECT t FROM TravelAgentBooking t WHERE t.flightId = :flightId AND t.flightBookingDate = :date"),
        @NamedQuery(name = TravelAgentBooking.FIND_BY_HOTEL_AND_DATE, query = "SELECT t FROM TravelAgentBooking t WHERE t.hotelId = :hotelId AND t.hotelBookingDate = :date")
})
@XmlRootElement
@Table(name = "travel_agent", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"taxi_id", "taxi_booking_date"}),
        @UniqueConstraint(columnNames = {"flight_id", "flight_booking_date"}),
        @UniqueConstraint(columnNames = {"hotel_id", "hotel_booking_date"})
})
public class TravelAgentBooking implements Serializable {

    public static final String FIND_ALL = "TravelAgentBooking.findAll";
    public static final String FIND_BY_CUSTOMER = "TravelAgentBooking.findByCustomer";
    public static final String FIND_BY_TAXI_AND_DATE = "TravelAgentBooking.findByTaxiAndDate";
    public static final String FIND_BY_FLIGHT_AND_DATE = "TravelAgentBooking.findByFlightAndDate";
    public static final String FIND_BY_HOTEL_AND_DATE = "TravelAgentBooking.findByHotelAndDate";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne()
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull
    @ManyToOne()
    @JoinColumn(name = "taxi_id", nullable = false)
    private Taxi taxi;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "taxi_booking_id", nullable = false)
    private Booking taxiBooking;

    @NotNull
    @Future(message = "The booking date must be in the future")
    @Column(name = "taxi_booking_date", nullable = false)
    private LocalDate taxiBookingDate;

    @NotNull
    @Column(name = "flight_id", nullable = false)
    private Long flightId;

    @NotNull
    @Column(name = "flight_booking_id", nullable = false)
    private Long flightBookingId;

    @NotNull
    @Future(message = "The booking date must be in the future")
    @Column(name = "flight_booking_date")
    private LocalDate flightBookingDate;

    @NotNull
    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @NotNull
    @Column(name = "hotel_booking_id", nullable = false)
    private Long hotelBookingId;

    @NotNull
    @Future(message = "The booking date must be in the future")
    @Column(name = "hotel_booking_date")
    private LocalDate hotelBookingDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull Customer getCustomer() {
        return customer;
    }

    public void setCustomer(@NotNull Customer customer) {
        this.customer = customer;
    }

    public @NotNull Taxi getTaxi() {
        return taxi;
    }

    public void setTaxi(@NotNull Taxi taxi) {
        this.taxi = taxi;
    }

    public @NotNull Booking getTaxiBooking() {
        return taxiBooking;
    }

    public void setTaxiBooking(@NotNull Booking booking) {
        this.taxiBooking = booking;
    }

    public @NotNull @Future(message = "The booking date must be in the future") LocalDate getTaxiBookingDate() {
        return taxiBookingDate;
    }

    public void setTaxiBookingDate(@NotNull @Future(message = "The booking date must be in the future") LocalDate taxiBookingDate) {
        this.taxiBookingDate = taxiBookingDate;
    }

    public @NotNull Long getFlightId() {
        return flightId;
    }

    public void setFlightId(@NotNull Long flightId) {
        this.flightId = flightId;
    }

    public @NotNull Long getFlightBookingId() {
        return flightBookingId;
    }

    public void setFlightBookingId(@NotNull Long flightBookingId) {
        this.flightBookingId = flightBookingId;
    }

    public @NotNull @Future(message = "The booking date must be in the future") LocalDate getFlightBookingDate() {
        return flightBookingDate;
    }

    public void setFlightBookingDate(@NotNull @Future(message = "The booking date must be in the future") LocalDate flightBookingDate) {
        this.flightBookingDate = flightBookingDate;
    }

    public @NotNull Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(@NotNull Long hotelId) {
        this.hotelId = hotelId;
    }

    public @NotNull Long getHotelBookingId() {
        return hotelBookingId;
    }

    public void setHotelBookingId(@NotNull Long hotelBookingId) {
        this.hotelBookingId = hotelBookingId;
    }

    public @NotNull @Future(message = "The booking date must be in the future") LocalDate getHotelBookingDate() {
        return hotelBookingDate;
    }

    public void setHotelBookingDate(@NotNull @Future(message = "The booking date must be in the future") LocalDate hotelBookingDate) {
        this.hotelBookingDate = hotelBookingDate;
    }
}
