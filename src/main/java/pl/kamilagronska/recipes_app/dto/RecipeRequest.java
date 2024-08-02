package pl.kamilagronska.recipes_app.dto;

import lombok.Builder;
import lombok.Data;
import pl.kamilagronska.recipes_app.entity.Status;


@Data
@Builder
public class RecipeRequest {
    String title;
    String description;
    Status status;
}
