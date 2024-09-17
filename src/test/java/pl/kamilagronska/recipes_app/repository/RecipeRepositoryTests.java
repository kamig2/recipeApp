package pl.kamilagronska.recipes_app.repository;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.kamilagronska.recipes_app.entity.Recipe;
import pl.kamilagronska.recipes_app.entity.Status;
import pl.kamilagronska.recipes_app.entity.User;

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
                .status(Status.PUBLIC)
                .build();

        Recipe savedRecipe = recipeRepository.save(recipe);

        Assertions.assertNotNull(savedRecipe);
        Assertions.assertEquals("title",savedRecipe.getTitle());
        Assertions.assertEquals("ingredients",savedRecipe.getIngredients());
        Assertions.assertEquals("description",savedRecipe.getDescription());
    }
}
