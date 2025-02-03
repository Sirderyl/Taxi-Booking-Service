package uk.ac.newcastle.enterprisemiddleware.taxi;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import uk.ac.newcastle.enterprisemiddleware.booking.Booking;
import uk.ac.newcastle.enterprisemiddleware.booking.BookingDTO;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTestResource(H2DatabaseTestResource.class)
public class TaxiRestServiceIntegrationTest {

    private static Taxi taxi;
    private static Long createdTaxiId;
    private static Long createdBookingId;

    @BeforeEach
    void setUp() {
        taxi = new Taxi();
        taxi.setRegistration("1234567");
        taxi.setNumberOfSeats(2);
    }

    @Test
    @Order(1)
    void createTaxi_shouldReturnCreatedStatus() {
        Response response =
            given().
                    contentType(ContentType.JSON).
                    body(taxi).
            when().
                    post("/taxis").
            then().
                    statusCode(201).
                    body("registration", is("1234567")).
                    extract().response();

        createdTaxiId = response.body().as(Taxi.class).getId();
    }

    @Test
    @Order(2)
    void getTaxiById_shouldReturnTaxi() {
        Response response =
                when().
                        get("/taxis/" + createdTaxiId).
                then().
                        statusCode(200).
                        body("registration", is("1234567")).
                        extract().response();

        Taxi result = response.body().as(Taxi.class);

        Assertions.assertEquals("1234567", result.getRegistration(), "Registration should match");
        Assertions.assertEquals(2, result.getNumberOfSeats(), "Number of seats should match");
    }

    @Test
    @Order(3)
    void getTaxiByRegistration_shouldReturnTaxi() {
        Response response =
                given().
                        contentType(ContentType.JSON).
                when().
                        get("/taxis/registration/" + taxi.getRegistration()).
                then().
                        statusCode(200).
                        body("registration", is("1234567")).
                        extract().response();

        Taxi result = response.body().as(Taxi.class);

        Assertions.assertEquals("1234567", result.getRegistration(), "Registration should match");
        Assertions.assertEquals(2, result.getNumberOfSeats(), "Number of seats should match");
    }

    @Test
    @Order(4)
    void getAllTaxis_shouldReturnListOfTaxis() {
        Response response =
                when().
                        get("/taxis").
                then().
                        statusCode(200).
                        extract().response();

        Taxi[] result = response.body().as(Taxi[].class);

        Assertions.assertTrue(result.length > 0);

        boolean taxiFound = false;
        for (Taxi t : result) {
            if (t.getRegistration().equals(taxi.getRegistration()) &&
                t.getNumberOfSeats() == taxi.getNumberOfSeats()) {

                taxiFound = true;
                break;
            }
        }

        Assertions.assertTrue(taxiFound);
    }

    @Test
    @Order(5)
    void createTaxi_duplicateRegistrationCausesError() {
        given().
                contentType(ContentType.JSON).
                body(taxi).
        when().
                post("/taxis").
        then().
                statusCode(409).
                body("reasons.registration", containsString("registration already exists"));
    }

    @Test
    @Order(6)
    void createTaxi_invalidNumberOfSeatsCausesError() {
        taxi.setNumberOfSeats(1);
        taxi.setRegistration("7654321");

        given().
                contentType(ContentType.JSON).
                body(taxi).
        when().
                post("/taxis").
        then().
                statusCode(400);

        taxi.setNumberOfSeats(21);

        given().
                contentType(ContentType.JSON).
                body(taxi).
        when().
                post("/taxis").
        then().
                statusCode(400);
    }

    @Test
    @Order(7)
    void updateTaxi_shouldModifyExistingTaxi() {
        taxi.setRegistration("abcdefg");
        taxi.setNumberOfSeats(20);
        taxi.setId(createdTaxiId);

        given().
                contentType(ContentType.JSON).
                body(taxi).
        when().
                put("/taxis/" + createdTaxiId).
        then().
                statusCode(200);

        Response response =
                when().
                        get("/taxis/" + createdTaxiId).
                then().
                        statusCode(200).
                        extract().response();

        Taxi result = response.body().as(Taxi.class);

        Assertions.assertEquals("abcdefg", result.getRegistration(), "Registration should match");
        Assertions.assertEquals(20, result.getNumberOfSeats(), "Number of seats should match");
    }

    @Test
    @Order(8)
    void createBookingForTaxi() {
        Customer customer = new Customer();
        customer.setFirstName("Jesse");
        customer.setLastName("Faden");
        customer.setEmail("jesse@faden.com");
        customer.setPhoneNumber("01234567898");

        Response customerResponse =
                given().
                        contentType(ContentType.JSON).
                        body(customer).
                when().
                        post("/customers").
                then().
                        statusCode(201).
                        body("firstName", is("Jesse")).
                        extract().response();

        Long createdCustomerId = customerResponse.body().as(Customer.class).getId();

        LocalDate bookingDate = LocalDate.now().plusDays(1);

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setCustomerId(createdCustomerId);
        bookingDTO.setTaxiId(createdTaxiId);
        bookingDTO.setBookingDate(bookingDate);

        Response bookingResponse =
                given().
                        contentType(ContentType.JSON).
                        body(bookingDTO).
                when().
                        post("/bookings").
                then().
                        statusCode(201).
                        body("date", is(bookingDTO.getBookingDate().toString())).
                        extract().response();

        createdBookingId = bookingResponse.body().as(Booking.class).getId();
    }

    @Test
    @Order(9)
    void deleteTaxi_shouldDeleteTaxiAndBooking() {
        when().
                delete("/taxis/" + createdTaxiId).
        then().
                statusCode(204);

        when().
                get("/bookings/" + createdBookingId).
        then().
                statusCode(404);
    }
}
