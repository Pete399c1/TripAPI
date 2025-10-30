package app.controllers;

import app.config.HibernateConfig;
import app.daos.GuideDAO;
import app.dtos.GuideDTO;
import app.dtos.TripDTO;
import app.entities.Guide;
import app.entities.Trip;
import app.exceptions.ApiException;
import app.services.GuideService;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class GuideController implements IController<Guide, Integer> {
    private final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private final GuideDAO guideDAO = GuideDAO.getInstance(emf);
    private final GuideService guideService = new GuideService();

    @Override
    public void getById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        GuideDTO guidDTO = guideService.getById(id);

        if(guidDTO == null){
            throw new ApiException(404, "The guide was not found"); // 404 not found
        }
        ctx.status(200).json(guidDTO); // 200 ok
    }

    @Override
    public void getAll(Context ctx) {
        List<GuideDTO> guideDTOS = guideService.getAll();
        ctx.status(200).json(guideDTOS);
    }

    @Override
    public void create(Context ctx) {
        GuideDTO guideDTO = ctx.bodyAsClass(GuideDTO.class);

        // Make a guide using service
        GuideDTO dto = guideService.create(guideDTO);

        ctx.status(201).json(dto); // 201 created
    }

    @Override
    public void update(Context ctx) {
        int id = ctx.pathParamAsClass("id",Integer.class)
                .check(this::validatePrimaryKey,"Not a valid id")
                .get();

        // Read body as guideDTO
        GuideDTO guideDTO = ctx.bodyAsClass(GuideDTO.class);

        // Update by service
        GuideDTO updated = guideService.update(id, guideDTO);

        if(updated == null){
            throw new ApiException(404,"Guide was not found");
        }
        ctx.status(200).json(updated);
    }

    @Override
    public void delete(Context ctx) {
        int id = ctx.pathParamAsClass("id",Integer.class)
                .check(this::validatePrimaryKey,"Not a valid id")
                .get();

        boolean deleted = guideService.delete(id);

        if(!deleted) {
            throw new ApiException(404, "Guide was not found");
        }
        // success
        ctx.status(204); // No content
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        return guideService.validatePrimaryKey(id);
    }

    @Override
    public Guide validateEntity(Context ctx) {
        return null;
    }
}
