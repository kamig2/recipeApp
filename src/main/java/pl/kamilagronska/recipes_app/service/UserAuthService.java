package pl.kamilagronska.recipes_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.kamilagronska.recipes_app.dto.UserAuthResponse;
import pl.kamilagronska.recipes_app.dto.UserDtoMapper;
import pl.kamilagronska.recipes_app.dto.UserRegisterRequest;
import pl.kamilagronska.recipes_app.entity.Role;
import pl.kamilagronska.recipes_app.entity.User;
import pl.kamilagronska.recipes_app.repository.UserRepository;
import pl.kamilagronska.recipes_app.dto.UserLoginRequest;
import pl.kamilagronska.recipes_app.security.JwtService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAuthService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    public UserAuthResponse addUser(UserRegisterRequest request){
        //warunki rejstracji
        //nie ma użytkownika o tej samej nazwie uzytkownika
        if(repository.existsByUserName(request.getUsername())){
            return null;
        }
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .userName(request.getUsername())
                .password(encoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        repository.save(user);
        String token = jwtService.generateToken(user);
        return UserAuthResponse.builder()
                .token(token)
                .build();
    }


    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public UserAuthResponse login(UserLoginRequest request) {//dodać kodowanie chasła
        /*if (repository.existsByUserName(request.getUsername())){ //błąd 500
            UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());
            if (user.getPassword().equals(request.getPassword())){
                String token = jwtService.generateToken(user);
                return UserAuthResponse.builder()
                        .token(token)
                        .build();
            }
        }*/

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));
        User user = repository.findUserByUserName(request.getUsername()).orElseThrow(()->new UsernameNotFoundException("User not found"));
        String token = jwtService.generateToken(user);
        return UserAuthResponse.builder()
                .token(token)
                .build();
    }
}
