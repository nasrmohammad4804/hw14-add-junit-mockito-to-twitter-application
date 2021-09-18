package service.impl;

import base.service.impl.BaseServiceImpl;
import domain.Twit;
import domain.User;
import domain.embeddable.Profile;
import domain.embeddable.TwitLike;
import domain.enumerated.LikeState;
import repsostory.impl.UserRepositoryImpl;
import service.UserService;
import util.ApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserServiceImpl extends BaseServiceImpl<User, Long, UserRepositoryImpl>
        implements UserService {


    private TwitServiceImpl twitService;

    public UserServiceImpl(UserRepositoryImpl repository) {
        super(repository);
    }

    public void initialize() {

        twitService = ApplicationContext.getApplicationContext().getTwitService();
    }
    public void setTwitServiceForTest(TwitServiceImpl twitService){
        this.twitService=twitService;
    }

    @Override
    public void delete(User element) {
        repository.delete(element);
    }

    @Override
    public Optional<User> register() {

        Scanner scanner = new Scanner(System.in);

        Optional<User> optional = Optional.empty();
        System.out.println("enter userName ..");
        String userName = scanner.nextLine();

        if (repository.findByUserName(userName).isPresent()) {
            System.out.println("this userName already exist !!!");
            return optional;
        }

        System.out.println("enter password");
        String password = scanner.nextLine();

        System.out.println("enter firstName");
        String name = scanner.nextLine();
        System.out.println("enter lastName");
        String family = scanner.nextLine();

        System.out.println("enter birthDay");
        LocalDate birthDay = LocalDate.parse(
                scanner.nextLine()
        );

        System.out.println("enter nationalCode");
        String nationalCode = scanner.nextLine();

        User user = User.builder().userName(userName).password(password).birthDay(birthDay)
                .profile(new Profile(name, family, nationalCode)).build();

        super.save(user);
        entityManager.refresh(user);

        return Optional.of(user);

        /*
         * here is best of refresh  case because user dont enter id
         * but we retrive user and using another in program but id is null
         * but if  use refresh get user object from db  and dont problem for return
         * */
    }

    @Override
    public Optional<User> login() {
        Scanner scanner = new Scanner(System.in);

        Optional<User> optional = Optional.empty();
        System.out.println("enter userName");

        String userName = scanner.nextLine();

        Optional<User> userOptional = repository.findByUserName(userName);

        if (userOptional.isEmpty()) {

            System.out.println("userName is wrong or delete account ");
            return optional;
        }

        System.out.println("enter your password");
        String password = scanner.nextLine();

        if (!userOptional.get().getPassword().equals(password)) {
            System.out.println("password is wrong");
            return optional;

        }
        return userOptional;
    }

    @Override
    public void addTwit(User user) {

        Scanner scanner = new Scanner(System.in);

        entityManager.getTransaction().begin();
        System.out.println("enter your context of twit");
        String context = scanner.nextLine();

        Twit twit = new Twit(context, user);
        user.getTwits().add(twit);


        entityManager.getTransaction().commit();

    }

    @Override
    public void changeProfile(User user) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("1.change base name");
        System.out.println("2.change base family");
        System.out.println("3.change base password");

        switch (Integer.parseInt(scanner.nextLine())) {

            case 1 -> {
                System.out.println("enter newName");
                String name = scanner.nextLine();
                user.getProfile().setFirstName(name);
                super.update(user);

            }
            case 2 -> {
                System.out.println("enter newLastName");
                String family = scanner.nextLine();
                user.getProfile().setLastName(family);
                super.update(user);
            }
            case 3 -> {
                System.out.println("enter old password ");
                String oldPassword = scanner.nextLine();

                if (!user.getPassword().equals(oldPassword)) {
                    System.out.println("password is wrong ... try again");
                    changeProfile(user);
                }
                System.out.println("enter newPassword");
                String newPassword = scanner.nextLine();
                user.setPassword(newPassword);
                super.update(user);
            }
            default -> System.out.println("input not valid");
        }
    }

    @Override
    public Optional<User> findByUserName(String userName) {
        return repository.findByUserName(userName);
    }

    @Override
    public void recovery() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("enter userName for recovery");
        String userName = scanner.nextLine();

        System.out.println("enter password for recovery");
        String password = scanner.nextLine();

        Optional<User> userOptional = repository.findByUserNameForRecovery(userName, password);

        if (userOptional.isEmpty()) {
            System.out.println("password or userName is wrong )) ");
            return;
        }

        User user = userOptional.get();
        user.setDeleted(false);
        super.update(user);
    }

    public void showUserTwits(User user, User anotherUser) {


        List<Twit> twits = twitService.findAllTwitOfUser(user);

        twits.forEach(twit -> {

            long commentNumber = twit.getComments().stream().filter(x -> x.getDeleted().equals(Boolean.FALSE)).count();

            System.out.print("id : " + twit.getId() + "    comment number is : " + commentNumber + "    ");
            Optional<TwitLike> twitLikeOptional = twit.getTwitLikes().
                    stream().filter(x -> x.getState().equals(LikeState.FOLLOW) &&
                    x.getUser().equals(anotherUser)).findAny();

            long likeNumber = twit.getTwitLikes().stream().
                    filter(x -> x.getState().equals(LikeState.FOLLOW)).count();
            twitLikeOptional.ifPresent(number -> {
                        System.out.println("like number    ♥" + " " + likeNumber);
                    }
            );
            if (twitLikeOptional.isEmpty())
                System.out.println("like number    ♡" + " " + likeNumber);

            System.out.println(twit.getContext());
            System.out.println("*".repeat(90));
        });

    }

    public void showSpecificTwit(User user) {


        System.out.println("enter id of twits");
        Long id = ApplicationContext.getApplicationContext().getScannerForInteger().nextLong();
        Optional<Twit> optional = user.getTwits().stream().filter(x -> x.getId().equals(id)
                && x.getDeleted().equals(Boolean.FALSE)).findFirst();

        if (optional.isEmpty()) {
            System.out.println(" dont have any twit with id or deleted");
            return;
        }
        optional.ifPresent(twit -> {

            if (twit.getComments().isEmpty())
                System.out.println("dont have any comment for twit ");

            else twit.print();

            System.out.println("-".repeat(90));
        });

    }

    public void showAllTwitsOfAnotherUser(User user, User anotherUser) {

        showUserTwits(user, anotherUser);

        System.out.println("if you want to select  twit enter yes ");
        String result = ApplicationContext.getApplicationContext().getScannerForString().nextLine();

        if (!result.equals("yes")) {
            System.out.println("nice to meet you");
            return;
        }
        List<Twit> twitList = twitService.findAllTwitOfUser(user);
        if (twitList.isEmpty()) {
            System.out.println("user with userName :" + user.getUserName() + " or dont have any twit or deleted\n");
            return;
        }

        System.out.println("enter twit id");
        Long id = ApplicationContext.getApplicationContext().getScannerForInteger().nextLong();

        Optional<Twit> optionalTwit = twitList.stream().filter(x -> x.getId().equals(id)).findAny();


        if (optionalTwit.isEmpty()) {
            System.out.println("this id for twit not exists\n");
            return;
        }
        operationOnAnotherTwit(user, anotherUser, optionalTwit.get());

    }

    private void operationOnAnotherTwit(User user, User anotherUser, Twit twit) {
        Scanner scanner=new Scanner(System.in);
        System.out.println("1.show comment of  twit");
        System.out.println("2.like or dislike twit");
        System.out.println("3.add comment to this twit");
        System.out.println("4.update comment");
        System.out.println("5.delete comment");
        System.out.println("6. back ");
        switch (scanner.nextInt()) {

            case 1 -> {
                if (twit.getComments().isEmpty())
                    System.out.println("dont confirm any comment for this twit witch writer is :" + user.getUserName());

                else twit.print();
                operationOnAnotherTwit(user, anotherUser, twit);
            }
            case 2 -> {

                twitService.setLikeStateOfTwit(anotherUser, twit);
                operationOnAnotherTwit(user, anotherUser, twit);
            }

            case 3 -> {
                twitService.addComments(twit, anotherUser);
                operationOnAnotherTwit(user, anotherUser, twit);
            }

            case 4 -> {
                twitService.updateComment(anotherUser, twit);
                operationOnAnotherTwit(user, anotherUser, twit);
            }
            case 5 -> {
                twitService.deleteComment(anotherUser, twit);
                operationOnAnotherTwit(user, anotherUser, twit);
            }
            case 6 -> System.out.println("back to home of " + anotherUser.getUserName());

        }
    }

}