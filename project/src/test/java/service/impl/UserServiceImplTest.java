package service.impl;

import domain.Twit;
import domain.User;
import domain.embeddable.Profile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import repsostory.impl.TwitRepositoryImpl;
import repsostory.impl.UserRepositoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {


    private static UserServiceImpl userService;
    private static UserRepositoryImpl userRepository;
    private static TwitRepositoryImpl twitRepository; //twitRepository
    private static TwitServiceImpl twitService;

    private static User user;

    @BeforeAll
    public static void setup() {
        userRepository = mock(UserRepositoryImpl.class, Answers.RETURNS_DEEP_STUBS);
        EntityManager entityManager = mock(EntityManager.class);
        EntityTransaction entityTransaction = mock(EntityTransaction.class);
        when(userRepository.getEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(entityTransaction);
        doNothing().when(entityTransaction).begin();
        doNothing().when(entityTransaction).commit();


        twitRepository = mock(TwitRepositoryImpl.class);
        EntityManager entityManagerTwit = mock(EntityManager.class);
        EntityTransaction entityTransactionTwit = mock(EntityTransaction.class);
        when(twitRepository.getEntityManager()).thenReturn(entityManagerTwit);
        when(entityManagerTwit.getTransaction()).thenReturn(entityTransactionTwit);
        doNothing().when(entityTransactionTwit).begin();
        doNothing().when(entityTransactionTwit).commit();


        twitService = new TwitServiceImpl(twitRepository);
        userService = new UserServiceImpl(userRepository);

        userService.setTwitServiceForTest(twitService);
//        userService.initialize();

        user = User.builder().userName("fk1507").password("1111").birthDay(LocalDate.parse("1378-04-27")).
                profile(new Profile("zahra", "akrami", "13125246")).build();

        user.setTwits(new ArrayList<>());


    }

    @Test
    void register() {
        String data = """
                ali1234
                1234ali
                ali
                akbari
                1378-04-27
                1287678235
                """;
        Optional<User> optionalUser = Optional.empty();
        when(userRepository.findByUserName(any())).thenReturn(optionalUser);

        InputStream input = System.in;
        System.setIn(new ByteArrayInputStream(data.getBytes()));

        Optional<User> optional = userService.register();
        System.setIn(input);

        assertFalse(optional.isEmpty());
        assertAll("check for register",
                () -> assertNotNull(optional.get().getPassword()),
                () -> assertNotNull(optional.get().getUserName()));
    }


    @Test
    void login() {
        String data = """
                fk1507
                1111
                """;
        InputStream input = System.in;
        System.setIn(new ByteArrayInputStream(data.getBytes()));

        when(userRepository.findByUserName(any())).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userService.login().get());

        System.setIn(input);
    }

    @Test
    void addTwit() {
        String data = """
                i like java spring
                """;
        InputStream input = System.in;
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        userService.addTwit(user);
        System.setIn(input);

        assertFalse(user.getTwits().isEmpty());
    }

    @Test
    void changeProfile() {
        String data = """
                1
                javad
                """;

        InputStream input = System.in;
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        userService.changeProfile(user); //test for firstName

        System.setIn(input);
        assertEquals("javad", user.getProfile().getFirstName());
    }

    @Test
    void recovery() {
        String data = """
                fk1507
                1111
                """;
        InputStream input = System.in;

        user.setDeleted(true);
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        when(userRepository.findByUserNameForRecovery(any(), any())).thenReturn(Optional.empty());
        userService.recovery();
        assertTrue(user.getDeleted());

        when(userRepository.findByUserNameForRecovery(any(), any())).thenReturn(Optional.of(user));
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        userService.recovery();
        assertFalse(user.getDeleted());

        System.setIn(input);

    }

    @Test
    public void Operation() {
        //test private method with reflection
        addTwit();  //only test for liking twit

        String operationLike = "2";
        InputStream input = System.in;
        System.setIn(new ByteArrayInputStream(operationLike.getBytes()));
        try {
            Method method = userService.getClass().
                    getDeclaredMethod("operationOnAnotherTwit", User.class, User.class, Twit.class);
            method.setAccessible(true);

            assertTrue(user.getTwits().get(0).getTwitLikes().isEmpty());

            assertThrows(InvocationTargetException.class, () -> {
                method.invoke(userService, user, user, user.getTwits().get(0));
            });

            assertFalse(user.getTwits().get(0).getTwitLikes().isEmpty());
            System.setIn(input);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}