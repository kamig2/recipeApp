package pl.kamilagronska.recipes_app.dto;


import org.springframework.stereotype.Service;
import pl.kamilagronska.recipes_app.entity.User;

import java.util.function.Function;

@Service
public class UserDtoMapper implements Function<User,UserDto> {
    @Override
    public UserDto apply(User user) {
        return new UserDto(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUserName()
        );
    }
}
