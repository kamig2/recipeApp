package pl.kamilagronska.recipes_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.kamilagronska.recipes_app.dto.RatingRequest;
import pl.kamilagronska.recipes_app.dto.RatingResponse;
import pl.kamilagronska.recipes_app.dto.RecipeRequest;
import pl.kamilagronska.recipes_app.dto.RecipeResponse;
import pl.kamilagronska.recipes_app.entity.*;
import pl.kamilagronska.recipes_app.exception.ResourceNotFoundException;
import pl.kamilagronska.recipes_app.repository.RatingRepository;
import pl.kamilagronska.recipes_app.repository.RecipeRepository;
import pl.kamilagronska.recipes_app.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;

    private final UserRepository userRepository;

    private final RatingRepository ratingRepository;

    public static String uploadDirectory = System.getProperty("user.dir")+"/upload";




    //wszystkie przepisy publiczne i prywatne tylko dla własciciela przepisu i admina
    public List<RecipeResponse> getAllRecipes(){
        List<Recipe> recipes;
        recipes = recipeRepository.findAll();
        List<RecipeResponse> recipeResponses = new ArrayList<>();
        for(Recipe recipe : recipes){
            if (recipe.getStatus().equals(Status.PUBLIC) || getCurrentUser().getRole().equals(Role.ADMIN) || recipe.getUser().equals(getCurrentUser())){
                recipeResponses.add(convertRecipeToRecipeResponse(recipe));
            }
        }
        return recipeResponses;
    }

    // wyswietla prxepis o danym id wszystkim jesli jest publiczny i wlascicielowi lub adminowi prywatny
    public RecipeResponse getRecipe(Long id){
        Recipe recipe = recipeRepository.findRecipeByRecipeId(id).orElseThrow(()->new ResourceNotFoundException("Recipe not found"));
        if (recipe.getUser().equals(getCurrentUser()) || getCurrentUser().getRole().equals(Role.ADMIN) || recipe.getStatus().equals(Status.PUBLIC)){
            return convertRecipeToRecipeResponse(recipe);
        }
        throw new UnsupportedOperationException("recipe is not public");

    }

    //sprawdzić wszystkie publiczne przepisy wybranego usera
    public List<RecipeResponse> getUserAllRecipe(Long userId){
        User user = userRepository.findUserByUserId(userId).orElseThrow(()->new ResourceNotFoundException("User not found"));
        List<RecipeResponse> responseList = new ArrayList<>();
        List<Recipe> recipes = user.getRecipes();
        for (Recipe recipe : recipes){
            if (recipe.getStatus().equals(Status.PUBLIC) || user.equals(getCurrentUser()) || getCurrentUser().getRole().equals(Role.ADMIN)){
                responseList.add(convertRecipeToRecipeResponse(recipe));
            }
        }
        return responseList;
    }

    //wszystkie przepisy zalogowanego usera
    public List<RecipeResponse> getLoggedUserAllRecipes(){
        User user = getCurrentUser();
        List<Recipe> recipes = user.getRecipes();
        List<RecipeResponse> responseList = new ArrayList<>();
        for (Recipe recipe : recipes){
            responseList.add(convertRecipeToRecipeResponse(recipe));
        }
        return responseList;
    }

    //wyswietla przepisy zawierające wyszukiwaną fraze //todo dodć wyszukiwanie po składnikach
    public List<RecipeResponse> getRecipiesByPhrase(String phrase) {
        List<Recipe> recipes = recipeRepository.findAllByTitleContainingOrIngredientsContaining(phrase,phrase);
        List<RecipeResponse> responseList = new ArrayList<>();
        for (Recipe recipe : recipes){
            if (recipe.getStatus().equals(Status.PUBLIC) || getCurrentUser().getRole().equals(Role.ADMIN) || recipe.getUser().equals(getCurrentUser())){
                responseList.add(convertRecipeToRecipeResponse(recipe));
            }
        }
        return responseList;
    }

    //dodawanie przepisu
    public RecipeResponse addRecipe(RecipeRequest request) throws IOException { //przy dodawaniu ocena będzie 0
        Recipe recipeSaved = convertRequestToRecipe(request);
        if (request.getFiles() != null && !request.getFiles().isEmpty()){
            recipeSaved.setImageUrls(saveImages(request.getFiles()));
        }else {
            recipeSaved.setImageUrls(new ArrayList<>());
        }
        recipeRepository.save(recipeSaved);

        User user = recipeSaved.getUser();
        user.getRecipes().add(recipeSaved); //dodanie nowego przepisu do listy przepisów usera
        userRepository.save(user);

        return convertRecipeToRecipeResponse(recipeSaved);
    }

    //update przepisu  każy user może updatować tylko dodane przez niego przepisy
    public RecipeResponse updateRecipe(Long id, RecipeRequest request) throws IOException {//przy update ocena float to średnia z ocen int z encji rating
        Recipe recipe = recipeRepository.findRecipeByRecipeId(id).orElseThrow(()->new ResourceNotFoundException("Recipe not found"));

        if (recipe.getUser().equals(getCurrentUser())){//jesli user niezmienionego przepisu jest równy currentUser

            if (request.getTitle() != null){
                recipe.setTitle(request.getTitle());
            }
            if (request.getDescription() != null){
                recipe.setDescription(request.getDescription());
            }
            if (request.getStatus() != null){
                recipe.setStatus(request.getStatus());
            }
            if (request.getFiles() != null){
                System.out.println("w srodku");
                deleteImages(recipe);
                recipe.setImageUrls(saveImages(request.getFiles()));//todo przy update jesli np dodaje tylko 1 zdj reszta zostaje takich samych żeby sie nie duplikowały
            }

            recipe.setDate(LocalDate.now());
            recipe.setRating(calculateRating(recipe));
            recipeRepository.save(recipe);
            return convertRecipeToRecipeResponse(recipe);
        }
        throw new UnsupportedOperationException("this user is not the owner of the recipe");

    }


    //usuwanie przepisu o wybranym id, tylko właściciel przepisu i admin może go usunąć
    @Transactional
    public String deleteRecipe(Long recipeId) {
        Recipe recipe = recipeRepository.findRecipeByRecipeId(recipeId).orElseThrow(()->new ResourceNotFoundException("Recipe not found"));

        if (recipe.getUser().equals(getCurrentUser()) || getCurrentUser().getRole().equals(Role.ADMIN)){

            recipe.getUser().getRecipes().remove(recipe); //usunięcie przepisu z listy przepisów usera
            deleteImages(recipe);//usuwanie niepotrzebnych juz zdjęc (które byłu przypisane do usuwanego przepisu)
            recipeRepository.deleteByRecipeId(recipeId);

            return "Recipe deleted";
        }
        throw new UnsupportedOperationException("this user is not the owner of the recipe");
    }

    private User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsserame = authentication.getName();
        userRepository.findUserByUserName(currentUsserame).orElseThrow(()->new ResourceNotFoundException("User not found"));
        return userRepository.findUserByUserName(currentUsserame).orElseThrow(()->new ResourceNotFoundException("User not found"));
    }

    private RecipeResponse convertRecipeToRecipeResponse(Recipe recipe){
        return RecipeResponse.builder()
                .recipeId(recipe.getRecipeId())
                .title(recipe.getTitle())
                .ingredients(recipe.getIngredients())
                .description(recipe.getDescription())
                .username(recipe.getUser().getUsername())
                .status(recipe.getStatus())
                .rating(recipe.getRating())
                .date(recipe.getDate())
                .imageUrls(recipe.getImageUrls())
                .build();
    }


    private Recipe convertRequestToRecipe(RecipeRequest request){
        return Recipe.builder()
                .title(request.getTitle())
                .ingredients(request.getIngredients())
                .description(request.getDescription())
                .user(getCurrentUser())
                .status(request.getStatus())
                .date(LocalDate.now())
                .build();
    }

    private float calculateRating(Recipe recipe){
        List<Rating> ratings = recipe.getRatingList();
        int sum = 0;
        if (ratings != null){
            for(Rating rating : ratings){
                sum += rating.getRate();
            }
            if (ratings.size() > 0){
                double value = (double) sum / ratings.size();
                BigDecimal bd = new BigDecimal(Double.toString(value));
                bd = bd.setScale(1, RoundingMode.HALF_UP); // Zaokrąglenie do 1 miejsc po przecinku
                return bd.floatValue();
            }

        }
        return 0;
    }

    private List<String> saveImages(List<MultipartFile> files) throws IOException {
        List<String> urls = new ArrayList<>();
        String orginalFilename;
        for (MultipartFile file : files){
            orginalFilename = file.getOriginalFilename();
            String uniqueFilename = UUID.randomUUID() + orginalFilename;
            Path path = Paths.get(uploadDirectory,uniqueFilename);
            Files.write(path,file.getBytes());
            urls.add(uniqueFilename);
        }
        return urls;
    }

    private void deleteImages(Recipe recipe){
        for (String fileName : recipe.getImageUrls()){
            File file  = new File(uploadDirectory +"/"+ fileName);
            System.out.println(fileName);
            if (file.delete()) {
                System.out.println("Plik został usunięty."); //zamiast sout dać loger
            } else {
                System.err.println("Nie udało się usunąć pliku.");
            }
        }
    }


    //wszystkie opinie do wybranego przepisu
    public List<RatingResponse> getOpinions(Long recipeId) {
        Recipe recipe = recipeRepository.findRecipeByRecipeId(recipeId).orElseThrow(()->new ResourceNotFoundException("Recipe not found"));
        List<Rating> ratingList = recipe.getRatingList();
        List<RatingResponse> responseList = new ArrayList<>();
        for(Rating rating : ratingList){
            responseList.add(convertRatingToRatingResponse(rating));
        }
        return responseList;
    }

    //wszystkie opinie wybranego urzytkownika
    public List<RatingResponse> getUserAllRatings(Long userId) {
        User user = userRepository.findUserByUserId(userId).orElseThrow(()->new ResourceNotFoundException("User not found"));
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
        Recipe recipe = rating.getRecipe();
        if (!recipe.getUser().equals(getCurrentUser())){
            if (request.getRate() >= 0 && request.getRate() <= 10){
                ratingRepository.save(rating);

                recipe.getRatingList().add(rating);//dodanie oceny do listy ocen w przepisie
                recipe.setRating(calculateRating(recipe));//update całkowitej oceny
                recipeRepository.save(recipe);

                User user =  rating.getUser();
                user.getRatingList().add(rating); //dodanie oceny do listy ocen usera
                userRepository.save(user);

                return convertRatingToRatingResponse(rating);
            }else {
                throw new IllegalArgumentException("the rating must be between 0 and 10");

            }
        }
        throw new UnsupportedOperationException("The user can't add a opinion to his recipe");

    }

    //zmiana wybranej opini, tylko własciciel moze zmiecnic
    public RatingResponse updateOpinion(Long recipeId, Long ratingId, RatingRequest request) {
        Rating rating = ratingRepository.findRatingById(ratingId).orElseThrow(()->new ResourceNotFoundException("Rating not found"));
        if (rating.getUser().equals(getCurrentUser())){
            rating.setRate(request.getRate());
            rating.setOpinion(request.getOpininion());
            ratingRepository.save(rating);

            Recipe recipe = recipeRepository.findRecipeByRecipeId(recipeId).orElseThrow(()->new ResourceNotFoundException("Recipe not found"));
            recipe.setRating(calculateRating(recipe));
            recipeRepository.save(recipe);
            return convertRatingToRatingResponse(rating);
        }
        throw new UnsupportedOperationException("this user is not the owner of the review");

    }

    //usuwanie opini tylko właściciel opini oraz admin może ją usunąc
    @Transactional
    public String deleteOpinion(Long ratingId){
        Rating rating = ratingRepository.findRatingById(ratingId).orElseThrow(()->new ResourceNotFoundException("Rating not found"));

        if(rating.getUser().equals(getCurrentUser()) || getCurrentUser().getRole().equals(Role.ADMIN) ){
            Recipe recipe = rating.getRecipe();
            recipe.getRatingList().remove(rating); //usuwanie oceny z listy ocen w encji recipe
            recipe.setRating(calculateRating(recipe));

            rating.getUser().getRatingList().remove(rating);  //usuwanie oceny z listy ocen w encji user
            ratingRepository.deleteById(ratingId);
            return "Opinion deleted";

        }
        throw new UnsupportedOperationException("this user is not the owner of the review");

    }

    private Rating convertRatingRequestToRating(RatingRequest request,Long recipeId) {
        return Rating.builder()
                .rate(request.getRate())
                .opinion(request.getOpininion())
                .recipe(recipeRepository.findRecipeByRecipeId(recipeId).orElseThrow(()->new ResourceNotFoundException("Recipe not found")))
                .user(getCurrentUser())
                .build();
    }

    private RatingResponse convertRatingToRatingResponse(Rating rating) {
        return RatingResponse.builder()
                .id(rating.getId())
                .recipeId(rating.getRecipe().getRecipeId())
                .rate(rating.getRate())
                .opinion(rating.getOpinion())
                .username(rating.getUser().getUsername())
                .build();
    }


}
