package pl.kamilagronska.recipes_app.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.kamilagronska.recipes_app.dto.RatingRequest;
import pl.kamilagronska.recipes_app.dto.RatingResponse;
import pl.kamilagronska.recipes_app.dto.RecipeRequest;
import pl.kamilagronska.recipes_app.dto.RecipeResponse;
import pl.kamilagronska.recipes_app.entity.SortParam;
import pl.kamilagronska.recipes_app.entity.Status;
import pl.kamilagronska.recipes_app.service.RecipeService;

import java.io.IOException;
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

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecipeResponse>> getUserAllRecipes(@PathVariable Long userId){
        return ResponseEntity.ok(recipeService.getUserAllRecipe(userId));
    }

    @GetMapping("/userRecipes")
    public ResponseEntity<List<RecipeResponse>> getLoggedUserAllRecipes(){
        return ResponseEntity.ok(recipeService.getLoggedUserAllRecipes());
    }

    @GetMapping("/find/{phrase}")
    public ResponseEntity<List<RecipeResponse>> getRecipiesByPhrase(@PathVariable String phrase){
        return ResponseEntity.ok(recipeService.getRecipiesByPhrase(phrase));
    }

//todo dodać endpoint który wyswietla posortowane przepisy według wyboru

    @GetMapping("sortedBy/{param}")
    public ResponseEntity<List<RecipeResponse>> getSortedRecipes(@PathVariable SortParam param){
        return ResponseEntity.ok(recipeService.getSortedRecipies(param));
    }

    @PostMapping(value = "/add",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<RecipeResponse> addRecipe(@RequestParam(value = "title") String title,
                                                    @RequestParam(value = "ingredients") String ingredients,
                                                    @RequestParam("description") String description,
                                                    @RequestParam("status") Status status,
                                                    @RequestParam(value = "files",required = false)List<MultipartFile> files) throws IOException {
        //todo możliwośc wybrania które zdj z listy będzie wyswietlane na "okładce"
        RecipeRequest request = RecipeRequest.builder()
                .title(title)
                .ingredients(ingredients)
                .description(description)
                .status(status)
                .files(files)
                .build();
        return new ResponseEntity<>(recipeService.addRecipe(request), HttpStatus.CREATED);
    }

    @PutMapping(value = "/update/{recipeId}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<RecipeResponse> updateRecipe(@PathVariable Long recipeId,
                                                       @RequestParam(value = "title",required = false) String title,
                                                       @RequestParam(value = "ingredients") String ingredients,
                                                       @RequestParam(value = "description",required = false) String description,
                                                       @RequestParam(value = "status",required = false) Status status,
                                                       @RequestParam(value = "files",required = false)List<MultipartFile> files) throws IOException {
        RecipeRequest request = RecipeRequest.builder()
                .title(title)
                .ingredients(ingredients)
                .description(description)
                .status(status)
                .files(files)
                .build();
        return ResponseEntity.ok(recipeService.updateRecipe(recipeId,request/*,files*/));

    }
    @DeleteMapping("/delete/{recipeId}")
    public ResponseEntity<String> deleteRecipe(@PathVariable Long recipeId){
        return ResponseEntity.ok(recipeService.deleteRecipe(recipeId));
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
    @PutMapping("/{recipeId}/update/rating/{ratingId}")
    public ResponseEntity<RatingResponse> updateOpinion(@PathVariable Long recipeId,
                                                        @PathVariable Long ratingId,
                                                        @RequestBody RatingRequest request){
        return ResponseEntity.ok(recipeService.updateOpinion(recipeId,ratingId,request));

    }

    @DeleteMapping("/delete/rating/{ratingId}")
    public ResponseEntity<String> deleteOpinion(@PathVariable Long ratingId){
        return ResponseEntity.ok(recipeService.deleteOpinion(ratingId));
    }


}
