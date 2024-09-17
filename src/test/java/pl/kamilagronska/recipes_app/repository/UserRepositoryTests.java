package pl.kamilagronska.recipes_app.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.kamilagronska.recipes_app.entity.User;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTests {
    @Autowired
    UserRepository userRepository;

    @Test
    public void UserRepository_Save_ReturnSavedUser(){
        User user = User.builder()
                .userName("username")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .build();

        User savedUser = userRepository.save(user);

        Assertions.assertNotNull(savedUser);
        Assertions.assertEquals("username",savedUser.getUsername());
        Assertions.assertEquals("name",savedUser.getFirstName());
        Assertions.assertEquals("lastName",savedUser.getLastName());
        Assertions.assertEquals("password",savedUser.getPassword());

    }

    @Test
    public void UserRepository_FindAll_ReturnMoreThenOneUser(){
        User user = User.builder()
                .userName("username")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .build();

        User user2 = User.builder()
                .userName("username2")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .build();

        userRepository.save(user);
        userRepository.save(user2);

        List<User> users = userRepository.findAll();

        Assertions.assertNotNull(users);
        Assertions.assertEquals(users.size(),2);
    }

    @Test
    public void UserRepository_FindById_ReturnOneUser(){
        User user = User.builder()
                .userName("username")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .build();

        userRepository.save(user);

        User returnedUser = userRepository.findById(user.getUserId()).get();

        Assertions.assertNotNull(returnedUser);
        Assertions.assertEquals("username",returnedUser.getUsername());
        Assertions.assertEquals("name",returnedUser.getFirstName());
        Assertions.assertEquals("lastName",returnedUser.getLastName());
    }

    @Test
    public void UserRepository_existsByUserName_ReturnTrue(){
        User user = User.builder()
                .userName("username")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .build();

        userRepository.save(user);

        boolean isExsist = userRepository.existsByUserName("username");

        Assertions.assertEquals(true,isExsist);
    }

    @Test
    public void UserRepository_findUserByUserName_ReturnOneUser(){
        User user = User.builder()
                .userName("username")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .build();

        userRepository.save(user);

        User returnedUser = userRepository.findUserByUserName(user.getUsername()).get();

        Assertions.assertNotNull(returnedUser);
        Assertions.assertEquals("username",returnedUser.getUsername());
        Assertions.assertEquals("name",returnedUser.getFirstName());
        Assertions.assertEquals("lastName",returnedUser.getLastName());
    }
}
