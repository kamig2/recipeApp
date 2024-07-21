package pl.kamilagronska.recipes_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kamilagronska.recipes_app.dto.RatingRequest;
import pl.kamilagronska.recipes_app.dto.RatingResponse;
import pl.kamilagronska.recipes_app.dto.RecipeRequest;
import pl.kamilagronska.recipes_app.dto.RecipeResponse;
//import pl.kamilagronska.recipes_app.dto.RecipeDtoMapper;
import pl.kamilagronska.recipes_app.entity.Rating;
import pl.kamilagronska.recipes_app.entity.Recipe;
import pl.kamilagronska.recipes_app.entity.User;
import pl.kamilagronska.recipes_app.repository.RatingRepository;
import pl.kamilagronska.recipes_app.repository.RecipeRepository;
import pl.kamilagronska.recipes_app.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    private final RatingRepository ratingRepository;

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

    public RecipeResponse addRecipe(RecipeRequest request) { //przy dodawaniu ocena będzie 0
        Recipe recipeSaved = convertReguestToRecipe(request);
//        recipeSaved.setRating(0);
        recipeRepository.save(recipeSaved);
        RecipeResponse response = convertRecipeToRecipeResponse(recipeSaved);
        return response;
    }

    public RecipeResponse updateRecipe(Long id,RecipeRequest request){//przy update ocena float to średnia z ocen int z encji rating
        Recipe recipeSaved = convertReguestToRecipe(request);
        recipeSaved.setRecipeId(id);
        recipeSaved.setRating(calculateRating(id));
        recipeRepository.save(recipeSaved);
        RecipeResponse response = convertRecipeToRecipeResponse(recipeSaved);
        return response;
    }

    @Transactional
    public ResponseEntity deleteRecipe(Long recipeId) {
        recipeRepository.deleteByRecipeId(recipeId);
        return  ResponseEntity.ok("Recipe deleted");
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
                .date(request.getDate())
                .build();
        return recipe;
    }







    private float calculateRating(Long recipeId){
        Recipe recipe = recipeRepository.findRecipeByRecipeId(recipeId).orElseThrow(()-> new RuntimeException("recipe not found") );
        List<Rating> ratings = recipe.getRatingList();
        int sum = 0;
        for(Rating rating : ratings){
            sum += rating.getRate();
        }
        float ratingAvg = (float) sum / ratings.size();
        return ratingAvg;
    }
    private float calculateRating(Recipe recipe){
        List<Rating> ratings = recipe.getRatingList();
        int sum = 0;
        for(Rating rating : ratings){
            sum += rating.getRate();
        }
        float ratingAvg = (float) sum / ratings.size();
        return ratingAvg;
    }

    //wszystkie opinie do wybranego przepisu
    public List<RatingResponse> getOpinions(Long recipeId) {
        Recipe recipe = recipeRepository.findRecipeByRecipeId(recipeId).orElseThrow(()->new RuntimeException("recipe not found"));
        List<Rating> ratingList = recipe.getRatingList();
        RatingResponse response;
        List<RatingResponse> responseList = new ArrayList<>();
        for(Rating rating : ratingList){
            response = convertRatingToRatingResponse(rating);
            responseList.add(response);
        }
        return responseList;
    }

    //dodadnie nowej opini

    public RatingResponse addOpinion(Long recipeId, RatingRequest request) {
        Rating rating = convertRatingRequestToRating(request,recipeId);
        ratingRepository.save(rating);

        Recipe recipe = recipeRepository.findRecipeByRecipeId(recipeId).orElseThrow(()->new RuntimeException("recipe not found"));
        recipe.getRatingList().add(rating);//dodanie oceny do listy ocen w przepisie
        recipe.setRating(calculateRating(recipe));//update całkowitej oceny
        recipeRepository.save(recipe);

        return convertRatingToRatingResponse(rating);
    }

    //zmiana wybranej opini
    public RatingResponse updateOpinion(Long recipeId, Long ratingId, RatingRequest request) {
        Rating rating = ratingRepository.findRatingById(ratingId).orElseThrow(()->new RuntimeException("Rating not found"));
        rating.setRate(request.getRate());
        rating.setOpinion(request.getOpininion());
        ratingRepository.save(rating);

        Recipe recipe = recipeRepository.findRecipeByRecipeId(recipeId).orElseThrow(()->new RuntimeException("Recipe not found"));
        recipe.setRating(calculateRating(recipe));
        recipeRepository.save(recipe);

        return convertRatingToRatingResponse(rating);
    }

    @Transactional
    public ResponseEntity deleteOpinion(Long ratingId){
        Rating rating = ratingRepository.findRatingById(ratingId).orElseThrow(()->new RuntimeException("Rating not found"));
        Recipe recipe = rating.getRecipe();
        recipe.getRatingList().remove(rating);
        recipe.setRating(calculateRating(recipe));
        ratingRepository.deleteById(ratingId);
        return ResponseEntity.ok("Opinion deleted");
    }

    private Rating convertRatingRequestToRating(RatingRequest request,Long recipeId) {
        return Rating.builder()
                .rate(request.getRate())
                .opinion(request.getOpininion())
                .recipe(recipeRepository.findRecipeByRecipeId(recipeId).orElseThrow(()->new RuntimeException("Recipe not found")))
                .build();
    }

    private RatingResponse convertRatingToRatingResponse(Rating rating) {
        return RatingResponse.builder()
                .id(rating.getId())
                .rate(rating.getRate())
                .opinion(rating.getOpinion())
                .build();
    }


}
