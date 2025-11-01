package app.routes;
import app.controllers.TripController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

public class TripRoutes {
    private TripController tripController = new TripController();

    // Remember this http://localhost:7070/api/v1/trips?category=city
    public EndpointGroup getTripRoutes(){
        return () -> {
            get("/", tripController::getByCategory);
            get("/{id}", tripController::getById);
            get("/{id}/packing", tripController::getPackingItems);
            get("/{id}/packing/weight", tripController::getPackingWeightForTrip);
            post("/", tripController::create);
            put("/{id}", tripController::update);
            put("/{tripId}/guides/{guideId}", tripController::linkGuide);
            delete("/{id}", tripController::delete);

        };
    }

}
