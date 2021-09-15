package repsostory.impl;

import domain.User;
import domain.embeddable.Profile;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.impl.UserServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryImplTest {

    private static final User user = new User.UserBuilder("alk1507", "123456").
            getBirthDay(LocalDate.parse("1990-05-23")).getProfile(
            new Profile("ali", "akbari", "1287862345")).build();

    private static final EntityManager entityManager = Persistence.createEntityManagerFactory("myTest")
            .createEntityManager();

    private static final UserRepositoryImpl userRepository = new UserRepositoryImpl(entityManager);
    private static final UserServiceImpl userService = new UserServiceImpl(userRepository);

    @BeforeAll
    static void firstInitializeUser() {

        userService.save(user);

        System.out.println("saved !!!");
    }


    @Test
    void delete() {
        userService.delete(user);
        assertTrue(user.getDeleted());

    }

    @Test
    void findByUserName() {

        assertEquals(user, userService.findByUserName("alk1507").get());

    }

    @Test
    void findByUserNameForRecovery() {
        userService.delete(user);
        assertEquals(user, userRepository.findByUserNameForRecovery("alk1507", "123456").get());


    }

    @AfterAll
    public static void end() {

        entityManager.getTransaction().begin();
        entityManager.remove(user);
        entityManager.getTransaction().commit();

        entityManager.close();
    }
}