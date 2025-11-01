package app.services;

import app.dtos.packing.PackageItemDTO;
import app.dtos.packing.PackingApiResponse;
import app.exceptions.ApiException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@NoArgsConstructor
public class PackingService {
    private  static final String API_URL = System.getenv("PACKING_API_URL");
    // objectMapper set with JavaTimeModule so ZonedDateTime attributes can be pase from the api
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    // Getting the packageList for specific category from the api
    public List<PackageItemDTO> fetchPackingItems(String category){
        if(API_URL == null || API_URL.isEmpty()){
            throw new ApiException(500, "The env variable is not set");
        }

        try{
            // Building the url toLowerCase to match api
            String url = API_URL + category.toLowerCase();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            // Send request and receive as string formatted
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();

            if(status != 200){
                throw new ApiException(status, "The packing api returned HTTP");
            }

            // Parse json response into DTO
            PackingApiResponse packingApiResponse = objectMapper.readValue(response.body(), PackingApiResponse.class);

            return packingApiResponse.getItems();

        }catch(IOException e){
            throw new ApiException(500, "Failed trying parse the response from packing API: " + e.getMessage());
        }catch(InterruptedException e){
            throw new ApiException(500, "The request to the packing API was interrupted");
        }catch (Exception e){
            throw new ApiException(500, "Failed while trying to fetch packing items: " + e.getMessage());
        }
    }

    // calculate total weight in grams for a category
    public int fetchTotalWeight(String category){
        return fetchPackingItems(category).stream()
                .mapToInt(PackageItemDTO::getTotalWeight)
                .sum();
    }



}
