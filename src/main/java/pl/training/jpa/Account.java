package pl.training.jpa;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(exclude = "id")
public class Account {

    @Id
    private Long id;
    @NonNull
    private String number;
    @NonNull
    private Long balance;

}
