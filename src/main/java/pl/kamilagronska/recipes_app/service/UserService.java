package pl.kamilagronska.recipes_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kamilagronska.recipes_app.entity.User;
import pl.kamilagronska.recipes_app.repository.UserRepository;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User addUser(User user){
        //warunki rejstracji
        //nie ma użytkownika o tej samej nazwie uzytkownika
        if(repository.existsByUserName(user.getUserName())){
            throw new IllegalStateException("Username is taken");
        }
        repository.save(user);
        return user;
    }


    public List<User> getAllUsers() {
        return repository.findAll();
    }
}
