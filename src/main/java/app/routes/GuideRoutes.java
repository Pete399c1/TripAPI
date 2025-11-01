package app.routes;


import app.controllers.GuideController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.put;

public class GuideRoutes {
    private GuideController guideController = new GuideController();


    public EndpointGroup getGuideRoutes(){
        return () -> {
            get("/", guideController::getAll);
            get("/{id}", guideController::getById);
            post("/", guideController::create);
            put("/{id}", guideController::update);
            delete("/{id}", guideController::delete);
        };
    }
}
