package app.controllers;

import app.config.HibernateConfig;
import app.daos.GuideDAO;
import app.dtos.GuideDTO;
import app.dtos.GuideTotalPriceDTO;
import app.entities.Guide;
import app.exceptions.ApiException;
import app.exceptions.ValidationException;
import app.services.GuideService;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Map;

public class GuideController implements IController<Guide, Integer> {
    private final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private final GuideDAO guideDAO = GuideDAO.getInstance(emf);
    private final GuideService guideService = new GuideService();

    @Override
    public void getById(Context ctx) {
        try {
            int id = ctx.pathParamAsClass("id", Integer.class).get();

            // Manual id validation
            if (!validatePrimaryKey(id)) {
                throw new ValidationException("Not a valid id");
            }

            GuideDTO guidDTO = guideService.getById(id);

            ctx.status(200).json(guidDTO); // 200 ok

        }catch (ValidationException e){
            ctx.status(400).json(Map.of("Error", e.getMessage())); // 400 bad request
        }catch (ApiException e){
            // API failed fx 404
            ctx.status(e.getCode()).json(Map.of("Error", e.getMessage()));
        }catch (Exception e){
            // not for seen error
            ctx.status(500).json(Map.of("Error", "An Internal server error"));
        }
    }

    @Override
    public void getAll(Context ctx) {
        try {
            List<GuideDTO> guideDTOS = guideService.getAll();

            ctx.status(200).json(guideDTOS);

        }catch (Exception e){
            ctx.status(500).json(Map.of("Error", "An Internal server error"));
        }
    }

    @Override
    public void create(Context ctx) {
        try {
            GuideDTO guideDTO = ctx.bodyAsClass(GuideDTO.class);

            // Make a guide using service
            GuideDTO dto = guideService.create(guideDTO);

            ctx.status(201).json(dto); // 201 created

        }catch (ValidationException e){
            ctx.status(400).json(Map.of("Error", e.getMessage()));
        }catch (Exception e){
            ctx.status(500).json(Map.of("Error", "An Internal server error"));
        }
    }

    @Override
    public void update(Context ctx) {
        try {
            int id = ctx.pathParamAsClass("id", Integer.class).get();

            // Read body as DTO
            GuideDTO guideDTO = ctx.bodyAsClass(GuideDTO.class);

            // Update by service
            GuideDTO updated = guideService.update(id, guideDTO);

            ctx.status(200).json(updated);

        }catch (ValidationException e){
            ctx.status(400).json(Map.of("Error", e.getMessage()));
        }catch (ApiException e){
            ctx.status(e.getCode()).json(Map.of("Error", e.getMessage()));
        }catch (Exception e){
            ctx.status(500).json(Map.of("Error", "An Internal server error"));
        }
    }

    @Override
    public void delete(Context ctx) {
        try {
            int id = ctx.pathParamAsClass("id", Integer.class).get();

            guideService.delete(id);

            // success
            ctx.status(204); // No content

        }catch (ValidationException e){
            ctx.status(400).json(Map.of("Error", e.getMessage()));
        }catch (ApiException e){
            ctx.status(e.getCode()).json(Map.of("Error", e.getMessage()));
        }catch (Exception e){
            ctx.status(500).json(Map.of("Error", "An Internal server error"));
        }
    }

    public void getTotalPricePerGuide(Context ctx){
        try {
            List<GuideTotalPriceDTO> totals = guideService.getTotalPricePerGuide();

            if(totals.isEmpty()){
                ctx.status(404).json(Map.of("Error", "No guides was found")); // The list is empty
                return;
            }

            ctx.status(200).json(totals);

        }catch (ApiException e){
            ctx.status(e.getCode()).json(Map.of("Error", e.getMessage()));
        }catch (Exception e){
            ctx.status(500).json(Map.of("Error", "An Internal Server error"));
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        return guideService.validatePrimaryKey(id);
    }
    
}
