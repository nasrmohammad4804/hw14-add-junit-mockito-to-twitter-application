package repsostory.impl;

import domain.User;
import domain.embeddable.Profile;
import org.junit.jupiter.api.*;
import util.HibernateUtil;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled()
class UserRepositoryImplTest {

    private static final User user = new User.UserBuilder("alk1507", "123456").
            getBirthDay(LocalDate.parse("1990-05-23")).getProfile(
            new Profile("ali", "akbari", "1287862345")).build();

    private static final EntityManager entityManager = HibernateUtil.getEntityManagerTestFactory()
            .createEntityManager();

    private static final UserRepositoryImpl userRepository = new UserRepositoryImpl(entityManager);

    @BeforeAll
    static void firstInitializeUser() {

        entityManager.getTransaction().begin();
        userRepository.save(user);
        entityManager.getTransaction().commit();

        System.out.println("saved !!!");

    }


    @Test
    void delete() {
        userRepository.delete(user);
        assertTrue(user.getDeleted());

    }

    @Test
    @DisplayName(value = "test for find by userName")
    void findByUserName() {

        assertEquals(user, userRepository.findByUserName("alk1507").get());

    }

    @Test
    void findByUserNameForRecovery() {
        userRepository.delete(user);
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