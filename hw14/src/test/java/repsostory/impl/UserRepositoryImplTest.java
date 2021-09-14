package repsostory.impl;

import domain.User;
import domain.embeddable.Profile;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRepositoryImplTest {

    private static final User user = new User.UserBuilder("ali_akbari", "123456").
            getBirthDay(LocalDate.parse("1990-05-23")).getProfile(
            new Profile("ali", "akbari", "1287862345")).build();

    private static final EntityManager entityManager = Persistence.createEntityManagerFactory("myTest")
            .createEntityManager();
    private static final UserRepositoryImpl userRepository = new UserRepositoryImpl(entityManager);

    @BeforeAll
    static void firstInitializeUser() {
        userRepository.save(user);
    }

    @Test
    void delete() {
        userRepository.delete(user);
        assertTrue(user.getDeleted());
        backToUndelete();
    }

    @Test
    void findByUserName() {

        assertEquals(user, userRepository.findByUserName("ali_akbari").get());
    }

    @Test
    void findByUserNameForRecovery() {
        userRepository.delete(user);
        assertEquals(user, userRepository.findByUserNameForRecovery("ali_akbari", "123456").get());

        backToUndelete();
    }

    public void backToUndelete() {
        user.setDeleted(false);
        userRepository.update(user);

    }

    @AfterAll
    public static void end() {

        entityManager.close();
    }
}