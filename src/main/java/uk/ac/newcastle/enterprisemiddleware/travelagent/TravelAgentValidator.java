package uk.ac.newcastle.enterprisemiddleware.travelagent;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class TravelAgentValidator {

    @Inject
    Validator validator;

    @Inject
    TravelAgentRepository travelAgentRepository;

    void validateTravelAgentBooking(TravelAgentBooking tab) {
        Set<ConstraintViolation<TravelAgentBooking>> violations = validator.validate(tab);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        Long taxiId = tab.getTaxi().getId();
        Long flightId = tab.getFlightId();
        Long hotelId = tab.getHotelId();
        LocalDate taxiDate = tab.getTaxiBookingDate();
        LocalDate flightDate = tab.getFlightBookingDate();
        LocalDate hotelDate = tab.getHotelBookingDate();

        if (tabExists(taxiId, taxiDate, flightId, flightDate, hotelId, hotelDate)) {
            throw new UniqueTravelAgentBookingException("Unique TravelAgentBooking violation");
        }
    }

    boolean tabExists(Long taxiId, LocalDate taxiDate, Long flightId, LocalDate flightDate, Long hotelId, LocalDate hotelDate) {
        TravelAgentBooking tab = null;

        try {
            tab = travelAgentRepository.findByTaxiAndDate(taxiId, taxiDate);
            tab = travelAgentRepository.findByFlightAndDate(flightId, flightDate);
            tab = travelAgentRepository.findByHotelAndDate(hotelId, hotelDate);
        } catch (NoResultException e) {
            return false;
        }

        return tab != null;
    }
}
