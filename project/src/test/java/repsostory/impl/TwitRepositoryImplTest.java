package repsostory.impl;

import domain.Twit;
import domain.User;
import domain.embeddable.Profile;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assumptions.*;

import service.impl.TwitServiceImpl;
import service.impl.UserServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TwitRepositoryImplTest {

    private static EntityManager entityManager;
    private static TwitRepositoryImpl twitRepository;
    private static User user;
    private static Twit twit1;
    private static Twit twit2;
    private static TwitServiceImpl twitService;
    private static UserServiceImpl userService;

    @BeforeAll
    public static void start() {

        entityManager = Persistence.createEntityManagerFactory("myTest")
                .createEntityManager();
        twitRepository = new TwitRepositoryImpl(entityManager);

        userService = new UserServiceImpl(new UserRepositoryImpl(entityManager));

        twitService = new TwitServiceImpl(twitRepository);

        user = new User.UserBuilder("mahsa5671", "13994359").
                getBirthDay(LocalDate.parse("2001-02-13")).getProfile(
                new Profile("mahsa", "noori", "1273214997")).build();


        twit1 = new Twit("i decide make cake", user);
        twit2 = new Twit("i went to travel russia", user);

        twit1.setUser(user);
        twit2.setUser(user);
        user.getTwits().add(twit1);
        user.getTwits().add(twit2);

        userService.save(user);
        entityManager.refresh(user);
    }

    @AfterAll
    public static void end() {

        entityManager.getTransaction().begin();
        entityManager.remove(twit1);
        entityManager.remove(twit2);
        entityManager.remove(user);
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    @Test
    void delete() {

        twitService.delete(twit1);
        assertTrue(twit1.getDeleted());

    }

    @Test
    void countOfTwitsOfUser() {

        assertAll("twitCount",
                () -> assertEquals(BigInteger.valueOf(2L), twitRepository.countOfTwitsOfUser(user.getId())),
                () -> assertEquals(BigInteger.valueOf(0), twitRepository.countOfTwitsOfUser(-4L)),
                () -> assertEquals(BigInteger.valueOf(0), twitRepository.countOfTwitsOfUser(0L)));

    }

    @Test
    void findById() {

        assertAll("findByTwitById",
                () -> assertEquals(Optional.empty(), twitRepository.findById(0L)),
                () -> assertEquals(Optional.empty(), twitRepository.findById(-2L)));


    }
}