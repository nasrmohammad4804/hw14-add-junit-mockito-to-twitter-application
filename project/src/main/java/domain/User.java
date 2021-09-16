package domain;

import base.domain.BaseEntity;
import domain.embeddable.Profile;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "unique_column", columnNames = {User.USER_NAME})})
@Getter
@Setter
@EqualsAndHashCode(of = {"userName", "password", "birthDay", "profile", "twits"}, callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity<Long> {

    public final static String USER_NAME = "user_name";

    @Column(name = USER_NAME, nullable = false)
    private String userName;

    private String password;
    private LocalDate birthDay;

    @Embedded
    private Profile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Twit> twits = new ArrayList<>();


    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", birthDay=" + birthDay +
                ", profile -> " + profile +
                '}';

    }

}

