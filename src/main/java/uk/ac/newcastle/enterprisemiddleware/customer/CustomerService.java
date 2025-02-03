package uk.ac.newcastle.enterprisemiddleware.customer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.logging.Logger;

@Dependent
public class CustomerService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    CustomerValidator customerValidator;

    @Inject
    CustomerRepository customerRepository;


    /**
     * Retrieves a Customer with the specified ID.
     * @param id the ID of the customer to retrieve
     * @return a Customer object with the matching ID, if found
     */
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    /**
     * Retrieves a Customer with the specified email.
     * @param email the email of the customer to retrieve
     * @return a Customer object with the matching email, if found
     */
    Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    /**
     * Retrieves a list of all customers.
     * @return a List of all customers in the database
     */
    List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Creates a new Customer and adds it to the database.
     * @param customer the customer to create
     */
    public Customer createCustomer(Customer customer) {
        log.info("CustomerService.createCustomer() - Creating " + customer.getFirstName() + " " +
                customer.getLastName());

        customerValidator.validateCustomer(customer);

        return customerRepository.create(customer);
    }

    /**
     * Updates a Customer's information in the database.
     * @param customer the customer with updated information
     */
    void updateCustomer(Customer customer) {
        log.info("CustomerService.updateCustomer() - Updating " + customer.getFirstName()
            + " " + customer.getLastName());

        customerValidator.validateCustomer(customer);
        customerRepository.update(customer);
    }

    /**
     * Deletes a Customer from the database.
     * @param customer the customer to delete
     */
    void deleteCustomer(Customer customer) {
        log.info("delete() - Deleting " + customer.toString());

        if (customer.getId() != null) {
            customerRepository.delete(customer);
        } else {
            log.info("delete() - No customer found");
        }
    }
}
