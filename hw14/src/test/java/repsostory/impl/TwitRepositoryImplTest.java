package repsostory.impl;

import domain.Twit;
import domain.User;
import domain.embeddable.Profile;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.math.BigInteger;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TwitRepositoryImplTest {

    private static EntityManager entityManager;
    private static TwitRepositoryImpl twitRepository;
    private static User user;
    private static Twit twit1;
    private static Twit twit2;

    @BeforeAll
    public static void start() {

        entityManager = Persistence.createEntityManagerFactory("myTest")
                .createEntityManager();
        twitRepository = new TwitRepositoryImpl(entityManager);

        user = new User.UserBuilder("mahsa5671", "13994359").
                getBirthDay(LocalDate.parse("2001-02-13")).getProfile(
                new Profile("mahsa", "noori", "1273214997")).build();

        twit1 = new Twit("i decide make cake", user);
        twit2 = new Twit("i went to travel russia", user);
        twitRepository.save(twit1);
        twitRepository.save(twit2);
    }

    @AfterAll
    public static void end() {

        entityManager.close();
    }

    @Test
    void delete() {

        twitRepository.delete(twit1);
        assertTrue(twit1.getDeleted());

        twit1.setDeleted(false);
    }


    @Test
    void countOfTwitsOfUser() {

        assertEquals(BigInteger.valueOf(2L), twitRepository.countOfTwitsOfUser(user.getId()));
        assertEquals(BigInteger.valueOf(0), twitRepository.countOfTwitsOfUser(100L));
        assertEquals(BigInteger.valueOf(0), twitRepository.countOfTwitsOfUser(-4L));
        assertEquals(BigInteger.valueOf(0), twitRepository.countOfTwitsOfUser(0L));
    }

    @Test
    void findById() {
        assertEquals(twit1, twitRepository.findById(1L).get());
        assertEquals(twit2, twitRepository.findById(2L).get());
    }

}