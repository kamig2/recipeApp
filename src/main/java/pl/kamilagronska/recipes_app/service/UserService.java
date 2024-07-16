package pl.kamilagronska.recipes_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.kamilagronska.recipes_app.dto.UserDtoMapper;
import pl.kamilagronska.recipes_app.entity.User;
import pl.kamilagronska.recipes_app.repository.UserRepository;
import pl.kamilagronska.recipes_app.dto.UserLoginDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final UserDtoMapper userDtoMapper;

    public ResponseEntity addUser(User user){
        //warunki rejstracji
        //nie ma użytkownika o tej samej nazwie uzytkownika
        if(repository.existsByUserName(user.getUsername())){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
        User userSaved = repository.save(user);
        return ResponseEntity.ok(userSaved);
    }


    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public User login(UserLoginDto request) {//dodać kodowanie chasła
        if (repository.existsByUserName(request.userName())){ //błąd 500
            /*User user = repository.findUserByUserName(request.userName()).orElseThrow(()->new UsernameNotFoundException("User not found"));
            if (user.getPassword() == request.password()){
                return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), );
            }*/
        }
        throw new RuntimeException();//dodać odsługe wyjątków
    }
}
