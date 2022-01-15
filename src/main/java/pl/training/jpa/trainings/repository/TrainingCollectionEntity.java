package pl.training.jpa.trainings.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;

@EqualsAndHashCode(of = "id")
@Data
@Entity(name = "TrainingCollection")
public class TrainingCollectionEntity {

    @Id
    private String id;
    @Column(unique = true)
    private String name;
    @Column(length = 4096)
    private String description;
    @ManyToMany
    private List<TrainingEntity> trainings;

}
