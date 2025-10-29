package app.utils;

import app.config.HibernateConfig;
import app.entities.Guide;
import app.entities.Trip;
import app.enums.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Populator {
    public void populateTripsAndGuides(EntityManagerFactory emf) {
        try (EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Trip").executeUpdate();
            em.createQuery("DELETE FROM Guide").executeUpdate();

            //Make Guides
            Guide guide1 = Guide.builder()
                    .name("Peter")
                    .email("pet@gmail.com")
                    .phoneNumber("20809010")
                    .yearsOfExperience(5)
                    .build();

            Guide guide2 = Guide.builder()
                    .name("cilla")
                    .email("cil@gmail.com")
                    .phoneNumber("40686174")
                    .yearsOfExperience(1)
                    .build();

            Guide guide3 = Guide.builder()
                    .name("Nikolaj")
                    .email("nik@gmail.com")
                    .phoneNumber("11578988")
                    .yearsOfExperience(3)
                    .build();

            em.persist(guide1);
            em.persist(guide2);
            em.persist(guide3);

            //Make Trips
            Trip trip1 = Trip.builder()
                    .name("Snow mountain adventure")
                    .startTime(LocalDateTime.now().plusDays(2))
                    .endTime(LocalDateTime.now().plusDays(4))
                    .latitude(67.7121)
                    .longitude(10.2128)
                    .price(new BigDecimal("588.77"))
                    .category(Category.SNOW)
                    .build();

            Trip trip2 = Trip.builder()
                    .name("Sunny Beach adventure")
                    .startTime(LocalDateTime.now().plusDays(1))
                    .endTime(LocalDateTime.now().plusDays(3))
                    .latitude(30.6143)
                    .longitude(13.1010)
                    .price(new BigDecimal("399.99"))
                    .category(Category.BEACH)
                    .build();

            Trip trip3 = Trip.builder()
                    .name("City Shopping adventure")
                    .startTime(LocalDateTime.now())
                    .endTime(LocalDateTime.now().plusDays(1))
                    .latitude(20.6554)
                    .longitude(12.1290)
                    .price(new BigDecimal("200.00"))
                    .category(Category.CITY)
                    .build();

            trip1.addGuide(guide1);
            trip2.addGuide(guide2);
            trip3.addGuide(guide3);

            em.persist(trip1);
            em.persist(trip2);
            em.persist(trip3);

            em.getTransaction().commit();

            System.out.println("Trips in DB:");
            em.createQuery("SELECT t FROM Trip t", Trip.class).getResultList().forEach(System.out::println);

            System.out.println("Guides in DB:");
            em.createQuery("SELECT g FROM Guide g", Guide.class).getResultList().forEach(System.out::println);
        }
    }

    public static void main(String[] args) {
        Populator populator = new Populator();
        populator.populateTripsAndGuides(HibernateConfig.getEntityManagerFactory());
    }
}
