package uk.ac.newcastle.enterprisemiddleware.taxi;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.logging.Logger;

@Dependent
public class TaxiService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    TaxiValidator taxiValidator;

    @Inject
    TaxiRepository taxiRepository;


    /**
     * Retrieves a Taxi with the specified ID.
     * @param id the ID of the taxi to retrieve
     * @return a Taxi object with the matching ID, if found
     */
    public Taxi getTaxiById(Long id) {
        return taxiRepository.findById(id);
    }

    /**
     * Retrieves a Taxi with the specified registration.
     * @param registration the registration of the taxi to retrieve
     * @return a Taxi object with the matching registration, if found
     */
    Taxi getTaxiByRegistration(String registration) {
        return taxiRepository.findByRegistration(registration);
    }

    /**
     * Retrieves a list of all taxis.
     * @return a List of all taxis in the database
     */
    List<Taxi> getAllTaxis() {
        return taxiRepository.findAll();
    }

    /**
     * Creates a new Taxi and adds it to the database.
     * @param taxi the taxi to create
     */
    void createTaxi(Taxi taxi) {
        log.info("TaxiService.createTaxi() - Creating " + taxi.getRegistration());

        taxiValidator.validateTaxi(taxi);

        taxiRepository.create(taxi);
    }

    /**
     * Updates the Taxi's information in the database.
     * @param taxi the taxi with the updated information
     */
    void updateTaxi(Taxi taxi) {
        log.info("TaxiService.updateTaxi() - Updating " + taxi.getRegistration());

        taxiValidator.validateTaxi(taxi);
        taxiRepository.update(taxi);
    }

    /**
     * Deletes a Taxi from the database.
     * @param taxi the taxi to delete
     */
    void deleteTaxi(Taxi taxi) {
        log.info("TaxiService.deleteTaxi() - Deleting " + taxi.toString());

        if (taxi.getId() != null) {
            taxiRepository.delete(taxi);
        } else {
            log.info("deleteTaxi() - No taxi found, specify ID");
        }
    }
}
