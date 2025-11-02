package app.controllers;

import app.config.HibernateConfig;
import app.daos.GuideDAO;
import app.daos.TripDAO;
import app.dtos.TripDTO;
import app.entities.Trip;
import app.exceptions.ApiException;
import app.exceptions.ValidationException;
import app.services.PackingService;
import app.services.TripService;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Map;

public class TripController implements IController<Trip, Integer> {
    private final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private final TripDAO tripDAO = TripDAO.getInstance(emf);
    private final GuideDAO guideDAO = GuideDAO.getInstance(emf);
    private final PackingService packingService = new PackingService();
    private final TripService tripService = new TripService();

    @Override
    public void getById(Context ctx) {
        try {
            int id = ctx.pathParamAsClass("id", Integer.class).get();

            if (!validatePrimaryKey(id)) {
                throw new ValidationException("Not a valid id");
            }

            TripDTO tripDTO = tripService.getById(id);

            ctx.status(200).json(tripDTO); // 200 ok

        }catch (ValidationException e){
            ctx.status(400).json(Map.of("Error", e.getMessage())); // 400 bad request
        }catch (ApiException e){
            ctx.status(e.getCode()).json(Map.of("Error", e.getMessage()));
        }catch (Exception e){
            // Converting the Map to. Json
            ctx.status(500).json(Map.of("Error", "An Internal server error"));
        }
    }

    @Override
    public void getAll(Context ctx) {
        try{
         List<TripDTO> tripDTOS = tripService.getAll();

         ctx.status(200).json(tripDTOS);

        }catch (Exception e){
            ctx.status(500).json(Map.of("Error", "An Internal server error"));
        }
    }

    @Override
    public void create(Context ctx) {
        try {
            TripDTO tripDTO = ctx.bodyAsClass(TripDTO.class);

            // Make a trip using service
            TripDTO dto = tripService.create(tripDTO);

            ctx.status(201).json(dto); // 201 created

        }catch (ValidationException e){
            //Map.of takes a key and a value cant be changed after it is made it is a static method
            ctx.status(400).json(Map.of("Error", e.getMessage()));
        }catch (Exception e){
            ctx.status(500).json(Map.of("Error", "An Internal server error"));
        }
    }

    @Override
    public void update(Context ctx) {
        try {
            int id = ctx.pathParamAsClass("id", Integer.class).get();

            // Read body as TripDTO
            TripDTO tripDTO = ctx.bodyAsClass(TripDTO.class);

            // Update by service
            TripDTO updated = tripService.update(id, tripDTO);

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

            tripService.delete(id);

            ctx.status(204); // success No content

        }catch (ValidationException e){
            ctx.status(400).json(Map.of("Error", e.getMessage()));
        }catch (ApiException e){
            ctx.status(e.getCode()).json(Map.of("Error", e.getMessage()));
        }catch (Exception e){
            ctx.status(500).json(Map.of("Error", "An Internal server error"));
        }
    }

    public void linkGuide(Context ctx) {
        try {
            int tripId = ctx.pathParamAsClass("tripId", Integer.class).get();

            int guideId = ctx.pathParamAsClass("guideId", Integer.class).get();

            TripDTO updatedTrip = tripService.addGuideToTrip(tripId, guideId);

            ctx.status(200).json(updatedTrip);

        }catch (ValidationException e){
            ctx.status(400).json(Map.of("Error", e.getMessage()));
        }catch (ApiException e){
            ctx.status(e.getCode()).json(Map.of("Error", e.getMessage()));
        }catch (Exception e){
            ctx.status(500).json(Map.of("Error", "An Internal server error"));
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        return tripService.validatePrimaryKey(id);
    }

    public void getByCategory(Context ctx){
        try {
            String category = ctx.queryParam("category");

            if (category == null || category.isEmpty()) {
                ctx.status(200).json(tripService.getAll());
                return;
            }

            List<TripDTO> trips = tripService.getTripsByCategory(category);

            ctx.status(200).json(trips);

        }catch (ApiException e){
            ctx.status(e.getCode()).json(Map.of("Error", e.getMessage()));
        }catch (Exception e){
            ctx.status(500).json(Map.of("Error", "An Internal server error"));
        }
    }

    // Get packing items for Trip
    public void getPackingItems(Context ctx){
        try {
            int tripId = ctx.pathParamAsClass("id", Integer.class).get();

            TripDTO tripDTO = tripService.getById(tripId);

            //Getting packing items by using PackingService based on trip Category
            var items = packingService.fetchPackingItems(tripDTO.getCategory().name());

            ctx.status(200).json(items);

        }catch (ValidationException e){
            ctx.status(400).json(Map.of("Error", e.getMessage()));
        }catch (ApiException e){
            ctx.status(e.getCode()).json(Map.of("Error", e.getMessage()));
        }catch (Exception e){
            ctx.status(500).json(Map.of("Error", "An Internal server error"));
        }
    }

    // Get Total packing weight for Trip
    public void getPackingWeightForTrip(Context ctx){
        try {
            int tripId = ctx.pathParamAsClass("id", Integer.class).get();

            TripDTO tripDTO = tripService.getById(tripId);

            // Calculate total weight by using PackingService
            int totalWeight = packingService.fetchTotalWeight(tripDTO.getCategory().name());

            ctx.status(200).json(totalWeight);

        }catch (ValidationException e){
            ctx.status(400).json(Map.of("Error", e.getMessage()));
        }catch (ApiException e){
            ctx.status(e.getCode()).json(Map.of("Error", e.getMessage()));
        }catch (Exception e){
            ctx.status(500).json(Map.of("Error", "An Internal server error"));
        }
    }


}
