package pl.kamilagronska.recipes_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kamilagronska.recipes_app.dto.UserDto;
import pl.kamilagronska.recipes_app.entity.User;
import pl.kamilagronska.recipes_app.request.UserLoginRequest;
import pl.kamilagronska.recipes_app.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping(  "/register")
    public ResponseEntity registerNewUser(@RequestBody User user){
        return service.addUser(user);
    }

    @GetMapping("/login")
    public User login(@RequestBody UserLoginRequest request){
        return service.login(request);
    }

    @GetMapping( "/users")
    public List<User> getAllUsers(){
        return service.getAllUsers();
    }
}
