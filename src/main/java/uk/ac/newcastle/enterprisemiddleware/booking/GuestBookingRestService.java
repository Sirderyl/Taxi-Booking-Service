package uk.ac.newcastle.enterprisemiddleware.booking;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerService;
import uk.ac.newcastle.enterprisemiddleware.taxi.Taxi;
import uk.ac.newcastle.enterprisemiddleware.taxi.TaxiService;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.inject.Inject;
import javax.transaction.*;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Path("/guest-bookings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GuestBookingRestService {

    @Inject
    CustomerService customerService;

    @Inject
    BookingService bookingService;

    @Inject
    UserTransaction transaction;

    @Inject
    TaxiService taxiService;


    @POST
    @Operation(
            summary = "Create a new Customer and Booking",
            description = "Add a new Customer and Booking to the database <br>" +
                    "<b>Email</b>: Must be in format email@domain.com <br>" +
                    "<b>Phone Number</b>: Must start with 0 and have 11 numbers in total <br>" +
                    "<b>Booking date</b>: Must be a date in the future and in format YYYY-MM-DD"
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Customer & Booking created successfully."),
            @APIResponse(responseCode = "400", description = "Invalid Customer/Booking supplied in request body"),
            @APIResponse(responseCode = "409", description = "Customer/Booking supplied in request body conflicts with an existing Customer/Booking"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    public Response createGuestBooking(
            @Parameter(description = "JSON representation of Customer and Booking objects to be added to the database", required = true)
            GuestBooking guestBooking) throws SystemException {

        if (guestBooking == null || guestBooking.getCustomerDTO() == null) {
            throw new RestServiceException("Please fill out all the required fields", Response.Status.BAD_REQUEST);
        }

        Customer customer = new Customer();
        customer.setId(null);
        customer.setFirstName(guestBooking.getCustomerDTO().getFirstName());
        customer.setLastName(guestBooking.getCustomerDTO().getLastName());
        customer.setEmail(guestBooking.getCustomerDTO().getEmail());
        customer.setPhoneNumber(guestBooking.getCustomerDTO().getPhoneNumber());

        Taxi taxi = taxiService.getTaxiById(guestBooking.getTaxiId());
        if (taxi == null) {
            throw new RestServiceException("Invalid Taxi ID", Response.Status.BAD_REQUEST);
        }

        LocalDate bookingDate = guestBooking.getBookingDate();

        Booking booking = new Booking();
        booking.setTaxi(taxi);
        booking.setDate(bookingDate);

        Response.ResponseBuilder builder;

        try {
            transaction.begin();

            customer = customerService.createCustomer(customer);
            booking.setCustomer(customer);

            bookingService.createBooking(booking);

            transaction.commit();
            builder = Response.status(Response.Status.CREATED).entity(booking);
        } catch (ConstraintViolationException e) {
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }

            transaction.rollback();
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, e);
        } catch (SystemException | NotSupportedException | HeuristicRollbackException | HeuristicMixedException |
                 RollbackException e) {

            transaction.rollback();
            throw new RuntimeException(e);
        }

        return builder.build();
    }
}
