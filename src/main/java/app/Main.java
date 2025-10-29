package app;


import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.utils.Populator;
import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    public static void main(String[]args){
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        Populator populator = new Populator();
        populator.populateTripsAndGuides(emf);

        Javalin app = ApplicationConfig.startServer(7070);
    }
}
