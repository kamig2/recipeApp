package pl.kamilagronska.recipes_app.controller;

import jakarta.persistence.EnumType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kamilagronska.recipes_app.dto.RecipeDto;
import pl.kamilagronska.recipes_app.entity.Recipe;
import pl.kamilagronska.recipes_app.entity.Status;
import pl.kamilagronska.recipes_app.service.RecipeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    @GetMapping
    public List<Recipe> getAllRecipes(){
        return recipeService.getAllRecipes();
    }

    @GetMapping("/{id}")
    public Recipe getRecipe(@PathVariable Long id){
        return recipeService.getRecipe(id);//sprawdzić czy dzial,a po skonfigurowaniu spring security
    }

    @PostMapping("/add")
    public ResponseEntity addRecipe(@RequestBody RecipeDto recipe){//jako argument DTO? //dodac path variable
        return recipeService.addRecipe(recipe);//sprawdzić czy dzial,a po skonfigurowaniu spring security
    }

    /*@PostMapping("/update/{id}")
    public ResponseEntity updateRcipe(@PathVariable Long id,
                                      @RequestParam String title,
                                      @RequestParam String description,
                                      @RequestParam String username,
                                      @RequestParam Status status)*/

}
