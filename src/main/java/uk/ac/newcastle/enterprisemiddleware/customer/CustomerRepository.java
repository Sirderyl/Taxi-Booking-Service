package uk.ac.newcastle.enterprisemiddleware.customer;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.logging.Logger;

@RequestScoped
public class CustomerRepository {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    EntityManager entityManager;

    Customer findById(Long id) {
        return entityManager.find(Customer.class, id);
    }

    List<Customer> findAll() {
        return entityManager.createNamedQuery("Customer.findAll", Customer.class).getResultList();
    }

    Customer findByEmail(String email) {
        TypedQuery<Customer> query = entityManager.createNamedQuery(Customer.FIND_BY_EMAIL, Customer.class)
                .setParameter("email", email);

        return query.getSingleResult();
    }

    @Transactional
    void update(Customer customer) {
        entityManager.merge(customer);
    }

    @Transactional
    Customer create(Customer customer) {
        log.info("CustomerRepository.create() - Creating " + customer.getFirstName() + " "
                + customer.getLastName());

        entityManager.persist(customer);

        return customer;
    }

    @Transactional
    void delete(Customer customer) {
        entityManager.remove(entityManager.contains(customer) ? customer : entityManager.merge(customer));
    }
}
