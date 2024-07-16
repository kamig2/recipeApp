package pl.kamilagronska.recipes_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.kamilagronska.recipes_app.dto.RecipeDto;
import pl.kamilagronska.recipes_app.dto.RecipeDtoMapper;
import pl.kamilagronska.recipes_app.entity.Recipe;
import pl.kamilagronska.recipes_app.repository.RecipeRepository;
import pl.kamilagronska.recipes_app.repository.UserRepository;
import pl.kamilagronska.recipes_app.entity.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;

    private final RecipeDtoMapper recipeDtoMapper;
    public List<Recipe> getAllRecipes(){
        return recipeRepository.findAll();
    }
    public Recipe getRecipe(Long id){
        return recipeRepository.findRecipeByRecipeId(id).orElseThrow(() -> new RuntimeException("Recipe not found"));
        //todo utworzyć klase do obsługi wyjątków gdy przepis noieznaleziony
    }

    public ResponseEntity addRecipe(RecipeDto recipe) {
        Recipe recipeSaved = recipeDtoMapper.apply(recipe);
        recipeRepository.save(recipeSaved);
        return ResponseEntity.ok(recipe);
    }
}
