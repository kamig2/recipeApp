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


    //wszystkie dostępne przepisy //todo zmienic na wszystkie publiczne
    public List<RecipeResponse> getAllRecipes(){
        List<Recipe> recipes = recipeRepository.findAll();
        List<RecipeResponse> recipeResponses = new ArrayList<>();
        for(Recipe recipe : recipes){
            recipeResponses.add(convertRecipeToRecipeResponse(recipe));
        }
        return recipeResponses;
    }

    // wyswietla prxepis o danym id //todo dodać sprawdzanie czy przepis jest publiczny
    public RecipeResponse getRecipe(Long id){
        Recipe recipe = recipeRepository.findRecipeByRecipeId(id).orElseThrow(()->new RuntimeException("recipe not found"));
        return convertRecipeToRecipeResponse(recipe);
        //todo utworzyć klase do obsługi wyjątków gdy przepis noieznaleziony
    }

    //todo wszystkie publiczne przepisy wybranego usera

    //todo wszystkie przepisy zalogowanego usera

    //dodawanie przepisu
    public RecipeResponse addRecipe(RecipeRequest request) { //przy dodawaniu ocena będzie 0
        Recipe recipeSaved = convertRequestToRecipe(request);
        recipeRepository.save(recipeSaved);

        User user = recipeSaved.getUser();
        user.getRecipes().add(recipeSaved); //dodanie nowego przepisu do listy przepisów usera
        userRepository.save(user);

        return convertRecipeToRecipeResponse(recipeSaved);
    }

    //update przepisu //todo każy user może updatować tylko dodane przez niego przepisy
    public RecipeResponse updateRecipe(Long id,RecipeRequest request){//przy update ocena float to średnia z ocen int z encji rating
        Recipe recipeSaved = convertRequestToRecipe(request);
        recipeSaved.setRecipeId(id);
        recipeSaved.setRating(calculateRating(recipeSaved));
        recipeRepository.save(recipeSaved);
        return convertRecipeToRecipeResponse(recipeSaved);
    }


    //usuwanie przepisu o wybranym id
    //todo tylko właściciel przepisu i admin może go usunąć
    @Transactional
    public ResponseEntity deleteRecipe(Long recipeId) {
//        Recipe recipe = recipeRepository.findRecipeByRecipeId(recipeId).orElseThrow(()->new RuntimeException("Recipe not found"));

        /*recipe.getUser().getRecipes().remove(recipe);*/ //usunięcie przepisu z listy przepisów usera
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
        return RecipeResponse.builder()
                .recipeId(recipe.getRecipeId())
                .title(recipe.getTitle())
                .description(recipe.getDescription())
                .username(recipe.getUser().getUsername())
                .status(recipe.getStatus())
                .rating(recipe.getRating())
                .date(recipe.getDate())
                .build();
    }


    private Recipe convertRequestToRecipe(RecipeRequest request){
        return Recipe.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .user(getCurrentUser())
                .status(request.getStatus())
                .date(request.getDate())
                .build();
    }

    private float calculateRating(Recipe recipe){
        List<Rating> ratings = recipe.getRatingList();
        int sum = 0;
        if (ratings != null){
            for(Rating rating : ratings){
                sum += rating.getRate();
            }
            float ratingAvg = (float) sum / ratings.size();
            return ratingAvg;
        }
        return 0;
    }

    //wszystkie opinie do wybranego przepisu
    public List<RatingResponse> getOpinions(Long recipeId) {
        Recipe recipe = recipeRepository.findRecipeByRecipeId(recipeId).orElseThrow(()->new RuntimeException("recipe not found"));
        List<Rating> ratingList = recipe.getRatingList();
        List<RatingResponse> responseList = new ArrayList<>();
        for(Rating rating : ratingList){
            responseList.add(convertRatingToRatingResponse(rating));
        }
        return responseList;
    }

    //wszystkie opinie wybranego urzytkownika
    public List<RatingResponse> getUserAllRatings(Long userId) {
        User user = userRepository.findUserByUserId(userId).orElseThrow(()->new RuntimeException("User not found"));
        List<Rating> ratingList = user.getRatingList();
        List<RatingResponse> responseList = new ArrayList<>();
        for (Rating rating : ratingList){
            responseList.add(convertRatingToRatingResponse(rating));
        }
        return responseList;
    }

    //wszystkie opinie zalogowanego urzytkownika
    public List<RatingResponse> getLoggedUserAllRatings() {
        User user = getCurrentUser();
        List<Rating> ratingList = user.getRatingList();
        List<RatingResponse> responseList = new ArrayList<>();
        for (Rating rating : ratingList){
            responseList.add(convertRatingToRatingResponse(rating));
        }
        return responseList;
    }



    //dodadnie nowej opini przez zalogowanego urzytkownika

    public RatingResponse addOpinion(Long recipeId, RatingRequest request) {
        Rating rating = convertRatingRequestToRating(request,recipeId);
        ratingRepository.save(rating);

        Recipe recipe = rating.getRecipe();
        recipe.getRatingList().add(rating);//dodanie oceny do listy ocen w przepisie
        recipe.setRating(calculateRating(recipe));//update całkowitej oceny
        recipeRepository.save(recipe);

        User user =  rating.getUser();
        user.getRatingList().add(rating); //dodanie oceny do listy ocen usera
        userRepository.save(user);

        return convertRatingToRatingResponse(rating);
    }

    //zmiana wybranej opini  //todo tylko właściciel opini może ją zmnienić
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

    //todo tylko właściciel opini oraz admin może ją usunąc
    @Transactional
    public ResponseEntity deleteOpinion(Long ratingId){
        Rating rating = ratingRepository.findRatingById(ratingId).orElseThrow(()->new RuntimeException("Rating not found"));

        Recipe recipe = rating.getRecipe();
        recipe.getRatingList().remove(rating); //usuwanie oceny z listy ocen w encji recipe
        recipe.setRating(calculateRating(recipe));

        rating.getUser().getRatingList().remove(rating);  //usuwanie oceny z listy ocen w encji user
        ratingRepository.deleteById(ratingId);
        return ResponseEntity.ok("Opinion deleted");
    }

    private Rating convertRatingRequestToRating(RatingRequest request,Long recipeId) {
        return Rating.builder()
                .rate(request.getRate())
                .opinion(request.getOpininion())
                .recipe(recipeRepository.findRecipeByRecipeId(recipeId).orElseThrow(()->new RuntimeException("Recipe not found")))
                .user(getCurrentUser())
                .build();
    }

    private RatingResponse convertRatingToRatingResponse(Rating rating) {
        return RatingResponse.builder()
                .id(rating.getId())
                .rate(rating.getRate())
                .opinion(rating.getOpinion())
                .username(rating.getUser().getUsername())
                .build();
    }


}
