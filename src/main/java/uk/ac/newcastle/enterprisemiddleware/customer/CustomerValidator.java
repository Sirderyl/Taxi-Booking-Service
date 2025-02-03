package uk.ac.newcastle.enterprisemiddleware.customer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class CustomerValidator {

    @Inject
    Validator validator;

    @Inject
    CustomerRepository customerRepository;

    void validateCustomer(Customer customer) {
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        if (emailAlreadyExists(customer.getEmail(), customer.getId())) {
            throw new UniqueEmailException("Unique email violation");
        }
    }

    boolean emailAlreadyExists(String email, Long id) {
        Customer customer = null;
        Customer customerWithID = null;

        try {
            customer = customerRepository.findByEmail(email);
        } catch (NoResultException e) {
            // ignore
        }

        if (customer != null && id != null) {
            try {
                customerWithID = customerRepository.findById(id);
                if (customerWithID != null && customerWithID.getEmail().equals(email)) {
                    customer = null;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }

        return customer != null;
    }
}
