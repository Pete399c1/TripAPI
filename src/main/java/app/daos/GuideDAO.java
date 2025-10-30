package app.daos;

import app.entities.Guide;
import app.entities.Trip;
import jakarta.persistence.*;

import java.util.List;

public class GuideDAO implements IDAO<Guide, Integer> {
    private static GuideDAO instance;
    private final EntityManagerFactory emf;

    public GuideDAO(EntityManagerFactory emf){
        this.emf = emf;
    }

    public static GuideDAO getInstance(EntityManagerFactory emf){
        if(instance == null){
            instance = new GuideDAO(emf);
        }
        return instance;
    }

    @Override
    public Guide create(Guide guide) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.persist(guide);
            em.getTransaction().commit();
            return guide;
        }
    }

    @Override
    public List<Guide> getAll() {
        try(EntityManager em = emf.createEntityManager()){
            TypedQuery<Guide> query = em.createQuery(
                    "SELECT DISTINCT g FROM Guide g LEFT JOIN FETCH g.trips", Guide.class);
            return query.getResultList();
        }
    }

    @Override
    public Guide getById(Integer id) {
        try(EntityManager em = emf.createEntityManager()){
            TypedQuery<Guide> query = em.createQuery(
                    "SELECT g FROM Guide g LEFT JOIN FETCH g.trips WHERE g.id =:id", Guide.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

    @Override
    public Guide update(Guide guide) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            Guide updatedGuide = em.merge(guide);
            em.getTransaction().commit();
            return updatedGuide;
        }
    }

    @Override
    public boolean delete(Integer id) {
        try(EntityManager em = emf.createEntityManager()){
            Guide guide = em.find(Guide.class, id);
            if(guide != null){
                em.getTransaction().begin();
                em.remove(guide);
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
