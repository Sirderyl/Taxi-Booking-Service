package uk.ac.newcastle.enterprisemiddleware.booking;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

@RequestScoped
public class BookingRepository {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    EntityManager entityManager;

    Booking findById(Long id) {
        return entityManager.find(Booking.class, id);
    }

    List<Booking> findAllByCustomer(Long customerId) {
        return entityManager.createNamedQuery(Booking.FIND_BY_CUSTOMER, Booking.class)
                .setParameter("customerId", customerId).getResultList();
    }

    Booking findByTaxiAndDate(Long taxiId, LocalDate bookingDate) {
        return entityManager.createNamedQuery(Booking.FIND_BY_TAXI_AND_DATE, Booking.class)
                .setParameter("taxiId", taxiId)
                .setParameter("date", bookingDate)
                .getSingleResult();
    }

    @Transactional
    Booking create(Booking booking) {
        log.info("BookingRepository.create() - Creating: " + booking.toString());
        entityManager.persist(booking);
        return booking;
    }

    @Transactional
    void delete(Booking booking) {
        entityManager.remove(entityManager.contains(booking) ? booking : entityManager.merge(booking));
    }
}
