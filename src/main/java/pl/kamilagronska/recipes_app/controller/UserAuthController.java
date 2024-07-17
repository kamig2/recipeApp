package pl.kamilagronska.recipes_app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kamilagronska.recipes_app.entity.User;
import pl.kamilagronska.recipes_app.dto.UserLoginDto;
import pl.kamilagronska.recipes_app.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {
    private final UserService service;


    @PostMapping(  "/register")
    public ResponseEntity registerNewUser(@RequestBody User user){
        return service.addUser(user);
    }

    @GetMapping("/login")
    public User login(@RequestBody UserLoginDto request){
        return service.login(request);
    }

    /*@GetMapping( "/users")
    public List<User> getAllUsers(){
        return service.getAllUsers();
    }*/
}
