package pl.kamilagronska.recipes_app.repository;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.kamilagronska.recipes_app.entity.Rating;
import pl.kamilagronska.recipes_app.entity.Recipe;
import pl.kamilagronska.recipes_app.entity.User;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class RatingRepositoryTests {
    @Autowired
    RatingRepository ratingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RecipeRepository recipeRepository;

    @Test
    public void RatingRepository_Save_ReturnSavedRating(){
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

        Rating rating = Rating.builder()
                .rate(5)
                .opinion("opinion")
                .recipe(savedRecipe)
                .user(savedUser)
                .build();

        Rating savedRating = ratingRepository.save(rating);

        Assertions.assertNotNull(savedRating);
        Assertions.assertEquals(5,savedRating.getRate());
        Assertions.assertEquals("opinion",savedRating.getOpinion());
    }

    @Test
    public void RatingRepository_FindAll_ReturnMoreThenOneRating(){
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

        Rating rating = Rating.builder()
                .rate(5)
                .opinion("opinion")
                .recipe(savedRecipe)
                .user(savedUser)
                .build();

        Rating rating2 = Rating.builder()
                .rate(5)
                .opinion("opinion2")
                .recipe(savedRecipe)
                .user(savedUser)
                .build();

        ratingRepository.save(rating);
        ratingRepository.save(rating2);

        List<Rating> ratingList = ratingRepository.findAll();

        Assertions.assertNotNull(ratingList);
        Assertions.assertEquals(2,ratingList.size());
    }

    @Test
    public void RatingRepository_FindById_ReturnOneRating(){
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

        Rating rating = Rating.builder()
                .rate(5)
                .opinion("opinion")
                .recipe(savedRecipe)
                .user(savedUser)
                .build();

        ratingRepository.save(rating);

        Rating returnedRating = ratingRepository.findById(rating.getId()).get();

        Assertions.assertNotNull(returnedRating);
        Assertions.assertEquals(5,returnedRating.getRate());
        Assertions.assertEquals("opinion",returnedRating.getOpinion());
    }

    @Test
    public void RatingRepository_UpdateRating_ReturnRating(){
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

        Rating rating = Rating.builder()
                .rate(5)
                .opinion("opinion")
                .recipe(savedRecipe)
                .user(savedUser)
                .build();

        Rating savedRating = ratingRepository.save(rating);
        savedRating.setRate(10);
        savedRating.setOpinion("newOpinion");

        Rating updatedRating = ratingRepository.save(savedRating);

        Assertions.assertNotNull(updatedRating);
        Assertions.assertEquals(10,updatedRating.getRate());
        Assertions.assertEquals("newOpinion",updatedRating.getOpinion());
    }

    @Test
    public void RatingRepository_DeleteRating_ReturnRatingIsEmpty(){
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

        Rating rating = Rating.builder()
                .rate(5)
                .opinion("opinion")
                .recipe(savedRecipe)
                .user(savedUser)
                .build();

        ratingRepository.save(rating);
        ratingRepository.deleteById(rating.getId());

        Optional<Rating> deletedRating = ratingRepository.findById(rating.getId());

        Assertions.assertTrue(deletedRating.isEmpty());
    }


}

