package app.services;

import app.config.HibernateConfig;
import app.daos.GuideDAO;
import app.dtos.GuideDTO;
import app.dtos.GuideTotalPriceDTO;
import app.entities.Guide;
import app.entities.Trip;
import app.exceptions.ApiException;
import app.exceptions.ValidationException;
import jakarta.persistence.EntityManagerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class GuideService implements IService<GuideDTO, Integer> {
    private final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private final GuideDAO guideDAO = GuideDAO.getInstance(emf);



    @Override
    public GuideDTO create(GuideDTO guideDTO) {
        validateDto(guideDTO);

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

       // Returns 404 not found in the controller
       if(guide == null){
           throw new ApiException(404, "Guide was not found");
       }

       return new GuideDTO(guide);
    }

    @Override
    public GuideDTO update(Integer id, GuideDTO guideDTO) {
        validateDto(guideDTO);

        Guide existing  = guideDAO.getById(id);

        if(existing == null){
            throw new ApiException(404, "Guide was not found");
        }

        existing.setName(guideDTO.getName());
        existing.setEmail(guideDTO.getEmail());
        existing.setPhoneNumber(guideDTO.getPhoneNumber());
        existing.setYearsOfExperience(guideDTO.getYearsOfExperience());

        Guide updated = guideDAO.update(existing);

        return new GuideDTO(updated);
    }

    @Override
    public boolean delete(Integer id) {
        boolean deleted = guideDAO.delete(id);

        // Runtime exception
        if(!deleted){
            throw new ApiException(404, "Guide was not found");
        }

        return true;
    }

    public List<GuideTotalPriceDTO> getTotalPricePerGuide(){
        List<Guide> guides = guideDAO.getAll();

        return guides.stream()
                .map(guide -> {
                    BigDecimal total = guide.getTrips().stream()
                            .map(Trip::getPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new GuideTotalPriceDTO(guide.getId(), total);

                })
                .toList();
    }

    public boolean validatePrimaryKey(Integer id) {
        return guideDAO.getById(id) != null;
    }

    @Override
    public void validateDto(GuideDTO guideDTO){
        if(guideDTO == null)
            throw new ValidationException("The guideDTO cannot be null");
        if(guideDTO.getName() == null || guideDTO.getName().isEmpty())
            throw new ValidationException("The guide name cannot be empty");
        if(guideDTO.getEmail() == null || guideDTO.getEmail().isEmpty())
            throw new ValidationException("The guide email cannot be null");
        if(guideDTO.getYearsOfExperience() < 0)
            throw new ValidationException("The years of experience cannot be negative");
    }

}
