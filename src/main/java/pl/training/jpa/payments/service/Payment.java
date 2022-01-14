package pl.training.jpa.payments.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javamoney.moneta.FastMoney;

import java.time.Instant;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    private Long id;
    private FastMoney value;
    private Instant timestamp;
    private PaymentStatus status;

}
