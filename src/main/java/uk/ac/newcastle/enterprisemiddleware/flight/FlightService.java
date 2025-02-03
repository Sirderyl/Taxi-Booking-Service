package uk.ac.newcastle.enterprisemiddleware.flight;

import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import java.util.List;

@RegisterRestClient(configKey = "flight-api")
public interface FlightService {

    @GET
    @Path("/flights")
    List<Flight> getFlights();

    @GET
    @Path("/bookings/customer/{customerId:[0-9]+}")
    List<FlightBookingGetDTO2> getBookingsByCustomerId(@PathParam("customerId") Long customerId);

    @POST
    @Path("/bookings")
    FlightBookingGetDTO createBooking(@Parameter FlightBookingPostDTO flightBookingPostDTO);

    @DELETE
    @Path("/bookings/{id:[0-9]+}")
    void deleteBooking(@PathParam("id") Long id);
}
