package pl.training.jpa.trainings.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@EqualsAndHashCode(of = "id")
@Data
@Entity(name = "Tag")
public class TagEntity {

    @Id
    private String id;
    @Column(unique = true)
    private String name;

}
