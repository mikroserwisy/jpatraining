package pl.training.jpa;

import javax.persistence.*;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.javamoney.moneta.FastMoney;

@Table(name = "PAYMENTS")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    private String id;
    @Convert(converter = FastMoneyConverter.class)
    private FastMoney value;
    //@Temporal(TemporalType.TIMESTAMP)
    //private Date timestamp;
    private Instant timestamp;
    @CollectionTable(name = "PAYMENTS_PROPERTIES", joinColumns = @JoinColumn(name = "PAYMENT_ID"))
    @MapKeyColumn(name = "KEY", length = 50)
    @Column(name = "VALUE", length = 100)
    @ElementCollection
    private Map<String, String> properties;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    @Transient
    private UUID transactionId;

}
