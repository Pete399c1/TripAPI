package app.entities;

import app.dtos.TripDTO;
import app.enums.Category;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = "guides")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "guides")
@Builder
@Entity
@Table(name = "trip")
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id", nullable = false)
    private int id;

    @Column(name = "trip_name", nullable = false)
    private String name;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    //Breddegrad north/south
    @Column(name = "trip_latitude", nullable = false)
    private double latitude;

    //Længdegrad east/west
    @Column(name = "trip_longitude", nullable = false)
    private double longitude;

    @Column(name = "trip_price", nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "trip_category",nullable = false)
    private Category category;

    @ManyToMany(mappedBy = "trips", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Guide> guides = new HashSet<>();

    public Trip(TripDTO tripDTO){
        this.id = tripDTO.getId();
        this.name = tripDTO.getName();
        this.startTime = tripDTO.getStartTime();
        this.endTime = tripDTO.getEndTime();
        this.latitude = tripDTO.getLatitude();
        this.longitude = tripDTO.getLongitude();
        this.price = tripDTO.getPrice();
        this.category = tripDTO.getCategory();
    }

    // Helper methods
    public void addGuide(Guide guide){
        if(guide != null){
            guides.add(guide);
            guide.getTrips().add(this);;
        }
    }

    public void removeGuide(Guide guide){
        if(guide != null){
            guides.remove(guide);
            guide.getTrips().remove(this);
        }
    }

}
