package uk.ac.newcastle.enterprisemiddleware.taxi;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class TaxiValidator {

    @Inject
    Validator validator;

    @Inject
    TaxiRepository taxiRepository;

    void validateTaxi(Taxi taxi) {
        Set<ConstraintViolation<Taxi>> violations = validator.validate(taxi);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        if (registrationAlreadyExists(taxi.getRegistration(), taxi.getId())) {
            throw new UniqueRegistrationException("Unique registration violation");
        }
    }

    boolean registrationAlreadyExists(String registration, Long id) {
        Taxi taxi = null;
        Taxi taxiWithID = null;

        try {
            taxi = taxiRepository.findByRegistration(registration);
        } catch (NoResultException e) {
            // ignore
        }

        if (taxi != null && id != null) {
            try {
                taxiWithID = taxiRepository.findById(id);
                if (taxiWithID != null && taxiWithID.getRegistration().equals(registration)) {
                    taxi = null;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }

        return taxi != null;
    }
}
