package app.services;

import java.util.List;

public interface IService<DTO,ID>{
    DTO create(DTO dto);
    List<DTO> getAll();
    DTO getById(ID id);
    DTO update(ID id, DTO dto);
    boolean delete(ID id);
    void validateDto(DTO dto);
}
