package pl.kamilagronska.recipes_app.dto;

import pl.kamilagronska.recipes_app.entity.Status;

import java.time.LocalDate;

public record RecipeDto(
        Long recipeId,
        String title,
        String description,
        String userName,
        Status status,
        float note,
        LocalDate date
){
}
