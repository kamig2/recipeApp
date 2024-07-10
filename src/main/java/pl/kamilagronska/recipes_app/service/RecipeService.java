package pl.kamilagronska.recipes_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kamilagronska.recipes_app.entity.Recipe;
import pl.kamilagronska.recipes_app.repository.RecipeRepository;

import java.util.List;

@Service
public class RecipeService {
    private final RecipeRepository repository;


    @Autowired
    public RecipeService(RecipeRepository repository) {
        this.repository = repository;
    }

    public List<Recipe> service(){
        return repository.findAll();
    }
}
