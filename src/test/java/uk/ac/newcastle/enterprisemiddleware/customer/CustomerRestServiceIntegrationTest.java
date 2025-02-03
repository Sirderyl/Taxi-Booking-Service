package uk.ac.newcastle.enterprisemiddleware.customer;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@TestHTTPEndpoint(CustomerRestService.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTestResource(H2DatabaseTestResource.class)
public class CustomerRestServiceIntegrationTest {

    private static Customer customer;
    private static Long createdCustomerId;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john@doe.com");
        customer.setPhoneNumber("07593743878");
    }

    @Test
    @Order(1)
    void createCustomer_shouldReturnCreatedStatus() {
        Response response =
            given().
                    contentType(ContentType.JSON).
                    body(customer).
            when().
                    post().
            then().
                    statusCode(201).
                    body("firstName", is("John")).
                    extract().response();

        createdCustomerId = response.body().as(Customer.class).getId();
    }

    @Test
    @Order(2)
    void getCustomerById_shouldReturnCustomer() {
        Response response =
            when().
                    get("/" + createdCustomerId).
            then().
                    statusCode(200).
                    body("firstName", is("John")).
                    extract().response();

        Customer result = response.body().as(Customer.class);

        Assertions.assertEquals("John", result.getFirstName(), "First name should match");
        Assertions.assertEquals("Doe", result.getLastName(),  "Last name should match");
        Assertions.assertEquals("john@doe.com", result.getEmail() , "Email should match");
        Assertions.assertEquals("07593743878", result.getPhoneNumber() ,
                "Phone number should match");
    }

    @Test
    @Order(3)
    void getCustomerByEmail_shouldReturnCustomer() {
        Response response =
            given().
                    contentType(ContentType.JSON).
            when().
                    get("/email/" + customer.getEmail()).
            then().
                    statusCode(200).
                    body("firstName", is("John")).
                    extract().response();

        Customer result = response.body().as(Customer.class);

        Assertions.assertEquals("John", result.getFirstName(), "First name should match");
        Assertions.assertEquals("Doe", result.getLastName(), "Last name should match");
        Assertions.assertEquals("john@doe.com", result.getEmail(), "Email should match");
        Assertions.assertEquals("07593743878", result.getPhoneNumber(),
                "Phone number should match");
    }

    @Test
    @Order(4)
    void getAllCustomers_shouldReturnListOfCustomers() {
        Response response =
                when().
                        get().
                then().
                        statusCode(200).
                        extract().response();

        Customer[] result = response.body().as(Customer[].class);

        Assertions.assertTrue(result.length > 0);

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

        Assertions.assertTrue(customerFound);
    }

    @Test
    @Order(5)
    void createCustomer_duplicateEmailCausesError() {
        given().
                contentType(ContentType.JSON).
                body(customer).
        when().
                post().
        then().
                statusCode(409).
                body("reasons.email", containsString("email is already used"));
    }

    @Test
    @Order(6)
    void createCustomer_wrongPhoneNumberCausesError() {
        Customer wrongPhoneNumber = new Customer();
        wrongPhoneNumber.setFirstName("Max");
        wrongPhoneNumber.setLastName("Payne");
        wrongPhoneNumber.setEmail("max@payne.com");
        wrongPhoneNumber.setPhoneNumber("17593743878");

        given().
                contentType(ContentType.JSON).
                body(wrongPhoneNumber).
        when().
                post().
        then().
                statusCode(400);
    }

    @Test
    @Order(7)
    void updateCustomer_shouldModifyExistingCustomer() {
        customer.setFirstName("Jack");
        customer.setId(createdCustomerId);

        given().
                contentType(ContentType.JSON).
                body(customer).
        when().
                put("/" + createdCustomerId).
        then().
                statusCode(200);

        Response response =
                when().
                        get("/" + createdCustomerId).
                then().
                        statusCode(200).
                        extract().response();

        Customer result = response.body().as(Customer.class);

        Assertions.assertEquals("Jack", result.getFirstName(), "First name not equal");
        Assertions.assertEquals("Doe", result.getLastName(), "Last name not equal");
    }

    @Test
    @Order(8)
    void deleteCustomer_shouldDeleteCustomer() {
        Response response =
                when().
                        get().
                then().
                        statusCode(200).
                        extract().response();

        Customer[] result = response.body().as(Customer[].class);

        when().
                delete(result[0].getId().toString()).
        then().
                statusCode(204);
    }
}
