package pl.training.jpa.payments.repository;

import lombok.*;
import lombok.extern.java.Log;
import org.javamoney.moneta.FastMoney;
import pl.training.jpa.commons.FastMoneyConverter;
import pl.training.jpa.commons.Identifiable;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.LAZY;


@NamedEntityGraph(name = PaymentEntity.WITH_PROPERTIES,
        attributeNodes = {
                @NamedAttributeNode("properties")
        }
)
@NamedQuery(name = PaymentEntity.GET_ALL, query = "select p from PaymentEntity p join fetch p.properties pp")
@ExcludeDefaultListeners
//@ExcludeSuperclassListeners
//@EntityListeners(PaymentListener.class)
@Entity
@Log
@Builder
//@EqualsAndHashCode(exclude = "id")
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEntity implements Identifiable<Long> {

    public static final String GET_ALL = "paymentsGetAll";
    public static final String WITH_PROPERTIES = "paymentsWithProperties";

    @GeneratedValue
    @Id
    private Long id;
    @Convert(converter = FastMoneyConverter.class)
    private FastMoney value;
    private Date timestamp;
    private String state;
    @JoinColumn(name = "PAYMENT_ID")
    @OneToMany(cascade = PERSIST, fetch = LAZY)
    private List<PropertyEntity> properties;
    @Version
    private Long version;

    /*@PrePersist
    public void prePersist() {
        log.info("prePersist");
    }

    @PostPersist
    public void postPersist() {
        log.info("postPersist");
    }

    @PreRemove
    public void preRemove() {
        log.info("preRemove");
    }

    @PostRemove
    public void postRemove() {
        log.info("postRemove");
    }

    @PreUpdate
    public void preUpdate() {
        log.info("preUpdate");
    }

    @PostUpdate
    public void postUpdate() {
        log.info("postUpdate");
    }

    @PostLoad
    public void postLoad() {
        log.info("postLoad");
    }*/

    @Override
    public boolean equals(Object otherPayment) {
        if (this == otherPayment) {
            return true;
        }
        if (otherPayment == null || getClass() != otherPayment.getClass()) {
            return false;
        }
        var payment = (PaymentEntity) otherPayment;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return 13; //Objects.hash(getClass().getName());
    }

}
