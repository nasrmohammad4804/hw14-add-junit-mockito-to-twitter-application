package domain;

import base.domain.BaseEntity;
import domain.embeddable.TwitLike;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@NamedNativeQuery(name = "countOfAllTwitsOfUser",query = "SELECT count(*) FROM twit as t where t.twit_comment is null" +
        " and t.user_id=:myId and t.isDeleted=false")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Twit extends BaseEntity<Long> {

    public static final String TABLE_NAME_OF_ALL_LIKES_TWIT = "twit_like";
    public static final String TABLE_NAME_FOR_COMMENTS = "twit_comment";

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = TABLE_NAME_FOR_COMMENTS)
    Set<Twit> comments = new HashSet<>();

    @Column(length = 280)
    private String context;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    @ElementCollection
    @JoinTable(name = TABLE_NAME_OF_ALL_LIKES_TWIT)
    private Set<TwitLike> twitLikes = new LinkedHashSet<>();


    public Twit(String context, User user) {
        this.context = context;
        this.user = user;
    }

    public void print() {

        System.out.println("-".repeat(80));
        System.out.printf("|%-35s!!twit!!%-35s %s\n"," "," ","|");
        System.out.printf("%-80s%s\n","|"+"twit id :"+getId()+"   "+"owner of twit :"+user.getUserName(),"|");
        System.out.printf("%-80s%s\n" ,"|context of twit : ","|");
        System.out.printf("%-80s%s\n","|"+context,"|");
        System.out.println("-".repeat(80));
        getComments().stream().filter(x -> x.getDeleted().equals(Boolean.FALSE)).forEach(comment -> {
            System.out.println("comment id :" + comment.getId() + "  " + " writed by :"
                    + comment.getUser().getUserName()
                    + "\n text of comment : \n" + comment.getContext());
            System.out.println("- - ".repeat(40));
        });

    }
}
