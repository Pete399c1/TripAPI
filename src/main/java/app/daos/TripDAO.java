package app.daos;

import app.entities.Trip;
import app.exceptions.ApiException;
import jakarta.persistence.*;

import java.util.List;

public class TripDAO implements IDAO<Trip, Integer>{
    private static TripDAO instance;
    private final EntityManagerFactory emf;

    public TripDAO(EntityManagerFactory emf){
        this.emf = emf;
    }

    public static TripDAO getInstance(EntityManagerFactory emf){
        if(instance == null){
            instance = new TripDAO(emf);
        }
        return instance;
    }

    @Override
    public Trip create(Trip trip) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.persist(trip);
            em.getTransaction().commit();
            return trip;
        }
    }

    @Override
    public List<Trip> getAll() {
        try(EntityManager em = emf.createEntityManager()){
            TypedQuery<Trip> query = em.createQuery(
                    // Choosing all trips from the entity
                    // Distinct removing the doublet so only once
                    // The (Left join fetch) only trips till we use getGuides()
                    "SELECT DISTINCT t FROM Trip t LEFT JOIN FETCH t.guides", Trip.class);
            return query.getResultList();
        }
    }

    @Override
    public Trip getById(Integer id) {
        try(EntityManager em = emf.createEntityManager()){
            TypedQuery<Trip> query = em.createQuery(
                    // Forcing. Hibernate get all guids to a specific trip
                    "SELECT t FROM Trip t LEFT JOIN FETCH t.guides WHERE t.id =:id", Trip.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        }catch (NoResultException e){
            return null;
        }
    }

    @Override
    public Trip update(Trip trip) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            Trip updatedTrip = em.merge(trip);
            em.getTransaction().commit();
            return updatedTrip;
        }
    }

    @Override
    public boolean delete(Integer id) {
        try(EntityManager em = emf.createEntityManager()){
            Trip trip = em.find(Trip.class, id);
            if(trip != null){
                em.getTransaction().begin();
                em.remove(trip);
                em.getTransaction().commit();
                return true;
            } else {
                return false;
            }
        }catch (PersistenceException ex){
            return false;
        }
    }
}
