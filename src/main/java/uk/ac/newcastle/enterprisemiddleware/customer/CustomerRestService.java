package uk.ac.newcastle.enterprisemiddleware.customer;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.reactive.Cache;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Path("/customers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CustomerRestService {

    @Inject
    @Named("logger")
    Logger log;

    private final CustomerService customerService;

    @Inject
    public CustomerRestService(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Endpoint to retrieve a customer by ID.
     * @param id the ID of the customer to retrieve
     * @return a response containing the customer or 404 if not found.
     */
    @GET
    @Cache
    @Path("/{id:[0-9]+}")
    @Operation(
            summary = "Fetch a Customer by ID",
            description = "Returns a JSON representation of the Customer object with the provided ID."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Contact found"),
            @APIResponse(responseCode = "404", description = "Contact with ID not found.")
    })
    public Response getCustomerById(
            @Parameter(description = "ID of Customer to be fetched")
            @Schema(minimum = "0", required = true)
            @PathParam("id")
            Long id) {

        Customer customer = customerService.getCustomerById(id);
        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        log.info("getCustomerById " + id + ": found Customer = " + customer);

        return Response.ok(customer).build();
    }

    /**
     * Endpoint to retrieve a customer by email.
     * @param email the email of the customer to retrieve
     * @return a response containing the customer or 404 if not found.
     */
    @GET
    @Cache
    @Path("/email/{email:.+[%40|@].+}")
    @Operation(
            summary = "Fetch a Customer by Email",
            description = "Returns a JSON representation of the Customer object with the provided email."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description ="Customer found"),
            @APIResponse(responseCode = "404", description = "Customer with email not found")
    })
    public Response getCustomerByEmail(
            @Parameter(description = "Email of Contact to be fetched", required = true)
            @PathParam("email")
            String email) {

        Customer customer;

        try {
            customer = customerService.getCustomerByEmail(email);
        } catch (NoResultException e) {
            throw new RestServiceException("No Contact with the email found: " + email, Response.Status.NOT_FOUND);
        }

        return Response.ok(customer).build();
    }

    /**
     * Endpoint to retrieve a list of all customers.
     * @return a response containing the list of customers
     */
    @GET
    @Operation(summary = "Fetch all Customers", description = "Returns a JSON array of all stored Customer objects.")
    public Response getAllCustomers() {
        return Response.ok(customerService.getAllCustomers()).build();
    }

    /**
     * Endpoint to create a new customer.
     * @param customerDTO the customer to create
     * @return a response with the created customer
     */
    @POST
    @Operation(summary = "Create a new Customer", description = "Add a new Customer to the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Customer created successfully."),
            @APIResponse(responseCode = "400", description = "Invalid Customer supplied in request body"),
            @APIResponse(responseCode = "409", description = "Customer supplied in request body conflicts with an existing Customer"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response createCustomer(
            @Parameter(description = "JSON representation of Customer object to be added to the database", required = true)
            CustomerDTO customerDTO) {

        if (customerDTO == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }

        Customer customer = new Customer();
        customer.setId(null);
        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        customer.setEmail(customerDTO.getEmail());
        customer.setPhoneNumber(customerDTO.getPhoneNumber());

        Response.ResponseBuilder builder;

        try {
            customerService.createCustomer(customer);
            builder = Response.status(Response.Status.CREATED).entity(customer);

        } catch (ConstraintViolationException e) {
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }

            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, e);

        } catch (UniqueEmailException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "That email is already used, please use a unique email");
            throw new RestServiceException("Customer details already in use", responseObj, Response.Status.CONFLICT, e);

        } catch (Exception e) {
            throw new RestServiceException(e);
        }

        log.info("createCustomer completed. Customer = " + customer);
        return builder.build();
    }

    /**
     * Endpoint to update an existing customer.
     * @param id the ID of the customer to update
     * @param updatedCustomerDTO the updated Customer information
     * @return a response with status 200 if updated, other statuses in case of issues
     */
    @PUT
    @Path("/{id:[0-9]+}")
    @Operation(summary = "Update a Customer record", description = "Update existing Customer in the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Customer updated successfully"),
            @APIResponse(responseCode = "400", description = "Invalid Customer supplied in request body"),
            @APIResponse(responseCode = "404", description = "Customer with id not found"),
            @APIResponse(responseCode = "409", description = "Customer details supplied in request body conflict with another existing Customer"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response updateCustomer(
            @Parameter(description = "ID of Customer to be updated", required = true)
            @Schema(minimum = "0")
            @PathParam("id")
            Long id,
            @Parameter(description = "JSON representation of Customer object to be updated in the database", required = true)
            @Valid
            CustomerDTO updatedCustomerDTO) {

        if (updatedCustomerDTO == null) {
            throw new RestServiceException("Invalid Customer supplied", Response.Status.BAD_REQUEST);
        }

        Customer existingCustomer = customerService.getCustomerById(id);

        if (existingCustomer == null) {
            throw new RestServiceException("No Customer with ID " + id + " found", Response.Status.NOT_FOUND);
        }

        Response.ResponseBuilder builder;

        try {
            existingCustomer.setFirstName(updatedCustomerDTO.getFirstName());
            existingCustomer.setLastName(updatedCustomerDTO.getLastName());
            existingCustomer.setEmail(updatedCustomerDTO.getEmail());
            existingCustomer.setPhoneNumber(updatedCustomerDTO.getPhoneNumber());

            customerService.updateCustomer(existingCustomer);

            builder = Response.ok(existingCustomer);
        } catch (ConstraintViolationException e) {
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, e);
        } catch (UniqueEmailException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "That email is already used, please use a unique email");
            throw new RestServiceException("Customer details already in use", responseObj, Response.Status.CONFLICT, e);
        } catch (Exception e) {
            throw new RestServiceException(e);
        }

        log.info("updateCustomer completed. Customer = " + updatedCustomerDTO);

        return builder.build();
    }

    /**
     * Endpoint to delete an existing customer.
     * @param id the ID of the customer to delete
     * @return a response with status 204 if deleted, other statuses in case of issues
     */
    @DELETE
    @Path("/{id:[0-9]+}")
    @Operation(summary = "Delete an existing Customer record", description = "Deletes a Customer from the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "The customer has been successfully deleted"),
            @APIResponse(responseCode = "400", description = "Invalid Customer ID supplied"),
            @APIResponse(responseCode = "404", description = "Customer with ID not found"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response deleteCustomer(
            @Parameter(description = "ID of Customer to be deleted", required = true)
            @Schema(minimum = "0")
            @PathParam("id")
            Long id) {

        Response.ResponseBuilder builder;

        Customer customer = customerService.getCustomerById(id);

        if (customer == null) {
            throw new RestServiceException("Customer with id " + id + " not found", Response.Status.NOT_FOUND);
        }

        try {
            customerService.deleteCustomer(customer);
            builder = Response.noContent();
        } catch (Exception e) {
            throw new RestServiceException(e);
        }

        log.info("deleteCustomer completed. Customer = " + customer);

        return builder.build();
    }
}
