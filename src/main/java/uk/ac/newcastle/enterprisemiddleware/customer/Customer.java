package uk.ac.newcastle.enterprisemiddleware.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import uk.ac.newcastle.enterprisemiddleware.booking.Booking;

import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Set;

@Entity
@NamedQueries({
        @NamedQuery(name = Customer.FIND_ALL, query = "SELECT c FROM Customer c ORDER BY c.lastName ASC, c.firstName ASC"),
        @NamedQuery(name = Customer.FIND_BY_EMAIL, query = "SELECT c FROM Customer c WHERE c.email = :email")
})
@XmlRootElement
@Table(name = "customer", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Customer implements Serializable {

    public static final String FIND_ALL = "Customer.findAll";
    public static final String FIND_BY_EMAIL = "Customer.findByEmail";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[A-Za-z-']+", message = "Please use a name without numbers or specials")
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[A-Za-z-']+", message = "Please use a name without numbers or specials")
    @Column(name = "last_name")
    private String lastName;

    @NotNull
    @NotEmpty
    @Email(message = "The email address must be in the format of name@domain.com")
    private String email;

    @NotNull
    @Pattern(regexp = "^0[0-9]{10}$", message = "Invalid phone number format")
    @Column(name = "phone_number")
    private String phoneNumber;

    @JsonIgnore
    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Booking> bookings;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull @NotEmpty @Size(min = 1, max = 25) @Pattern(regexp = "[A-Za-z-']+", message = "Please use a name without numbers or specials") String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotNull @NotEmpty @Size(min = 1, max = 25) @Pattern(regexp = "[A-Za-z-']+", message = "Please use a name without numbers or specials") String firstName) {
        this.firstName = firstName;
    }

    public @NotNull @NotEmpty @Size(min = 1, max = 25) @Pattern(regexp = "[A-Za-z-']+", message = "Please use a name without numbers or specials") String getLastName() {
        return lastName;
    }

    public void setLastName(@NotNull @NotEmpty @Size(min = 1, max = 25) @Pattern(regexp = "[A-Za-z-']+", message = "Please use a name without numbers or specials") String lastName) {
        this.lastName = lastName;
    }

    public @NotNull @NotEmpty @Email(message = "The email address must be in the format of name@domain.com") String getEmail() {
        return email;
    }

    public void setEmail(@NotNull @NotEmpty @Email(message = "The email address must be in the format of name@domain.com") String email) {
        this.email = email;
    }

    public @NotNull @Pattern(regexp = "^0[0-9]{10}$") String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@NotNull @Pattern(regexp = "^0[0-9]{10}$") String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }
}
