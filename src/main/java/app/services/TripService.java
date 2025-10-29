package app.services;

import app.daos.GuideDAO;
import app.daos.TripDAO;
import app.dtos.TripDTO;
import app.entities.Guide;
import app.entities.Trip;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.stream.Collectors;

public class TripService implements IService<TripDTO,Integer> {
    private final TripDAO tripDAO;
    private final GuideDAO guideDAO;

    public TripService(TripDAO tripDAO, GuideDAO guideDAO) {
        this.tripDAO = tripDAO;
        this.guideDAO = guideDAO;
    }

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
        if(trip != null){
            return new TripDTO(trip);
        }else{
            return null;
        }
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
}
