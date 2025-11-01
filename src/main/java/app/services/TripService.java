package app.services;

import app.config.HibernateConfig;
import app.daos.GuideDAO;
import app.daos.TripDAO;
import app.dtos.TripDTO;
import app.entities.Guide;
import app.entities.Trip;
import app.exceptions.ApiException;
import app.exceptions.ValidationException;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.stream.Collectors;

public class TripService implements IService<TripDTO,Integer> {
    private final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private final TripDAO tripDAO = TripDAO.getInstance(emf);
    private final GuideDAO guideDAO = GuideDAO.getInstance(emf);
    private final PackingService packingService = new PackingService();

    @Override
    public TripDTO create(TripDTO tripDTO) {
        validateDto(tripDTO);

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

        // Runtime exception
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
        validateDto(tripDTO);

        Trip existing  = tripDAO.getById(id);

        if(existing == null){
            throw new ApiException(404, "Trip was not found");
        }

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
        boolean deleted = tripDAO.delete(id);

        if(!deleted){
            throw new ApiException(404, "Trip was not found");
        }

        return true;
    }

    public TripDTO addGuideToTrip(int tripId, int guideId){
        Trip trip = tripDAO.getById(tripId);
        Guide guide = guideDAO.getById(guideId);

        if(trip == null || guide == null){
            throw new ApiException(404, "The Trip or Guide was not found");
        }

        trip.addGuide(guide);
        Trip updated = tripDAO.update(trip);

        return new TripDTO(updated);
    }

    public boolean validatePrimaryKey(Integer id){
             return tripDAO.getById(id) != null;
    }

    public List<TripDTO> getTripsByCategory(String category){
        List<TripDTO> trips = tripDAO.getAll().stream()
                .filter(trip -> trip.getCategory().name().equalsIgnoreCase(category))
                .map(TripDTO::new)
                .toList();

        if(trips.isEmpty()){
            throw new ApiException(404, "There was no trips found for category: " + category);
        }

        return trips;
    }

    @Override
    public void validateDto(TripDTO tripDTO){
        if(tripDTO == null)
            throw new ValidationException("The tripDTO cannot be null");
        if(tripDTO.getName() == null || tripDTO.getName().isEmpty())
            throw new ValidationException("The trip name cannot be empty");
        if(tripDTO.getCategory() == null)
            throw new ValidationException("The trip category cannot be null");
        if(tripDTO.getPrice() != null && tripDTO.getPrice().doubleValue() < 0)
            throw new ValidationException("The price cannot be negative");
    }

}
