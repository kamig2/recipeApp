package pl.kamilagronska.recipes_app.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class RecipeDto {
    Long recipeId;
    String title;
    String ingredients;
    String description;
    String username;
    float rating;
    LocalDate date;
    private List<String> imageUrls;
}
