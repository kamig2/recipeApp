package pl.kamilagronska.recipes_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.kamilagronska.recipes_app.dto.RecipeRequest;
import pl.kamilagronska.recipes_app.dto.RecipeResponse;
//import pl.kamilagronska.recipes_app.dto.RecipeDtoMapper;
import pl.kamilagronska.recipes_app.entity.Recipe;
import pl.kamilagronska.recipes_app.entity.User;
import pl.kamilagronska.recipes_app.repository.RecipeRepository;
import pl.kamilagronska.recipes_app.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

//    private final RecipeDtoMapper recipeDtoMapper;
    public List<RecipeResponse> getAllRecipes(){
        List<Recipe> recipes = recipeRepository.findAll();
        List<RecipeResponse> recipeResponses = new ArrayList<>();
        RecipeResponse recipeResponse;
        for(Recipe recipe : recipes){
            recipeResponse = RecipeResponse.builder()
                    .recipeId(recipe.getRecipeId())
                    .title(recipe.getTitle())
                    .description(recipe.getDescription())
                    .username(recipe.getUser().getUsername())
                    .status(recipe.getStatus())
                    .rating(recipe.getRating())
                    .date(recipe.getDate())
                    .build();
            recipeResponses.add(recipeResponse);
        }
        return recipeResponses;
    }
    public RecipeResponse getRecipe(Long id){
        Recipe recipe = recipeRepository.findRecipeByRecipeId(id).orElseThrow(()->new RuntimeException("recipe not found"));
        RecipeResponse recipeResponse = RecipeResponse.builder()
                .recipeId(recipe.getRecipeId())
                .title(recipe.getTitle())
                .description(recipe.getDescription())
                .username(recipe.getUser().getUsername())
                .status(recipe.getStatus())
                .rating(recipe.getRating())
                .date(recipe.getDate())
                .build();
        return recipeResponse;
        //todo utworzyć klase do obsługi wyjątków gdy przepis noieznaleziony
    }

    public RecipeResponse addRecipe(RecipeRequest request) {
        Recipe recipeSaved = convertReguestToRecipe(request);
        recipeRepository.save(recipeSaved);
        RecipeResponse response = convertRecipeToRecipeResponse(recipeSaved);
        return response;
    }

    public RecipeResponse updateRecipe(Long id,RecipeRequest request){
        Recipe recipeSaved = convertReguestToRecipe(request);
        recipeSaved.setRecipeId(id);
        recipeRepository.save(recipeSaved);
        RecipeResponse response = convertRecipeToRecipeResponse(recipeSaved);
        return response;
    }

    private User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsserame = authentication.getName();
        User user = userRepository.findUserByUserName(currentUsserame).orElseThrow(()->new UsernameNotFoundException("User not found"));
        return user;
    }

    private RecipeResponse convertRecipeToRecipeResponse(Recipe recipe){
        RecipeResponse response = RecipeResponse.builder()
                .recipeId(recipe.getRecipeId())
                .title(recipe.getTitle())
                .description(recipe.getDescription())
                .username(recipe.getUser().getUsername())
                .status(recipe.getStatus())
                .rating(recipe.getRating())
                .date(recipe.getDate())
                .build();
        return response;
    }

    private Recipe convertReguestToRecipe(RecipeRequest request){
        Recipe recipe = Recipe.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .user(getCurrentUser())
                .status(request.getStatus())
                .rating(request.getRating())
                .date(request.getDate())
                .build();
        return recipe;
    }
}
