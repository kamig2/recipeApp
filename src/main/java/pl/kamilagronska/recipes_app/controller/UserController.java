package pl.kamilagronska.recipes_app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kamilagronska.recipes_app.security.JwtService;
import pl.kamilagronska.recipes_app.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {


    private final UserService userService;
    private final JwtService jwtService;

    /*@PostMapping("/logout")
    public ResponseEntity logout(@RequestBody String token){
        String jwt = token.substring(7);
        jwtService.invalidateToken(jwt);
    }*/

}
