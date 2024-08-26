package pl.kamilagronska.recipes_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kamilagronska.recipes_app.entity.Recipe;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe,Long> {
    Optional<Recipe> findRecipeByRecipeId(Long id);
    void deleteByRecipeId(Long id);

    List<Recipe> findAllByTitleContainingOrIngredientsContaining(String titlePhrase,String ingredientsPhrase);

}
