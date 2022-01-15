package pl.training.jpa.trainings.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(of = "id")
@Data
@Entity(name = "Training")
public class TrainingEntity {

    @Id
    private String id;
    @Column(unique = true)
    private String code;
    @Column(unique = true)
    private String title;
    private String slug;
    @ManyToMany
    private List<PersonEntity> authors;
    @ManyToMany
    private Set<TagEntity> tags;
    @Enumerated(EnumType.STRING)
    private TrainingType type;
    @AttributeOverride(name = "value", column = @Column(name = "DURATION"))
    @AttributeOverride(name = "unit", column = @Column(name = "DURATION_UNIT"))
    @Embedded
    private Duration duration;
    @Enumerated(EnumType.ORDINAL)
    private Difficulty difficulty;
    @Column(length = 4096)
    private String description;
    @OneToMany
    private List<ModuleEntity> modules;


    @PrePersist
    public void prePersist() {
        slug = title.toLowerCase().replaceAll("\\s+", "-");
    }


}
