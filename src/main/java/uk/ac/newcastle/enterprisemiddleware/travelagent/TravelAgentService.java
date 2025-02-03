package uk.ac.newcastle.enterprisemiddleware.travelagent;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.logging.Logger;

@Dependent
public class TravelAgentService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    TravelAgentRepository travelAgentRepository;

    @Inject
    TravelAgentValidator travelAgentValidator;

    public TravelAgentBooking getTravelAgentBookingById(Long id) {
        return travelAgentRepository.findById(id);
    }

    List<TravelAgentBooking> getTravelAgentBookingsByCustomerId(Long customerId) {
        return travelAgentRepository.findAllByCustomer(customerId);
    }

    List<TravelAgentBooking> getAllTravelAgentBookings() {
        return travelAgentRepository.findAll();
    }

    TravelAgentBooking createTravelAgentBooking(TravelAgentBooking travelAgentBooking) {
        travelAgentValidator.validateTravelAgentBooking(travelAgentBooking);

        try {
            return travelAgentRepository.create(travelAgentBooking);
        } catch (Exception e) {
            log.severe("failed: " + e.getMessage());
            throw new RuntimeException("Failed to create Travel Agent Booking", e);
        }
    }

    void deleteTravelAgentBooking(TravelAgentBooking travelAgentBooking) {
        if (travelAgentBooking.getId() != null) {
            travelAgentRepository.delete(travelAgentBooking);
        } else {
            log.info("TravelAgentService.deleteBooking() - No id found");
        }
    }
}
