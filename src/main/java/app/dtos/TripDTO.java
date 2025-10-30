package app.dtos;

import app.dtos.packing.PackageItemDTO;
import app.entities.Guide;
import app.entities.Trip;
import app.enums.Category;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TripDTO {
    private int id;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double latitude;
    private double longitude;
    private BigDecimal price;
    private Category category;
    private Set<GuideDTO> guides = new HashSet<>();

    // Get from external api
    private List<PackageItemDTO> packingItems;

    public TripDTO(Trip trip){
        this.id = trip.getId();
        this.name = trip.getName();
        this.startTime = trip.getStartTime();
        this.endTime = trip.getEndTime();
        this.latitude = trip.getLatitude();
        this.longitude = trip.getLongitude();
        this.price = trip.getPrice();
        this.category = trip.getCategory();

        this.guides = new HashSet<>();

        if(trip.getGuides() != null){
            for(Guide guide : trip.getGuides()){
                this.guides.add(new GuideDTO(guide));
            }
        }

        // The list is empty waiting till the service calls the api
        this.packingItems = null;
    }
}
