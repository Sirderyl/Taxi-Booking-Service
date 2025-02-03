package uk.ac.newcastle.enterprisemiddleware.TravelAgentBooking;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.*;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.flight.FlightBookingGetDTO2;
import uk.ac.newcastle.enterprisemiddleware.flight.FlightService;
import uk.ac.newcastle.enterprisemiddleware.hotel.HotelBookingGetDTO2;
import uk.ac.newcastle.enterprisemiddleware.hotel.HotelService;
import uk.ac.newcastle.enterprisemiddleware.taxi.Taxi;
import uk.ac.newcastle.enterprisemiddleware.travelagent.TravelAgentBooking;
import uk.ac.newcastle.enterprisemiddleware.travelagent.TravelAgentDTO;
import uk.ac.newcastle.enterprisemiddleware.travelagent.TravelAgentService;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.is;

/**
 * These tests are excluded from running as the API this depends on is no longer available.
 */

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTestResource(H2DatabaseTestResource.class)
public class TravelAgentRestServiceIntegrationTest {

    private static final Long FLIGHT_ID = 4L;
    private static final Long HOTEL_ID = 4L;
    private static final Long HOTEL_CUSTOMER_ID = 14L;
    private static final Long FLIGHT_CUSTOMER_ID = 6L;

    private static Long createdCustomerId;
    private static Long createdTaxiId;
    private static Long createdTravelAgentBookingId;

    private static Customer customer;
    private static Taxi taxi;

    private static LocalDate taxiBookingDate;
    private static LocalDate flightBookingDate;
    private static LocalDate hotelBookingDate;

    @Inject
    TravelAgentService travelAgentService;

    @RestClient
    FlightService flightService;

    @RestClient
    HotelService hotelService;


    //@Test
    //@Order(1)
    void createInvalidTravelAgentBooking_shouldRollbackTransaction() {
        taxiBookingDate = LocalDate.of(2024, 12, 26);
        flightBookingDate = LocalDate.of(2024, 12, 26);

        // INVALID DATE
        hotelBookingDate = LocalDate.of(2024, 11, 10);

        customer = new Customer();
        customer.setFirstName("Alan");
        customer.setLastName("Wake");
        customer.setEmail("alan@wake.com");
        customer.setPhoneNumber("01234567898");

        Response customerResponse =
                given().
                        contentType(ContentType.JSON).
                        body(customer).
                when().
                        post("/customers").
                then().
                        statusCode(201).
                        body("lastName", is("Wake")).
                        extract().response();

        createdCustomerId = customerResponse.body().as(Customer.class).getId();
        customer.setId(createdCustomerId);

        taxi = new Taxi();
        taxi.setRegistration("tabreg1");
        taxi.setNumberOfSeats(7);

        Response taxiResponse =
                given().
                        contentType(ContentType.JSON).
                        body(taxi).
                when().
                        post("/taxis").
                then().
                        statusCode(201).
                        body("registration", is("tabreg1")).
                        extract().response();

        createdTaxiId = taxiResponse.body().as(Taxi.class).getId();
        taxi.setId(createdTaxiId);

        TravelAgentDTO tab = new TravelAgentDTO();
        tab.setCustomerId(createdCustomerId);
        tab.setTaxiId(createdTaxiId);
        tab.setFlightId(FLIGHT_ID);
        tab.setHotelId(HOTEL_ID);
        tab.setTaxiBookingDate(taxiBookingDate);
        tab.setFlightBookingDate(flightBookingDate);
        tab.setHotelBookingDate(hotelBookingDate);

        // Test creating invalid TravelAgent Booking
        given().
                contentType(ContentType.JSON).
                body(tab).
        when().
                post("/travel-agent").
        then().
                statusCode(500);

        // ******* TEST THE BOOKING DOESN'T EXIST ANYWHERE ******
        // Taxi
        when().
                get("/bookings/customer/" + createdCustomerId).
        then().
                statusCode(404);

        // Flight
        List<FlightBookingGetDTO2> flightBookings = flightService.getBookingsByCustomerId(FLIGHT_CUSTOMER_ID);
        Assertions.assertTrue(flightBookings.isEmpty());

        // Hotel
        List<HotelBookingGetDTO2> hotelBookings = hotelService.getBookingsByCustomerId(HOTEL_CUSTOMER_ID);
        Assertions.assertTrue(hotelBookings.isEmpty());
    }

    //@Test
    //@Order(2)
    void createTravelAgentBooking_shouldCreateAllBookings() {
        taxiBookingDate = LocalDate.of(2024, 12, 26);
        flightBookingDate = LocalDate.of(2024, 12, 26);
        hotelBookingDate = LocalDate.of(2024, 12, 26);


        TravelAgentDTO tab = new TravelAgentDTO();
        tab.setCustomerId(createdCustomerId);
        tab.setTaxiId(createdTaxiId);
        tab.setFlightId(FLIGHT_ID);
        tab.setHotelId(HOTEL_ID);
        tab.setTaxiBookingDate(taxiBookingDate);
        tab.setFlightBookingDate(flightBookingDate);
        tab.setHotelBookingDate(hotelBookingDate);

        Response travelAgentResponse =
            given().
                    contentType(ContentType.JSON).
                    body(tab).
            when().
                    post("/travel-agent").
            then().
                    statusCode(201).
                    extract().response();

        createdTravelAgentBookingId = travelAgentResponse.body().as(TravelAgentBooking.class).getId();

        // ******* TEST THE BOOKING EXISTS EVERYWHERE ******
        // Taxi
        when().
                get("/bookings/customer/" + createdCustomerId).
        then().
                statusCode(200);

        // Flight
        List<FlightBookingGetDTO2> flightBookings = flightService.getBookingsByCustomerId(FLIGHT_CUSTOMER_ID);

        Assertions.assertFalse(flightBookings.isEmpty());

        // Hotel
        List<HotelBookingGetDTO2> hotelBookings = hotelService.getBookingsByCustomerId(HOTEL_CUSTOMER_ID);
        Assertions.assertFalse(hotelBookings.isEmpty());
    }

    //@Test
    //@Order(3)
    void getTravelAgentBookings_shouldReturnTheCreatedBooking() {
        TravelAgentBooking tab = travelAgentService.getTravelAgentBookingById(createdTravelAgentBookingId);

        Assertions.assertNotNull(tab);
        Assertions.assertEquals(createdTravelAgentBookingId, tab.getId());
        Assertions.assertEquals(customer.getEmail(), tab.getCustomer().getEmail());
        Assertions.assertEquals(taxi.getRegistration(), tab.getTaxi().getRegistration());
        Assertions.assertEquals(taxiBookingDate, tab.getTaxiBookingDate());
        Assertions.assertEquals(flightBookingDate, tab.getFlightBookingDate());
        Assertions.assertEquals(hotelBookingDate, tab.getFlightBookingDate());
        Assertions.assertEquals(FLIGHT_ID, tab.getFlightId());
        Assertions.assertEquals(HOTEL_ID, tab.getHotelId());
    }

    //@Test
    //@Order(4)
    void deleteTravelAgentBooking_shouldDeleteOnAllServices() {
        when().
                delete("/travel-agent/" + createdTravelAgentBookingId).
        then().
                statusCode(204);

        when().
                get("/bookings/customer/" + createdCustomerId).
        then().
                statusCode(404);

        // Flight
        List<FlightBookingGetDTO2> flightBookings = flightService.getBookingsByCustomerId(FLIGHT_CUSTOMER_ID);

        Assertions.assertTrue(flightBookings.isEmpty());

        // Hotel
        List<HotelBookingGetDTO2> hotelBookings = hotelService.getBookingsByCustomerId(HOTEL_CUSTOMER_ID);
        Assertions.assertTrue(hotelBookings.isEmpty());
    }
}
