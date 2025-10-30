package app.controllers;

import app.config.HibernateConfig;
import app.daos.GuideDAO;
import app.daos.TripDAO;
import app.dtos.TripDTO;
import app.entities.Trip;
import app.exceptions.ApiException;
import app.services.PackingService;
import app.services.TripService;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class TripController implements IController<Trip, Integer> {
    private final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private final TripDAO tripDAO = TripDAO.getInstance(emf);
    private final GuideDAO guideDAO = GuideDAO.getInstance(emf);
    private final PackingService packingService = new PackingService();
    private final TripService tripService = new TripService();

    @Override
    public void getById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        TripDTO tripDTO = tripService.getById(id);

        if(tripDTO == null){
            throw new ApiException(404, "The Trip was not found"); // 404 not found
        }
        ctx.status(200).json(tripDTO); // 200 ok
    }

    @Override
    public void getAll(Context ctx) {
        List<TripDTO> tripDTOS = tripService.getAll();
        ctx.status(200).json(tripDTOS);
    }

    @Override
    public void create(Context ctx) {
        TripDTO tripDTO = ctx.bodyAsClass(TripDTO.class);

        // Make a trip using service
        TripDTO dto = tripService.create(tripDTO);

        ctx.status(201).json(dto); // 201 created
    }

    @Override
    public void update(Context ctx) {
        int id = ctx.pathParamAsClass("id",Integer.class)
                .check(this::validatePrimaryKey,"Not a valid id")
                .get();

        // Read body as TripDTO
        TripDTO tripDTO = ctx.bodyAsClass(TripDTO.class);

        // Update by service
        TripDTO updated = tripService.update(id, tripDTO);

        if(updated == null){
            throw new ApiException(404,"Trip was not found");
        }
        ctx.status(200).json(updated);
    }

    @Override
    public void delete(Context ctx) {
        int id = ctx.pathParamAsClass("id",Integer.class)
                .check(this::validatePrimaryKey,"Not a valid id")
                .get();

        boolean deleted = tripService.delete(id);

        if(!deleted) {
            throw new ApiException(404, "Trip was not found");
        }
        // success
        ctx.status(204); // No content
    }

    public void linkGuide(Context ctx) {
        int tripId = ctx.pathParamAsClass("tripId",Integer.class)
                .check(this::validatePrimaryKey,"Not a valid trip id")
                .get();

        int guideId = ctx.pathParamAsClass("guideId",Integer.class)
                .get();

        TripDTO updatedTrip = tripService.addGuideToTrip(tripId, guideId);

        if(updatedTrip == null){
            throw new ApiException(404,"Trip or guide not found");
        }
        ctx.status(200).json(updatedTrip);
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        return tripService.validatePrimaryKey(id);
    }

    @Override
    public Trip validateEntity(Context ctx) {
        return null;
    }

    public void getByCategory(Context ctx){
        String category = ctx.queryParam("category");

        if(category == null || category.isEmpty()){
            ctx.status(200).json(tripService.getAll());
            return;
        }
        List<TripDTO> trips = tripService.getTripsByCategory(category);

        ctx.status(200).json(trips);
    }

    // Get packing items for Trip
    public void getPackingItems(Context ctx){
        int tripId = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid trip id")
                .get();

        TripDTO tripDTO = tripService.getById(tripId);

        if(tripDTO == null){
            throw new ApiException(404,"Trip was not found");
        }

        //Getting packing items by using PackingService based on trip Category
        var items = packingService.fetchPackingItems(tripDTO.getCategory().name());
        ctx.status(200).json(items);
    }

    // Get Total packing weight for Trip
    public void getPackingWeightForTrip(Context ctx){
        int tripId = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid trip id")
                .get();

        TripDTO tripDTO = tripService.getById(tripId);

        if(tripDTO == null){
            throw  new ApiException(404,"Trip was not found");
        }

        // Calculate total weight by using PackingService
        int totalWeight = packingService.fetchTotalWeight(tripDTO.getCategory().name());
        ctx.status(200).json(totalWeight);
    }


}
