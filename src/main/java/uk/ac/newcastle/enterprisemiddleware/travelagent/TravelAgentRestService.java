package uk.ac.newcastle.enterprisemiddleware.travelagent;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import uk.ac.newcastle.enterprisemiddleware.booking.Booking;
import uk.ac.newcastle.enterprisemiddleware.booking.BookingService;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerService;
import uk.ac.newcastle.enterprisemiddleware.flight.*;
import uk.ac.newcastle.enterprisemiddleware.hotel.*;
import uk.ac.newcastle.enterprisemiddleware.taxi.Taxi;
import uk.ac.newcastle.enterprisemiddleware.taxi.TaxiService;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Path("/travel-agent")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TravelAgentRestService {

    private static final Long HOTEL_CUSTOMER_ID = 14L;
    private static final Long FLIGHT_CUSTOMER_ID = 6L;

    @Inject
    TravelAgentService travelAgentService;

    @Inject
    CustomerService customerService;

    @Inject
    TaxiService taxiService;

    @Inject
    @RestClient
    FlightService flightService;

    @Inject
    @RestClient
    HotelService hotelService;

    @Inject
    BookingService taxiBookingService;

    @Inject
    UserTransaction transaction;

    @Inject
    @Named("logger")
    Logger log;

    @GET
    @Operation(summary = "Fetch all Travel Agent Bookings", description = "Returns a JSON array of the Travel Agent Booking fields")
    public Response getAllTravelAgentBookings() {
        return Response.ok(travelAgentService.getAllTravelAgentBookings()).build();
    }

    @POST
    @Operation(summary = "Create Travel Agent Booking", description = "Creates a Taxi, Flight, and Hotel bookings")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Booking created successfully."),
            @APIResponse(responseCode = "400", description = "Invalid Booking supplied in request body"),
            @APIResponse(responseCode = "409", description = "Booking supplied in request body conflicts with an existing Booking"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    public Response createTravelAgentBooking(
            @Parameter(description = "JSON of TravelAgentBooking properties", required = true)
            TravelAgentDTO dto) throws SystemException {

        Customer customer = customerService.getCustomerById(dto.getCustomerId());
        Taxi taxi = taxiService.getTaxiById(dto.getTaxiId());
        List<Flight> flights;
        List<Hotel> hotels;

        try {
            flights = flightService.getFlights();
            hotels = hotelService.getHotels();
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == Response.Status.SERVICE_UNAVAILABLE.getStatusCode()) {
                throw new RestServiceException("External services (flight or hotel) are currently unavailable.", Response.Status.SERVICE_UNAVAILABLE);
            } else {
                throw e;
            }
        }

        if (customer == null) {
            throw new RestServiceException("Customer not found", Response.Status.NOT_FOUND);
        }
        if (taxi == null) {
            throw new RestServiceException("Taxi not found", Response.Status.NOT_FOUND);
        }
        if (flights == null || flights.isEmpty()) {
            throw new RestServiceException("No flights found", Response.Status.NOT_FOUND);
        }
        if (hotels == null || hotels.isEmpty()) {
            throw new RestServiceException("No hotels found", Response.Status.NOT_FOUND);
        }

        log.info("Flights retrieved: " + flights);

        Flight flight = flights.stream()
                .filter(f -> f.getFlightId().equals(dto.getFlightId()))
                .findFirst()
                .orElseThrow(() -> new RestServiceException("Flight not found", Response.Status.NOT_FOUND));

        log.info("Flight found: " + flight);

        Hotel hotel = hotels.stream()
                .filter(h -> h.getId().equals(dto.getHotelId()))
                .findFirst()
                .orElseThrow(() -> new RestServiceException("Hotel not found", Response.Status.NOT_FOUND));

        log.info("Hotel found: " + hotel);

        Response.ResponseBuilder builder;

        TravelAgentBooking tab = new TravelAgentBooking();
        tab.setId(null);
        tab.setCustomer(customer);
        tab.setTaxi(taxi);
        tab.setFlightId(flight.getFlightId());
        tab.setHotelId(hotel.getId());
        tab.setTaxiBookingDate(dto.getTaxiBookingDate());
        tab.setFlightBookingDate(dto.getFlightBookingDate());
        tab.setHotelBookingDate(dto.getHotelBookingDate());

        Booking booking = new Booking();
        booking.setId(null);
        booking.setCustomer(customer);
        booking.setTaxi(taxi);
        booking.setDate(dto.getTaxiBookingDate());

        FlightBookingPostDTO fb = new FlightBookingPostDTO();
        fb.setCustomerId(FLIGHT_CUSTOMER_ID);
        fb.setFlightId(dto.getFlightId());
        fb.setDate(dto.getFlightBookingDate());

        HotelBookingPostDTO hb = new HotelBookingPostDTO();
        hb.setCustomerId(HOTEL_CUSTOMER_ID);
        hb.setHotelId(dto.getHotelId());
        hb.setBookingDate(dto.getHotelBookingDate());

        boolean taxiBookingCreated = false;
        boolean flightBookingCreated = false;
        boolean hotelBookingCreated = false;
        boolean travelAgentBookingCreated = false;

        try {
            transaction.begin();

            Booking taxiBooking = taxiBookingService.createBooking(booking);
            tab.setTaxiBooking(taxiBooking);
            taxiBookingCreated = true;

            FlightBookingGetDTO flightBooking = flightService.createBooking(fb);
            tab.setFlightBookingId(flightBooking.getBookingId());
            flightBookingCreated = true;

            HotelBookingGetDTO hotelBooking = hotelService.createBooking(hb);
            tab.setHotelBookingId(hotelBooking.getId());
            hotelBookingCreated = true;

            tab = travelAgentService.createTravelAgentBooking(tab);

            transaction.commit();

            builder = Response.status(Response.Status.CREATED).entity(tab);
        } catch (ConstraintViolationException e) {
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }

            transaction.rollback();
            rollbackBookings(taxiBookingCreated, flightBookingCreated, hotelBookingCreated, travelAgentBookingCreated, booking, tab);
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, e);
        } catch (WebApplicationException e) {
            transaction.rollback();
            rollbackBookings(taxiBookingCreated, flightBookingCreated, hotelBookingCreated, travelAgentBookingCreated, booking, tab);
            throw new RestServiceException("External service error: " + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR, e);
        }
        catch (Exception e) {
            transaction.rollback();
            rollbackBookings(taxiBookingCreated, flightBookingCreated, hotelBookingCreated, travelAgentBookingCreated, booking, tab);
            throw new RestServiceException("An unexpected error occurred whilst processing the request", Response.Status.INTERNAL_SERVER_ERROR, e);
        }

        return builder.build();
    }

    @DELETE
    @Path("/{id:[0-9]+}")
    @Operation(summary = "Delete an existing TravelAgent Booking", description = "Deletes the bookings for Taxi, Flight, and Hotel")
    public Response deleteTravelAgentBooking(
            @Parameter(description = "ID of the TravelAgent Booking to be deleted", required = true)
            @Schema(minimum = "0")
            @PathParam("id")
            Long id) {

        TravelAgentBooking tab = travelAgentService.getTravelAgentBookingById(id);
        if (tab == null) {
            throw new RestServiceException("Travel Agent Booking not found with ID: " + id, Response.Status.NOT_FOUND);
        }

        Response.ResponseBuilder builder;

        List<FlightBookingGetDTO2> flightBookings;
        List<HotelBookingGetDTO2> hotelBookings;

        try {
            flightBookings = flightService.getBookingsByCustomerId(FLIGHT_CUSTOMER_ID);
            hotelBookings = hotelService.getBookingsByCustomerId(HOTEL_CUSTOMER_ID);
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == Response.Status.SERVICE_UNAVAILABLE.getStatusCode()) {
                throw new RestServiceException("External services (flight or hotel) are currently unavailable.", Response.Status.SERVICE_UNAVAILABLE);
            } else {
                throw e;
            }
        }

        if (flightBookings == null || flightBookings.isEmpty()) {
            throw new RestServiceException("No flights found", Response.Status.NOT_FOUND);
        }
        if (hotelBookings == null || hotelBookings.isEmpty()) {
            throw new RestServiceException("No hotels found", Response.Status.NOT_FOUND);
        }

        log.info("Flight Bookings retrieved: " + flightBookings);
        log.info("Hotel Bookings retrieved: " + hotelBookings);

        FlightBookingGetDTO2 flightBooking = flightBookings.stream()
                .filter(fb -> fb.getBookingId().equals(tab.getFlightBookingId()))
                .findFirst()
                .orElseThrow(() -> new RestServiceException("Flight not found", Response.Status.NOT_FOUND));

        log.info("Flight Booking found: " + flightBooking);

        HotelBookingGetDTO2 hotelBooking = hotelBookings.stream()
                .filter(hb -> hb.getId().equals(tab.getHotelBookingId()))
                .findFirst()
                .orElseThrow(() -> new RestServiceException("Hotel not found", Response.Status.NOT_FOUND));

        log.info("Hotel Booking found: " + hotelBooking);


        try {
            log.info("Got here");
            hotelService.deleteBooking(hotelBooking.getId());
            log.info("hotel booking deleted");
            flightService.deleteBooking(flightBooking.getBookingId());
            log.info("flight booking deleted");
            travelAgentService.deleteTravelAgentBooking(tab);
            log.info("travel agent booking deleted");

            builder = Response.noContent();
        } catch (Exception e) {
            throw new RestServiceException(e);
        }

        return builder.build();
    }

    private void rollbackBookings(boolean taxiBookingCreated,
                                  boolean flightBookingCreated,
                                  boolean hotelBookingCreated,
                                  boolean travelAgentBookingCreated,
                                  Booking booking,
                                  TravelAgentBooking tab){

        try {
            if (taxiBookingCreated && !travelAgentBookingCreated) {
                taxiBookingService.deleteBooking(booking);
            }
            if (flightBookingCreated) {
                flightService.deleteBooking(tab.getFlightBookingId());
            }
            if (hotelBookingCreated) {
                hotelService.deleteBooking(tab.getHotelBookingId());
            }
            if (travelAgentBookingCreated) {
                travelAgentService.deleteTravelAgentBooking(tab);
            }
        } catch (Exception e) {
            log.severe("Failed to rollback bookings: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
