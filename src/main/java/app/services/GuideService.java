package app.services;

import app.daos.GuideDAO;
import app.dtos.GuideDTO;
import app.dtos.TripDTO;
import app.entities.Guide;
import app.entities.Trip;

import java.util.List;
import java.util.stream.Collectors;

public class GuideService implements IService<GuideDTO, Integer> {
    private final GuideDAO guideDAO;

    public GuideService(GuideDAO guideDAO){
        this.guideDAO = guideDAO;
    }

    @Override
    public GuideDTO create(GuideDTO guideDTO) {
        Guide guide = new Guide(guideDTO);

        Guide created = guideDAO.create(guide);

        return new GuideDTO(created);
    }

    @Override
    public List<GuideDTO> getAll() {
        return guideDAO.getAll().stream()
                .map(GuideDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public GuideDTO getById(Integer id) {
       Guide guide = guideDAO.getById(id);
       if(guide != null){
           return new GuideDTO(guide);
       }else{
           return  null;
       }
    }

    @Override
    public GuideDTO update(Integer id, GuideDTO guideDTO) {
        Guide existing  = guideDAO.getById(id);
        if(existing == null) return  null;

        existing.setName(guideDTO.getName());
        existing.setEmail(guideDTO.getEmail());
        existing.setPhoneNumber(guideDTO.getPhoneNumber());
        existing.setYearsOfExperience(guideDTO.getYearsOfExperience());


        Guide updated = guideDAO.update(existing);
        return new GuideDTO(updated);
    }

    @Override
    public boolean delete(Integer id) {
        return guideDAO.delete(id);
    }

    public boolean validatePrimaryKey(Integer id) {
        Guide guide = guideDAO.getById(id);
        return guide != null;
    }
}
