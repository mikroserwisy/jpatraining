package pl.training.jpa.payments.repository;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@EqualsAndHashCode(exclude = "id")
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class PropertyEntity {

    @GeneratedValue
    @Id
    private Long id;
    @NonNull
    private String key;
    @NonNull
    private String value;

}
