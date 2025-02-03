package uk.ac.newcastle.enterprisemiddleware.hotel;

import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import java.util.List;

@RegisterRestClient(configKey = "hotel-api")
public interface HotelService {

    @GET
    @Path("/hotels")
    List<Hotel> getHotels();

    @GET
    @Path("/booking")
    List<HotelBookingGetDTO2> getBookingsByCustomerId(@QueryParam("customerId") Long customerId);

    @POST
    @Path("/booking")
    HotelBookingGetDTO createBooking(@Parameter HotelBookingPostDTO hotelBookingPostDTO);

    @DELETE
    @Path("/booking/{id:[0-9]+}")
    void deleteBooking(@PathParam("id") Long id);
}
