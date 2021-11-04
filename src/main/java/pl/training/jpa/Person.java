package pl.training.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

//@IdClass(PersonId.class)
@Entity
@Getter
@Setter
public class Person {

    @AttributeOverride(name = "lastName", column = @Column(name = "LAST_NAME"))
    @EmbeddedId
    private PersonId personId;

    //@Id
    //private String pesel;
    private String firstName;
    //@Id
    //private String lastName;

}
