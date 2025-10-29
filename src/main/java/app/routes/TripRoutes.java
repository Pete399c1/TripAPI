package app.routes;
import app.config.HibernateConfig;
import app.controllers.TripController;
import app.daos.TripDAO;
import app.services.TripService;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

public class TripRoutes {
    private TripController tripController = new TripController();

    public EndpointGroup getTripRoutes(){
        return () -> {
            get("/", tripController::getAll);
            get("/{id}", tripController::getById);
            post("/", tripController::create);
            put("/{id}", tripController::update);
            delete("/{id}", tripController::delete);
            put("/{tripId}/guides/{guideId}",tripController::linkGuide);
        };
    }

}
