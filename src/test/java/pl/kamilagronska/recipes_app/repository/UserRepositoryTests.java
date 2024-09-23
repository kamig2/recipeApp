package pl.kamilagronska.recipes_app.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.kamilagronska.recipes_app.entity.User;

import java.util.List;
import java.util.Optional;

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
        Assertions.assertEquals(2,users.size());
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

        boolean isExist = userRepository.existsByUserName("username");

        Assertions.assertTrue(isExist);
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

    @Test
    public void UserRepository_UpdateUser_ReturnUser(){
        User user = User.builder()
                .userName("username")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .build();

        User savedUser = userRepository.save(user);
        savedUser.setUserName("newUsername");
        savedUser.setFirstName("newName");
        savedUser.setLastName("newLastName");
        savedUser.setPassword("newPassword");

        User updatedUser = userRepository.save(savedUser);

        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals("newUsername",updatedUser.getUsername());
        Assertions.assertEquals("newName",updatedUser.getFirstName());
        Assertions.assertEquals("newLastName",updatedUser.getLastName());

    }

    @Test
    public void UserRepository_DeleteUser_ReturnUserIsEmpty(){
        User user = User.builder()
                .userName("username")
                .firstName("name")
                .lastName("lastName")
                .password("password")
                .build();

        userRepository.save(user);

        userRepository.deleteById(user.getUserId());

        Optional<User> deletedUser = userRepository.findById(user.getUserId());

        Assertions.assertTrue(deletedUser.isEmpty());

    }
}
