package pl.kamilagronska.recipes_app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.kamilagronska.recipes_app.dto.RatingRequest;
import pl.kamilagronska.recipes_app.dto.RatingResponse;
import pl.kamilagronska.recipes_app.dto.RecipeRequest;
import pl.kamilagronska.recipes_app.dto.RecipeResponse;
import pl.kamilagronska.recipes_app.entity.Status;
import pl.kamilagronska.recipes_app.entity.User;
import pl.kamilagronska.recipes_app.service.RecipeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    @GetMapping
    public ResponseEntity<List<RecipeResponse>> getAllRecipes(){
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getRecipe(@PathVariable Long id){
        return ResponseEntity.ok(recipeService.getRecipe(id));//sprawdzić czy dzial,a po skonfigurowaniu spring security
    }

    @PostMapping("/add")
    public ResponseEntity<RecipeResponse> addRecipe(@RequestBody RecipeRequest recipe){//jako argument DTO? //dodac path variable
        return ResponseEntity.ok(recipeService.addRecipe(recipe));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity updateRecipe(@PathVariable Long id, @RequestBody RecipeRequest request){
        return ResponseEntity.ok(recipeService.updateRecipe(id,request));

    }
    @DeleteMapping("/delete/{recipeId}")
    public ResponseEntity deleteRecipe(@PathVariable Long recipeId){
        return recipeService.deleteRecipe(recipeId);
    }

    @GetMapping("/{recipeId}/rating")
    public ResponseEntity<List<RatingResponse>> getOpinions(@PathVariable Long recipeId){
        return ResponseEntity.ok(recipeService.getOpinions(recipeId));
    }

    //wszystkie opinie dodane przez wybranego usera
    @GetMapping("/user/{userId}/rating")
    public ResponseEntity<List<RatingResponse>> getUserAllRatings(@PathVariable Long userId){
        return ResponseEntity.ok(recipeService.getUserAllRatings(userId));
    }

    @GetMapping("/user/rating")
    public ResponseEntity<List<RatingResponse>> getLoggedUserAllRatings(){
        return ResponseEntity.ok(recipeService.getLoggedUserAllRatings());
    }


    @PostMapping("/{recipeId}/add/rating")
    public ResponseEntity<RatingResponse> addOpinion(@PathVariable Long recipeId, @RequestBody RatingRequest request){
        return ResponseEntity.ok(recipeService.addOpinion(recipeId,request));

    }
    @PostMapping("/{recipeId}/update/rating/{ratingId}")
    public ResponseEntity<RatingResponse> updateOpinion(@PathVariable Long recipeId,
                                                        @PathVariable Long ratingId,
                                                        @RequestBody RatingRequest request){
        return ResponseEntity.ok(recipeService.updateOpinion(recipeId,ratingId,request));

    }

    @DeleteMapping("/delete/rating/{ratingId}")
    public ResponseEntity deleteOpinion(@PathVariable Long ratingId){
        return recipeService.deleteOpinion(ratingId);
    }





}
