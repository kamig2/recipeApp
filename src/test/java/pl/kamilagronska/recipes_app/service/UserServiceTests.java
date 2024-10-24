package pl.kamilagronska.recipes_app.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kamilagronska.recipes_app.dto.UserResponse;
import pl.kamilagronska.recipes_app.entity.User;
import pl.kamilagronska.recipes_app.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @InjectMocks
    UserService userService;
    @Mock
    UserRepository userRepository;

    @Test
    public void userService_getAllUsers_returnResponseList(){
        User user = User.builder()
                .userId(1)
                .userName("username")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .build();

        User user2 = User.builder()
                .userId(2)
                .userName("username2")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .build();
        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user2);

        when(userRepository.findAll()).thenReturn(users);

        List<UserResponse> responseList = userService.getAllUsers();

        assertEquals(users.size(),responseList.size());
    }

    @Test
    public void userService_getUserById_returnUserResponse(){
        User user = User.builder()
                .userId(1)
                .userName("username")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        UserResponse response = userService.getUserById(user.getUserId());

        assertNotNull(response);
        assertEquals(user.getUsername(),response.getUsername());
    }
}
