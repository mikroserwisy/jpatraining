package pl.training.jpa.trainings.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Set;

@EqualsAndHashCode(of = "id")
@Data
@Entity(name = "Person")
public class PersonEntity {

    @Id
    private String id;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @CollectionTable(name = "Email", joinColumns = @JoinColumn(name = "PERSON_ID"))
    @MapKeyColumn(name = "ID")
    @Column(name = "VALUE", unique = true)
    @ElementCollection
    private Set<String> emails;

}
