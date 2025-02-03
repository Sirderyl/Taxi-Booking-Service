package uk.ac.newcastle.enterprisemiddleware.booking;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.reactive.Cache;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerService;
import uk.ac.newcastle.enterprisemiddleware.taxi.Taxi;
import uk.ac.newcastle.enterprisemiddleware.taxi.TaxiService;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Path("/bookings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BookingRestService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    BookingService bookingService;

    @Inject
    CustomerService customerService;

    @Inject
    TaxiService taxiService;


    @GET
    @Cache
    @Path("/{id:[0-9]+}")
    @Operation(
            summary = "Fetch a Booking by ID",
            description = "Returns a JSON representation of the Booking object with the provided ID."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Booking found"),
            @APIResponse(responseCode = "404", description = "Booking with ID not found.")
    })
    public Response getBookingById(
            @Parameter(description = "ID of Booking to be fetched")
            @Schema(minimum = "0", required = true)
            @PathParam("id")
            Long id) {

        Booking booking = bookingService.getBookingById(id);
        if (booking == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(booking).build();
    }

    @GET
    @Cache
    @Path("/customer/{customerId:[0-9]+}")
    @Operation(
            summary = "Fetch all bookings for customer ID",
            description = "Returns a JSON representation of all Booking objects with the provided customer ID."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Booking found"),
            @APIResponse(responseCode = "404", description = "Booking with customer ID not found.")
    })
    public Response getBookingsByCustomerId(
            @Parameter(description = "Customer ID associated")
            @Schema(minimum = "0", required = true)
            @PathParam("customerId")
            Long customerId) {

        List<Booking> booking = bookingService.getBookingsByCustomerId(customerId);

        if (booking.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(bookingService.getBookingsByCustomerId(customerId)).build();
    }

    @POST
    @Operation(summary = "Create a new Booking", description = "Add a new Booking to the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Booking created successfully."),
            @APIResponse(responseCode = "400", description = "Invalid Booking supplied in request body"),
            @APIResponse(responseCode = "409", description = "Booking supplied in request body conflicts with an existing Booking"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response createBooking(
            @Parameter(description = "JSON representation of Booking object to be added to the database", required = true)
            BookingDTO bookingDTO) {

        Customer customer = customerService.getCustomerById(bookingDTO.getCustomerId());
        Taxi taxi = taxiService.getTaxiById(bookingDTO.getTaxiId());

        if (customer == null || taxi == null) {
            throw new RestServiceException("Customer or Taxi null", Response.Status.BAD_REQUEST);
        }

        Booking booking = new Booking();
        booking.setId(null);
        booking.setCustomer(customer);
        booking.setTaxi(taxi);
        booking.setDate(bookingDTO.getBookingDate());

        Response.ResponseBuilder builder;

        try {
            bookingService.createBooking(booking);
            builder = Response.status(Response.Status.CREATED).entity(booking);
        } catch (ConstraintViolationException e) {
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }

            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, e);
        } catch (UniqueBookingException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("message", "A booking for this taxi and date already exists");
            throw new RestServiceException("Booking exists", responseObj, Response.Status.CONFLICT, e);
        }
        catch (Exception e) {
            throw new RestServiceException(e);
        }

        return builder.build();
    }

    @DELETE
    @Path("/{id:[0-9]+}")
    @Operation(summary = "Delete an existing Booking record", description = "Deletes a Booking from the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "The booking has been successfully deleted"),
            @APIResponse(responseCode = "400", description = "Invalid Booking ID supplied"),
            @APIResponse(responseCode = "404", description = "Booking with ID not found"),
            @APIResponse(responseCode = "409", description = "Booking cannot be deleted due to TravelAgent association"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response deleteBooking(
            @Parameter(description = "ID of Booking to be deleted", required = true)
            @Schema(minimum = "0")
            @PathParam("id")
            Long id) {

        Response.ResponseBuilder builder;

        Booking booking = bookingService.getBookingById(id);

        if (booking == null) {
            throw new RestServiceException("Booking with ID " + id + " not found", Response.Status.NOT_FOUND);
        }

        if (booking.getTab() != null) {
            throw new RestServiceException("Booking with ID: " + id + " is associated with a TravelAgentBooking and cannot be deleted",
                    Response.Status.CONFLICT);
        }

        try {
            bookingService.deleteBooking(booking);
            builder = Response.noContent();
        } catch (Exception e) {
            throw new RestServiceException(e);
        }

        log.info("Booking with ID " + id + " deleted successfully");

        return builder.build();
    }
}
