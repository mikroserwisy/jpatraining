package pl.training.jpa.trainings.repository;

import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
public class Duration {

    private Long value;
    private DurationUnit unit;

}
