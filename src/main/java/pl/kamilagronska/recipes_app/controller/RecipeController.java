package pl.kamilagronska.recipes_app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.kamilagronska.recipes_app.dto.RatingRequest;
import pl.kamilagronska.recipes_app.dto.RatingResponse;
import pl.kamilagronska.recipes_app.dto.RecipeRequest;
import pl.kamilagronska.recipes_app.dto.RecipeResponse;
import pl.kamilagronska.recipes_app.service.RecipeService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public static String uploadDirectory = System.getProperty("user.dir")+"/upload";

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

    @PostMapping("/add")
    public ResponseEntity<RecipeResponse> addRecipe(@ModelAttribute RecipeRequest request,
                                                    @RequestParam("files")List<MultipartFile> files) throws IOException {
        //todo zmienić forme zapisu pliku aby dac unikalną nazwe
        //todo dodać update zdjęc do funkcji update
        //todo w response dodac url
        //todo możliwośc wybrania które zdj z listy będzie wyswietlane na "okładce"
        List<String> urls = new ArrayList<>();
        String orginalFilename;
        for (MultipartFile file : files){
            orginalFilename = file.getOriginalFilename();
            Path path = Paths.get(uploadDirectory,orginalFilename);
            Files.write(path,file.getBytes());
            urls.add(orginalFilename);
        }
        return new ResponseEntity<>(recipeService.addRecipe(request,urls), HttpStatus.CREATED);
    }

    @PutMapping("/update/{recipeId}")
    public ResponseEntity<RecipeResponse> updateRecipe(@PathVariable Long recipeId, @RequestBody RecipeRequest request){
        return ResponseEntity.ok(recipeService.updateRecipe(recipeId,request));

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
