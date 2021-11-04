package pl.training.jpa;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.Objects;

@Entity
@Data
public class Comment implements Identifiable<Long> {

    @GeneratedValue
    @Id
    private Long id;
    @Lob
    private String value;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        var comment = (Comment) other;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
