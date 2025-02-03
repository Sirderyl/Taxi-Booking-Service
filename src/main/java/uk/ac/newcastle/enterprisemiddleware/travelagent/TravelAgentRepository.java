package uk.ac.newcastle.enterprisemiddleware.travelagent;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

@RequestScoped
public class TravelAgentRepository {

    @Inject
    EntityManager entityManager;

    @Inject
    @Named("logger")
    Logger log;

    TravelAgentBooking findById(Long id) {
        return entityManager.find(TravelAgentBooking.class, id);
    }

    List<TravelAgentBooking> findAllByCustomer(Long customerId) {
        return entityManager.createNamedQuery(TravelAgentBooking.FIND_BY_CUSTOMER, TravelAgentBooking.class)
                .setParameter("customerId", customerId).getResultList();
    }

    List<TravelAgentBooking> findAll() {
        return entityManager.createNamedQuery(TravelAgentBooking.FIND_ALL, TravelAgentBooking.class).getResultList();
    }

    TravelAgentBooking findByTaxiAndDate(Long taxiId, LocalDate bookingDate) {
        return entityManager.createNamedQuery(TravelAgentBooking.FIND_BY_TAXI_AND_DATE, TravelAgentBooking.class)
                .setParameter("taxiId", taxiId)
                .setParameter("date", bookingDate)
                .getSingleResult();
    }

    TravelAgentBooking findByFlightAndDate(Long flightId, LocalDate bookingDate) {
        return entityManager.createNamedQuery(TravelAgentBooking.FIND_BY_FLIGHT_AND_DATE, TravelAgentBooking.class)
                .setParameter("flightId", flightId)
                .setParameter("date", bookingDate)
                .getSingleResult();
    }

    TravelAgentBooking findByHotelAndDate(Long hotelId, LocalDate bookingDate) {
        return entityManager.createNamedQuery(TravelAgentBooking.FIND_BY_HOTEL_AND_DATE, TravelAgentBooking.class)
                .setParameter("hotelId", hotelId)
                .setParameter("date", bookingDate)
                .getSingleResult();
    }

    @Transactional
    TravelAgentBooking create(TravelAgentBooking tab) {
        log.info("TravelAgentRepository.create() - Creating: " + tab.toString());
        return entityManager.merge(tab);
    }

    @Transactional
    void delete(TravelAgentBooking tab) {
        entityManager.remove(entityManager.contains(tab) ? tab : entityManager.merge(tab));
    }
}
