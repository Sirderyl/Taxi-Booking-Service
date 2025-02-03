package uk.ac.newcastle.enterprisemiddleware.taxi;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.logging.Logger;

@RequestScoped
public class TaxiRepository {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    EntityManager entityManager;

    Taxi findById(Long id) {
        return entityManager.find(Taxi.class, id);
    }

    Taxi findByRegistration(String registration) {
        TypedQuery<Taxi> query = entityManager.createNamedQuery(Taxi.FIND_BY_REGISTRATION, Taxi.class)
                .setParameter("registration", registration);

        return query.getSingleResult();
    }

    List<Taxi> findAll() {
        return entityManager.createNamedQuery(Taxi.FIND_ALL, Taxi.class).getResultList();
    }

    @Transactional
    void create(Taxi taxi) {
        log.info("TaxiRepository.create() - Creating " + taxi.getRegistration());
        entityManager.persist(taxi);
    }

    @Transactional
    void update(Taxi taxi) {
        log.info("TaxiRepository.update() - Updating " + taxi.getRegistration());
        entityManager.merge(taxi);
    }

    @Transactional
    void delete(Taxi taxi) {
        entityManager.remove(entityManager.contains(taxi) ? taxi : entityManager.merge(taxi));
    }
}
