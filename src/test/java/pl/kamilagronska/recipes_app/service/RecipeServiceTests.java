package pl.kamilagronska.recipes_app.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import pl.kamilagronska.recipes_app.dto.RatingResponse;
import pl.kamilagronska.recipes_app.dto.RecipeDto;
import pl.kamilagronska.recipes_app.dto.RecipeRequest;
import pl.kamilagronska.recipes_app.dto.RecipeResponse;
import pl.kamilagronska.recipes_app.entity.Rating;
import pl.kamilagronska.recipes_app.entity.Recipe;
import pl.kamilagronska.recipes_app.entity.SortParam;
import pl.kamilagronska.recipes_app.entity.User;
import pl.kamilagronska.recipes_app.repository.RatingRepository;
import pl.kamilagronska.recipes_app.repository.RecipeRepository;
import pl.kamilagronska.recipes_app.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class RecipeServiceTests {

    @InjectMocks
    RecipeService recipeService;
    @Mock
    RecipeRepository recipeRepository;
    @Mock
    RatingRepository ratingRepository;
    @Mock
    UserRepository userRepository;

//    @Mock
//    SecurityContextHolder securityContextHolder;

    @Test
    public void recipeRepository_getAllRecipes_returnRecipeResponse(){
        Page<Recipe> recipePage = Mockito.mock(Page.class);
        when(recipeRepository.findAll(Mockito.any(Pageable.class))).thenReturn(recipePage);

        RecipeResponse response = recipeService.getAllRecipes(1,10);

        assertNotNull(response);
    }

    @Test
    public void recipeRepository_getRecipe_returnRecipeDto(){
        User user = User.builder()
                .userId(1)
                .userName("username")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .build();

        Recipe recipe = Recipe.builder()
                .recipeId(1L)
                .title("title")
                .ingredients("ingredients")
                .description("description")
                .user(user)
                .build();

        when(recipeRepository.findById(1L)).thenReturn(Optional.ofNullable(recipe));

        RecipeDto recipeDto = recipeService.getRecipe(1L);

        assertNotNull(recipeDto);
        assertEquals(recipe.getTitle(),recipeDto.getTitle());
    }

    @Test
    public void recipeService_getUserAllRecipe_returnRecipeResponse(){
        User user = User.builder()
                .userId(1)
                .userName("username")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .build();

        Page<Recipe> recipePage = Mockito.mock(Page.class);
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        when(recipeRepository.findAllByUser(Mockito.any(User.class),Mockito.any(Pageable.class))).thenReturn(recipePage);

        RecipeResponse response = recipeService.getUserAllRecipe(1L,1,10);

        assertNotNull(response);
    }

    @Test
    public void recipeService_getLoggedUserAllRecipe_returnRecipeResponse(){
        RecipeResponse response;
        User user = User.builder()
                .userId(1)
                .userName("username")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .build();
        Page<Recipe> recipePage = Mockito.mock(Page.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        try(var securityContextHolder = Mockito.mockStatic(SecurityContextHolder.class)){
            SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            when(authentication.getName()).thenReturn(user.getUsername());
            when(userRepository.findUserByUserName(user.getUsername())).thenReturn(Optional.ofNullable(user));
            when(recipeRepository.findAllByUser(Mockito.eq(user),Mockito.any(Pageable.class))).thenReturn(recipePage);

            response = recipeService.getLoggedUserAllRecipes(1,10);
        }

        assertNotNull(response);
    }

    @Test
    public void recipeService_getRecipeByPhrase_returnRecipeResponse(){
        Page<Recipe> recipePage = Mockito.mock(Page.class);
        when(recipeRepository.findAllByTitleContainingOrIngredientsContaining(Mockito.any(String.class),
                Mockito.any(String.class),Mockito.any(Pageable.class))).thenReturn(recipePage);

        RecipeResponse response = recipeService.getRecipiesByPhrase("phrase",1,10);

        assertNotNull(response);

    }

    @Test
    public void recipeService_getSortedRecipes_returnRecipeResponseSortedByBestRatingParam(){
        Page<Recipe> recipePage = Mockito.mock(Page.class);
        when(recipeRepository.findAll(Mockito.any(Pageable.class))).thenReturn(recipePage);

        RecipeResponse response = recipeService.getSortedRecipies(SortParam.BestRating,1,10);

        assertNotNull(response);
        Mockito.verify(recipeRepository).findAll(PageRequest.of(1,10, Sort.by(Sort.Direction.DESC,"rating")));
    }

    @Test
    public void recipeService_getSortedRecipes_returnRecipeResponseSortedByWorstRatingParam(){
        Page<Recipe> recipePage = Mockito.mock(Page.class);
        when(recipeRepository.findAll(Mockito.any(Pageable.class))).thenReturn(recipePage);

        RecipeResponse response = recipeService.getSortedRecipies(SortParam.WorstRating,1,10);

        assertNotNull(response);
        Mockito.verify(recipeRepository).findAll(PageRequest.of(1,10, Sort.by(Sort.Direction.ASC,"rating")));
    }

    @Test
    public void recipeService_getSortedRecipes_returnRecipeResponseSortedByDateParam(){
        Page<Recipe> recipePage = Mockito.mock(Page.class);
        when(recipeRepository.findAll(Mockito.any(Pageable.class))).thenReturn(recipePage);

        RecipeResponse response = recipeService.getSortedRecipies(SortParam.Date,1,10);

        assertNotNull(response);
        Mockito.verify(recipeRepository).findAll(PageRequest.of(1,10, Sort.by(Sort.Direction.ASC,"date")));
    }

    @Test
    public void recipeService_getSortedRecipes_returnRecipeResponseSortedByTitleParam(){
        Page<Recipe> recipePage = Mockito.mock(Page.class);
        when(recipeRepository.findAll(Mockito.any(Pageable.class))).thenReturn(recipePage);

        RecipeResponse response = recipeService.getSortedRecipies(SortParam.Title,1,10);

        assertNotNull(response);
        Mockito.verify(recipeRepository).findAll(PageRequest.of(1,10, Sort.by(Sort.Direction.ASC,"title")));
    }

    @Test
    public void recipeService_addRecipe_ReturnRecipeDtoWithImageUrlsNotNull() throws IOException {
        List<MultipartFile> files = new ArrayList<>();
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        files.add(multipartFile);
        RecipeRequest request = RecipeRequest.builder()
                .title("title")
                .ingredients("ingredients")
                .description("description")
                .files(files)
                .build();



        User user = User.builder()
                .userId(1)
                .userName("username")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .build();
        List<Recipe> recipeList = new ArrayList<>();
        Recipe recipe = new Recipe();
        RecipeDto recipeDto;
        recipeList.add(recipe);
        user.setRecipes(recipeList);

        try(var securityContexHolder = Mockito.mockStatic(SecurityContextHolder.class)){
            SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            Authentication authentication = Mockito.mock(Authentication.class);

            securityContexHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(user.getUsername());
            when(userRepository.findUserByUserName(user.getUsername())).thenReturn(Optional.ofNullable(user));
            when(recipeRepository.save(Mockito.any(Recipe.class))).thenReturn(recipe);
            when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

            //zasymulowane dane w metodzie saveImages
            String orginalFilename = "jmage.jpg";
            when(multipartFile.getOriginalFilename()).thenReturn(orginalFilename);
            String uniqueFilename = UUID.randomUUID() + orginalFilename;
            String uploadDirectory = System.getProperty("user.dir")+"/upload";
            Path path = Paths.get(uploadDirectory,uniqueFilename);
            byte[] fileBytes = "content".getBytes();
            when(multipartFile.getBytes()).thenReturn(fileBytes);
            try(var file = Mockito.mockStatic(Files.class)){
                file.when(() -> Files.write(Mockito.any(Path.class),Mockito.any(byte[].class))).thenReturn(path);

                recipeDto = recipeService.addRecipe(request);

            }
        }
        assertEquals(2, user.getRecipes().size());
        assertNotNull(recipeDto);

    }

    ///////////////
    @Test
    public void recipeService_getOpinions_ReturnListOfRatingResponse(){
        User user = User.builder()
                .userId(1)
                .userName("username")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .build();
        Rating rating = Rating.builder()
                .id(1L)
                .rate(5)
                .opinion("opinion")
                .user(user)
                .build();
        List<Rating> ratingList = new ArrayList<>();
        ratingList.add(rating);

        Recipe recipe = Recipe.builder()
                .recipeId(1L)
                .title("title")
                .ingredients("ingredients")
                .description("description")
                .user(user)
                .ratingList(ratingList)
                .build();

        ratingList.get(0).setRecipe(recipe);


        when(recipeRepository.findById(1L)).thenReturn(Optional.ofNullable(recipe));

        List<RatingResponse> responseList = recipeService.getOpinions(1L);

        assertEquals(1,responseList.size());

    }

    @Test
    public void recipeService_getUserAllRatings_returnRatingResponseList(){
        Recipe recipe = Recipe.builder()
                .recipeId(1L)
                .title("title")
                .ingredients("ingredients")
                .description("description")
                .build();

        Rating rating = Rating.builder()
                .id(1L)
                .rate(5)
                .opinion("opinion")
                .recipe(recipe)
                .build();
        List<Rating> ratingList = new ArrayList<>();
        ratingList.add(rating);


        User user = User.builder()
                .userId(1)
                .userName("username")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .ratingList(ratingList)
                .build();

        ratingList.get(0).setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        List<RatingResponse> responseList = recipeService.getUserAllRatings(1L);

        assertEquals(1,responseList.size());
    }

    @Test
    public void recipeService_getLoggedUserAllRatings_returnListofRatingResponse(){
        Recipe recipe = Recipe.builder()
                .recipeId(1L)
                .title("title")
                .ingredients("ingredients")
                .description("description")
                .build();

        Rating rating = Rating.builder()
                .id(1L)
                .rate(5)
                .opinion("opinion")
                .recipe(recipe)
                .build();

        List<Rating> ratingList = new ArrayList<>();
        ratingList.add(rating);

        User user = User.builder()
                .userId(1)
                .userName("username")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .ratingList(ratingList)
                .build();

        ratingList.get(0).setUser(user);

        try (var securitContexHolder = Mockito.mockStatic(SecurityContextHolder.class)){
            SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            Authentication authentication = Mockito.mock(Authentication.class);

            securitContexHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(user.getUsername());
            when(userRepository.findUserByUserName(Mockito.any(String.class))).thenReturn(Optional.ofNullable(user));

            List<RatingResponse> response = recipeService.getLoggedUserAllRatings();

            assertNotNull(response);
        }
    }


}
