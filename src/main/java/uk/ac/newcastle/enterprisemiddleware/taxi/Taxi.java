package uk.ac.newcastle.enterprisemiddleware.taxi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Range;
import uk.ac.newcastle.enterprisemiddleware.booking.Booking;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Set;

@Entity
@NamedQueries({
        @NamedQuery(name = Taxi.FIND_ALL, query = "SELECT t FROM Taxi t"),
        @NamedQuery(name = Taxi.FIND_BY_REGISTRATION, query = "SELECT t FROM Taxi t WHERE t.registration = :registration")
})
@XmlRootElement
@Table(name = "taxi", uniqueConstraints = @UniqueConstraint(columnNames = "registration"))
public class Taxi implements Serializable {

    public static final String FIND_ALL = "Taxi.findAll";
    public static final String FIND_BY_REGISTRATION = "Taxi.findByEmail";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotEmpty
    @Size(min = 7, max = 7, message = "Registration must be 7 characters long")
    @Column(name = "registration", length = 7, nullable = false)
    private String registration;

    @NotNull
    @Range(min = 2, max = 20)
    @Column(name = "number_of_seats")
    private int numberOfSeats;

    @JsonIgnore
    @OneToMany(mappedBy = "taxi", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Booking> bookings;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull @NotEmpty @Size(min = 7, max = 7) String getRegistration() {
        return registration;
    }

    public void setRegistration(@NotNull @NotEmpty @Size(min = 7, max = 7) String registration) {
        this.registration = registration;
    }

    @NotNull
    @Range(min = 2, max = 20)
    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(@NotNull @Range(min = 2, max = 20) int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public Set<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }
}
