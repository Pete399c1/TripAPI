package app.dtos;

import app.entities.Guide;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class GuideDTO {
    private int id;
    private String name;
    private String email;
    private String phoneNumber;
    private int yearsOfExperience;

    public GuideDTO(Guide guide){
        this.id = guide.getId();
        this.name = guide.getName();
        this.email = guide.getEmail();
        this.phoneNumber = guide.getPhoneNumber();
        this.yearsOfExperience = guide.getYearsOfExperience();
    }


}
