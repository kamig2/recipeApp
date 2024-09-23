package pl.kamilagronska.recipes_app.repository;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.kamilagronska.recipes_app.entity.Recipe;
import pl.kamilagronska.recipes_app.entity.User;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class RecipeRepositoryTests {

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    public void RecipeRepository_Save_ReturnSavedRecipe(){
        User user = User.builder()
                .userName("username")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .build();

        User savedUser = userRepository.save(user);

        Recipe recipe = Recipe.builder()
                .title("title")
                .ingredients("ingredients")
                .description("description")
                .user(savedUser)
                .build();

        Recipe savedRecipe = recipeRepository.save(recipe);

        Assertions.assertNotNull(savedRecipe);
        Assertions.assertEquals("title",savedRecipe.getTitle());
        Assertions.assertEquals("ingredients",savedRecipe.getIngredients());
        Assertions.assertEquals("description",savedRecipe.getDescription());
    }

    @Test
    public void RecipeRepository_FindAll_ReturnMoreThenOneRecipe(){
        User user = User.builder()
                .userName("username")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .build();

        User savedUser = userRepository.save(user);

        Recipe recipe = Recipe.builder()
                .title("title")
                .ingredients("ingredients")
                .description("description")
                .user(savedUser)
                .build();

        Recipe recipe2 = Recipe.builder()
                .title("title2")
                .ingredients("ingredients2")
                .description("description2")
                .user(savedUser)
                .build();

        recipeRepository.save(recipe);
        recipeRepository.save(recipe2);

        List<Recipe> recipeList = recipeRepository.findAll();

        Assertions.assertEquals(recipeList.size(),2);
        Assertions.assertNotNull(recipeList);
    }

    @Test
    public void RecipeRepository_FindById_ReturnOneRecipe(){
        User user = User.builder()
                .userName("username")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .build();

        User savedUser = userRepository.save(user);

        Recipe recipe = Recipe.builder()
                .title("title")
                .ingredients("ingredients")
                .description("description")
                .user(savedUser)
                .build();

        recipeRepository.save(recipe);

        Recipe returnedRecipe = recipeRepository.findById(recipe.getRecipeId()).get();

        Assertions.assertNotNull(returnedRecipe);
        Assertions.assertEquals("title", returnedRecipe.getTitle());
        Assertions.assertEquals("ingredients", returnedRecipe.getIngredients());
        Assertions.assertEquals("description", returnedRecipe.getDescription());

    }

    @Test
    public void RecipeRepository_UpdateRecipe_ReturnRecipe(){
        User user = User.builder()
                .userName("username")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .build();

        User savedUser = userRepository.save(user);

        Recipe recipe = Recipe.builder()
                .title("title")
                .ingredients("ingredients")
                .description("description")
                .user(savedUser)
                .build();

        Recipe savedRecipe = recipeRepository.save(recipe);
        savedRecipe.setTitle("newTitle");
        savedRecipe.setIngredients("newIngredients");
        savedRecipe.setDescription("newDescription");

        Recipe updatedRecipe = recipeRepository.save(savedRecipe);

        Assertions.assertNotNull(updatedRecipe);
        Assertions.assertEquals("newTitle", updatedRecipe.getTitle());
        Assertions.assertEquals("newIngredients", updatedRecipe.getIngredients());
        Assertions.assertEquals("newDescription", updatedRecipe.getDescription());

    }

    @Test
    public void RecipeRepository_DeleteRecipe_ReturnRecipeIsEmpty(){
        User user = User.builder()
                .userName("username")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .build();

        User savedUser = userRepository.save(user);

        Recipe recipe = Recipe.builder()
                .title("title")
                .ingredients("ingredients")
                .description("description")
                .user(savedUser)
                .build();

        recipeRepository.save(recipe);

        recipeRepository.deleteById(recipe.getRecipeId());

        Optional<Recipe> deletedRecipe = recipeRepository.findById(recipe.getRecipeId());

        Assertions.assertTrue(deletedRecipe.isEmpty());
    }

    @Test
    public void RecipeRepository_findAllByTitleContainingOrIngredientsContaining_ReturnTwoRecipes(){
        User user = User.builder()
                .userName("username")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .build();

        User savedUser = userRepository.save(user);

        Recipe recipe = Recipe.builder()
                .title("title")
                .ingredients("i")
                .description("description")
                .user(savedUser)
                .build();

        Recipe recipe2 = Recipe.builder()
                .title("t")
                .ingredients("ingredients2")
                .description("description2")
                .user(savedUser)
                .build();

        recipeRepository.save(recipe);
        recipeRepository.save(recipe2);

        Pageable pageable = PageRequest.of(0,10);

        Page<Recipe> returnedRecipes = recipeRepository.findAllByTitleContainingOrIngredientsContaining("title","ingredients2",pageable);

        Assertions.assertNotNull(returnedRecipes);
        Assertions.assertEquals(2,returnedRecipes.getTotalElements());

    }

    @Test
    public void RecipeRepository_FindAllByUser_ReturnTwoRecipes(){
        User user = User.builder()
                .userName("username")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .build();

        User savedUser = userRepository.save(user);

        Recipe recipe = Recipe.builder()
                .title("title")
                .ingredients("ingredients")
                .description("description")
                .user(savedUser)
                .build();

        Recipe recipe2 = Recipe.builder()
                .title("title2")
                .ingredients("ingredients2")
                .description("description2")
                .user(savedUser)
                .build();

        recipeRepository.save(recipe);
        recipeRepository.save(recipe2);

        Pageable pageable = PageRequest.of(0,10);

        Page<Recipe> returnedRecipes = recipeRepository.findAllByUser(savedUser,pageable);

        Assertions.assertNotNull(returnedRecipes);
        Assertions.assertEquals(2,returnedRecipes.getTotalElements());

    }


}












