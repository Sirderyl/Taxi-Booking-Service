package uk.ac.newcastle.enterprisemiddleware.booking;

import com.fasterxml.jackson.annotation.JsonIgnore;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.taxi.Taxi;
import uk.ac.newcastle.enterprisemiddleware.travelagent.TravelAgentBooking;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@NamedQueries({
        @NamedQuery(name = Booking.FIND_BY_CUSTOMER, query = "SELECT b FROM Booking b WHERE b.customer.id = :customerId"),
        @NamedQuery(name = Booking.FIND_BY_TAXI_AND_DATE, query = "SELECT b FROM Booking b WHERE b.taxi.id = :taxiId AND b.date = :date")
})
@XmlRootElement
@Table(name = "booking", uniqueConstraints = @UniqueConstraint(columnNames = {"taxi_id", "date"}))
public class Booking implements Serializable {

    public static final String FIND_BY_CUSTOMER = "Booking.findByCustomer";
    public static final String FIND_BY_TAXI_AND_DATE = "Booking.findByTaxiAndDate";

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
    @Future(message = "The booking date must be in the future")
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @JsonIgnore
    @OneToOne(mappedBy = "taxiBooking")
    private TravelAgentBooking tab;


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

    public @NotNull @Future LocalDate getDate() {
        return date;
    }

    public void setDate(@NotNull @Future LocalDate date) {
        this.date = date;
    }

    public TravelAgentBooking getTab() {
        return tab;
    }

    public void setTab(TravelAgentBooking tab) {
        this.tab = tab;
    }
}
