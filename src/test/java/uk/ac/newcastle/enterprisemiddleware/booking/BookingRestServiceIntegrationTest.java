package uk.ac.newcastle.enterprisemiddleware.booking;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.taxi.Taxi;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTestResource(H2DatabaseTestResource.class)
public class BookingRestServiceIntegrationTest {

    private static Long createdBookingId;
    private static Long createdBookingId2;
    private static Customer createdCustomer;
    private static Taxi createdTaxi;
    private static LocalDate bookingDate;
    private static LocalDate bookingDate2;


    @Test
    @Order(1)
    void createBooking_shouldReturnCreatedStatus() {
        Customer customer = new Customer();
        customer.setFirstName("Abraham");
        customer.setLastName("Lincoln");
        customer.setEmail("abraham@lincoln.com");
        customer.setPhoneNumber("01234567898");

        Taxi taxi = new Taxi();
        taxi.setRegistration("7654321");
        taxi.setNumberOfSeats(4);

        Response customerResponse =
                given().
                        contentType(ContentType.JSON).
                        body(customer).
                when().
                        post("/customers").
                then().
                        statusCode(201).
                        body("firstName", is("Abraham")).
                        extract().response();

        createdCustomer = customerResponse.body().as(Customer.class);

        Response taxiResponse =
                given().
                        contentType(ContentType.JSON).
                        body(taxi).
                when().
                        post("/taxis").
                then().
                        statusCode(201).
                        body("registration", is("7654321")).
                        extract().response();

        createdTaxi = taxiResponse.body().as(Taxi.class);

        bookingDate = LocalDate.now().plusDays(1);
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setCustomerId(createdCustomer.getId());
        bookingDTO.setTaxiId(createdTaxi.getId());
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

        bookingDate2 = LocalDate.now().plusDays(2);
        BookingDTO bookingDTO2 = new BookingDTO();
        bookingDTO2.setCustomerId(createdCustomer.getId());
        bookingDTO2.setTaxiId(createdTaxi.getId());
        bookingDTO2.setBookingDate(bookingDate2);

        Response bookingResponse2 =
                given().
                        contentType(ContentType.JSON).
                        body(bookingDTO2).
                when().
                        post("/bookings").
                then().
                        statusCode(201).
                        body("date", is(bookingDTO2.getBookingDate().toString())).
                        extract().response();

        createdBookingId2 = bookingResponse2.body().as(Booking.class).getId();
    }

    @Test
    @Order(2)
    void getBookingById_shouldReturnBooking() {
        Response response =
                when().
                        get("/bookings/" + createdBookingId).
                then().
                        statusCode(200).
                        body("customer.id", is(createdCustomer.getId().intValue())).
                        body("customer.firstName", is(createdCustomer.getFirstName())).
                        body("customer.lastName", is(createdCustomer.getLastName())).
                        body("customer.email", is(createdCustomer.getEmail())).
                        body("customer.phoneNumber", is(createdCustomer.getPhoneNumber())).
                        body("taxi.id", is(createdTaxi.getId().intValue())).
                        body("taxi.registration", is(createdTaxi.getRegistration())).
                        body("taxi.numberOfSeats", is(createdTaxi.getNumberOfSeats())).
                        body("date", is(bookingDate.toString())).
                        extract().response();

        Booking result = response.body().as(Booking.class);

        Assertions.assertEquals(createdBookingId, result.getId());
        Assertions.assertEquals(createdCustomer.getId(), result.getCustomer().getId());
        Assertions.assertEquals(createdTaxi.getId(), result.getTaxi().getId());
        Assertions.assertEquals(bookingDate, result.getDate());
    }

    @Test
    @Order(3)
    void getAllBookingsForCustomer_shouldReturnListOfBookings() {
        Response response =
                when().
                        get("/bookings/customer/" + createdCustomer.getId()).
                then().
                        statusCode(200).
                        extract().response();

        Booking[] result = response.body().as(Booking[].class);

        Assertions.assertTrue(result.length > 1);

        boolean bookingFound = false;
        for (Booking b : result) {
            if (b.getCustomer().getId().equals(createdCustomer.getId()) &&
                b.getTaxi().getId().equals(createdTaxi.getId()) &&
                b.getDate().equals(bookingDate)) {

                bookingFound = true;
                break;
            }
        }

        Assertions.assertTrue(bookingFound);
    }

    @Test
    @Order(4)
    void deleteBooking_shouldDeleteBooking() {
        when().
                delete("/bookings/" + createdBookingId).
        then().
                statusCode(204);
    }

    @Test
    void deleteCustomer_shouldDeleteAllCustomerRelatedBookings() {
        // Test the second booking still exists
        when().
                get("/bookings/" + createdBookingId2).
        then().
                statusCode(200);

        // Delete customer
        when().
                delete("/customers/" + createdCustomer.getId()).
        then().
                statusCode(204);

        // No bookings for customer exist
        when().
                get("/bookings/customer/" + createdCustomer.getId()).
        then().
                statusCode(404);
    }
}
