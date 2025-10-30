package app.services;

import app.config.HibernateConfig;
import app.daos.GuideDAO;
import app.daos.TripDAO;
import app.dtos.TripDTO;
import app.dtos.packing.PackageItemDTO;
import app.entities.Guide;
import app.entities.Trip;
import app.enums.Category;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TripService implements IService<TripDTO,Integer> {
    private final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private final TripDAO tripDAO = TripDAO.getInstance(emf);
    private final GuideDAO guideDAO = GuideDAO.getInstance(emf);
    private final PackingService packingService = new PackingService();

    @Override
    public TripDTO create(TripDTO tripDTO) {
        Trip trip = new Trip(tripDTO);

        Trip created = tripDAO.create(trip);

        return new TripDTO(created);
    }

    @Override
    public List<TripDTO> getAll() {
        return  tripDAO.getAll().stream()
                .map(TripDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public TripDTO getById(Integer id) {
        Trip trip = tripDAO.getById(id);

        if(trip == null){
            throw new ApiException(404, "The Trip was not found");
        }

        TripDTO tripDTO = new TripDTO(trip);

        // Adding the Packing Items automatic from the extern api
        if(trip.getCategory() != null){
            tripDTO.setPackingItems(packingService.fetchPackingItems(trip.getCategory().toString()));
        }
        return tripDTO;
    }

    @Override
    public TripDTO update(Integer id, TripDTO tripDTO) {
        Trip existing  = tripDAO.getById(id);
        if(existing == null) return  null;

        existing.setName(tripDTO.getName());
        existing.setStartTime(tripDTO.getStartTime());
        existing.setEndTime(tripDTO.getEndTime());
        existing.setLatitude(tripDTO.getLatitude());
        existing.setLongitude(tripDTO.getLongitude());
        existing.setPrice(tripDTO.getPrice());
        existing.setCategory(tripDTO.getCategory());

        Trip updated = tripDAO.update(existing);
        return new TripDTO(updated);
    }

    @Override
    public boolean delete(Integer id) {
        return tripDAO.delete(id);
    }

    public TripDTO addGuideToTrip(int tripId, int guideId){
        Trip trip = tripDAO.getById(tripId);
        Guide guide = guideDAO.getById(guideId);

        if(trip == null || guide == null){
            return null;
        }

        trip.addGuide(guide);
        Trip updated = tripDAO.update(trip);

        return new TripDTO(updated);
    }

    public boolean validatePrimaryKey(Integer id){
            Trip trip = tripDAO.getById(id);
            return trip != null;
    }

    public List<TripDTO> getTripsByCategory(String category){
        return tripDAO.getAll().stream()
                .filter(trip -> trip.getCategory().name().equalsIgnoreCase(category))
                .map(TripDTO::new)
                .toList();
    }



    // Get total weight package stuff for Trip
    public int getTotalPackingWeightByTrip(int id){
        // Getting packing by using the method getById()
        TripDTO trip = getById(id);
        return trip.getPackingItems().stream()
                .mapToInt(PackageItemDTO::getTotalWeight)
                .sum();

    }
}
