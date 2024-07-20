//package pl.kamilagronska.recipes_app.dto;
//
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import pl.kamilagronska.recipes_app.entity.Recipe;
//import pl.kamilagronska.recipes_app.entity.User;
//import pl.kamilagronska.recipes_app.repository.UserRepository;
//
//import java.util.function.Function;
//
//@Service
//@RequiredArgsConstructor
//public class RecipeDtoMapper implements Function<RecipeRequest,Recipe> {
//    private final UserRepository userRepository;
//
//    @Override
//    public Recipe apply(RecipeRequest recipeDto) {
//        User user = userRepository.findUserByUserName(recipeDto.getUsername()).orElseThrow(()->new RuntimeException("User not found"));
//        return Recipe.builder()
//                .title(recipeDto.getTitle())
//                .description(recipeDto.getDescription())
//                .user(user)
//                .status(recipeDto.getStatus())
//                .note(recipeDto.getNote())
//                .date(recipeDto.getDate())
//                .build();
//    }
//}
