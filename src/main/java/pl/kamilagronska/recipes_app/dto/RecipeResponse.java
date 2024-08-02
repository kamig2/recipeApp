package pl.kamilagronska.recipes_app.dto;

import lombok.Builder;
import lombok.Data;
import pl.kamilagronska.recipes_app.entity.Status;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class RecipeResponse {
    Long recipeId;
    String title;
    String description;
    String username;
    Status status;
    float rating;
    LocalDate date;
    private List<String> imageUrls;
}
