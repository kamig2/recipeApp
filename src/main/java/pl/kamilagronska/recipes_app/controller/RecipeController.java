package pl.kamilagronska.recipes_app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.kamilagronska.recipes_app.dto.RecipeRequest;
import pl.kamilagronska.recipes_app.dto.RecipeResponse;
import pl.kamilagronska.recipes_app.entity.Status;
import pl.kamilagronska.recipes_app.entity.User;
import pl.kamilagronska.recipes_app.service.RecipeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/recipes")
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

}
