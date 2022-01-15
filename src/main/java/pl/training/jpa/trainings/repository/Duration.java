package pl.training.jpa.trainings.repository;

import javax.persistence.Embeddable;

@Embeddable
public class Duration {

    private Long value;
    private DurationUnit unit;

}
