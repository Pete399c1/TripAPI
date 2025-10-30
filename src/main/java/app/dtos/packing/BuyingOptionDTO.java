package app.dtos.packing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuyingOptionDTO {
    private String shopName;
    private String shopUrl;
    private double price;
}
