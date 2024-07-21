package pl.kamilagronska.recipes_app.dto;

import lombok.Builder;
import lombok.Data;
import pl.kamilagronska.recipes_app.entity.Status;

import java.time.LocalDate;

@Data
@Builder
public class RecipeRequest {
    String title;
    String description;
    Status status;
    //float rating;//zmienic żeby dodawać automatycznie //dodać encje z ocenami w relacji one to many
    LocalDate date;
}
