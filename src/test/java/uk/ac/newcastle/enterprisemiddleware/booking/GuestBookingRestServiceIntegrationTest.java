package uk.ac.newcastle.enterprisemiddleware.booking;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerDTO;
import uk.ac.newcastle.enterprisemiddleware.taxi.Taxi;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTestResource(H2DatabaseTestResource.class)
public class GuestBookingRestServiceIntegrationTest {

    private static Long createdTaxiId;
    private static CustomerDTO customer;


    @Test
    @Order(1)
    void createInvalidGuestBooking_shouldRollbackTransaction() {
        customer = new CustomerDTO();
        customer.setFirstName("Barrack");
        customer.setLastName("Obama");
        customer.setEmail("barrack@obama.com");
        customer.setPhoneNumber("01234567898");

        Taxi taxi = new Taxi();
        taxi.setRegistration("testreg");
        taxi.setNumberOfSeats(10);

        Response taxiResponse =
                given().
                        contentType(ContentType.JSON).
                        body(taxi).
                when().
                        post("/taxis").
                then().
                        statusCode(201).
                        body("registration", is("testreg")).
                        extract().response();

        createdTaxiId = taxiResponse.body().as(Taxi.class).getId();

        // Invalid booking date
        LocalDate bookingDate = LocalDate.now().minusDays(1);

        GuestBooking guestBooking = new GuestBooking();
        guestBooking.setCustomerDTO(customer);
        guestBooking.setTaxiId(createdTaxiId);
        guestBooking.setBookingDate(bookingDate);

        // Test creating invalid guest booking
        given().
                contentType(ContentType.JSON).
                body(guestBooking).
        when().
                post("/guest-bookings").
        then().
                statusCode(400);

        // Test customer wasn't created
        Response customerResponse =
                when().
                        get("/customers").
                then().
                        statusCode(200).
                        extract().response();

        Customer[] result = customerResponse.body().as(Customer[].class);

        boolean customerFound = false;
        for (Customer c : result) {
            if (c.getFirstName().equals(customer.getFirstName()) &&
                    c.getLastName().equals(customer.getLastName()) &&
                    c.getEmail().equals(customer.getEmail()) &&
                    c.getPhoneNumber().equals(customer.getPhoneNumber())) {

                customerFound = true;
                break;
            }
        }

        Assertions.assertFalse(customerFound);
    }

    @Test
    @Order(2)
    void createGuestBooking_shouldCreateCustomerAndBooking() {
        LocalDate bookingDate = LocalDate.now().plusDays(1);

        GuestBooking guestBooking = new GuestBooking();
        guestBooking.setCustomerDTO(customer);
        guestBooking.setTaxiId(createdTaxiId);
        guestBooking.setBookingDate(bookingDate);

        Response response =
            given().
                    contentType(ContentType.JSON).
                    body(guestBooking).
            when().
                    post("/guest-bookings").
            then().
                    statusCode(201).
                    extract().response();

        Booking bookingResult1 = response.body().as(Booking.class);

        // Test customer is created
        when().
                get("/customers/" + bookingResult1.getCustomer().getId()).
        then().
                statusCode(200).
                body("id", is(bookingResult1.getCustomer().getId().intValue())).
                body("firstName", is(customer.getFirstName())).
                body("lastName", is(customer.getLastName())).
                body("email", is(customer.getEmail())).
                body("phoneNumber", is(customer.getPhoneNumber()));

        // Test booking is created
        Response bookingResponse =
                when().
                        get("/bookings/customer/" + bookingResult1.getCustomer().getId()).
                then().
                        statusCode(200).
                        extract().response();

        Booking[] bookingResult = bookingResponse.body().as(Booking[].class);

        Assertions.assertTrue(bookingResult.length > 0);

        boolean bookingFound = false;
        for (Booking b : bookingResult) {
            if (b.getCustomer().getId().equals(bookingResult1.getCustomer().getId()) &&
                    b.getTaxi().getId().equals(createdTaxiId) &&
                    b.getDate().equals(bookingDate)) {

                bookingFound = true;
                break;
            }
        }

        Assertions.assertTrue(bookingFound);
    }
}
