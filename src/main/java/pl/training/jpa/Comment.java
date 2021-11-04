package pl.training.jpa;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
@Data
public class Comment implements Identifiable<Long> {

    @GeneratedValue
    @Id
    private Long id;
    @Lob
    private String value;

}
