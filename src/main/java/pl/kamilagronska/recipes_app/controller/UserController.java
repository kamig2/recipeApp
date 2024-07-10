package pl.kamilagronska.recipes_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.kamilagronska.recipes_app.entity.User;
import pl.kamilagronska.recipes_app.service.UserService;

import java.util.List;

@RestController
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping( path = "/register")
    public User registerNewUser(@RequestBody User user){
        return service.addUser(user);
    }

    @GetMapping(path = "/users")
    public List<User> getAllUsers(){
        return service.getAllUsers();
    }
}
