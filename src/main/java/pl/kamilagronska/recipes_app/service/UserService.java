package pl.kamilagronska.recipes_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.kamilagronska.recipes_app.dto.UserDto;
import pl.kamilagronska.recipes_app.dto.UserDtoMapper;
import pl.kamilagronska.recipes_app.entity.User;
import pl.kamilagronska.recipes_app.repository.UserRepository;
import pl.kamilagronska.recipes_app.request.UserLoginRequest;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repository;
    private final UserDtoMapper userDtoMapper;

    @Autowired
    public UserService(UserRepository repository, UserDtoMapper userDtoMapper) {
        this.repository = repository;
        this.userDtoMapper = userDtoMapper;
    }

    public ResponseEntity addUser(User user){
        //warunki rejstracji
        //nie ma użytkownika o tej samej nazwie uzytkownika
        if(repository.existsByUserName(user.getUserName())){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
        User userSaved = repository.save(user);
        return ResponseEntity.ok(userSaved);
    }


    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public User login(UserLoginRequest request) {//dodać kodowanie chasła
        if (repository.existsByUserName(request.userName())){ //błąd 500
            User user = repository.findUserByUserName(request.userName());
            if (user.getPassword()== request.password()){
                return repository.findUserByUserName(user.getUserName()) ;
            }
        }
        throw new RuntimeException();//dodać odsługe wyjątków
    }
}
