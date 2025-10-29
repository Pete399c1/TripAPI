package app.entities;

import app.dtos.GuideDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = "trips")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "trips")
@Builder
@Entity
@Table(name = "guide")
public class Guide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guide_id", nullable = false)
    private int id;

    @Column(name = "guide_name", nullable = false)
    private String name;

    @Column(name = "guide_email", nullable = false)
    private String email;

    @Column(name = "guide_phone")
    private String phoneNumber;

    @Column(name = "years_of_experience", nullable = false)
    private int yearsOfExperience;

    @ManyToMany(fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Trip> trips = new HashSet<>();

    public Guide(GuideDTO guideDTO){
        this.id = guideDTO.getId();
        this.name = guideDTO.getName();
        this.email = guideDTO.getEmail();
        this.phoneNumber = guideDTO.getPhoneNumber();
        this.yearsOfExperience = guideDTO.getYearsOfExperience();
    }

    public void addTrip(Trip trip) {
        if (trip != null) {
            trips.add(trip);
            trip.getGuides().add(this);
        }
    }

    public void removeTrip(Trip trip) {
        if (trip != null) {
            trips.remove(trip);
            trip.getGuides().remove(this);
        }
    }
}
