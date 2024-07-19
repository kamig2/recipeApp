package pl.kamilagronska.recipes_app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kamilagronska.recipes_app.dto.UserRegisterRequest;
import pl.kamilagronska.recipes_app.dto.UserAuthResponse;
import pl.kamilagronska.recipes_app.entity.User;
import pl.kamilagronska.recipes_app.dto.UserLoginRequest;
import pl.kamilagronska.recipes_app.service.UserAuthService;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserAuthController {
    private final UserAuthService service;


    @PostMapping(  "/register")
    public ResponseEntity<UserAuthResponse> registerNewUser(@RequestBody UserRegisterRequest request){
        UserAuthResponse response = service.addUser(request);
        if (response == null){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserAuthResponse> login(@RequestBody UserLoginRequest request){

        return ResponseEntity.ok(service.login(request));
    }

    /*@GetMapping( "/users")
    public List<User> getAllUsers(){
        return service.getAllUsers();
    }*/
}
