package pl.kamilagronska.recipes_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kamilagronska.recipes_app.dto.UserResponse;
import pl.kamilagronska.recipes_app.entity.User;
import pl.kamilagronska.recipes_app.exception.ResourceNotFoundException;
import pl.kamilagronska.recipes_app.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;


    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> responseList = new ArrayList<>();
        for(User user: users){
            responseList.add(convertUserToUserResponse(user));
        }
        return responseList;
    }

    public UserResponse getUserById(Long userId) {
        User user = userRepository.findUserByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return convertUserToUserResponse(user);
    }

    private UserResponse convertUserToUserResponse(User user) {
        return UserResponse.builder()
                .userID(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .build();
    }
}
