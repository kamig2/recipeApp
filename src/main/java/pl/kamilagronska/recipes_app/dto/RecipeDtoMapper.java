package pl.kamilagronska.recipes_app.dto;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kamilagronska.recipes_app.entity.Recipe;
import pl.kamilagronska.recipes_app.entity.User;
import pl.kamilagronska.recipes_app.repository.UserRepository;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class RecipeDtoMapper implements Function<RecipeDto,Recipe> {
    private final UserRepository userRepository;

    @Override
    public Recipe apply(RecipeDto recipeDto) {
        User user = userRepository.findUserByUserName(recipeDto.userName()).orElseThrow(()->new RuntimeException("User not found"));
        return new Recipe(
                recipeDto.recipeId(),
                recipeDto.title(),
                recipeDto.description(),
                user,
                recipeDto.status(),
                recipeDto.note(),
                recipeDto.date()

        );
    }
}
