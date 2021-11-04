package pl.training.jpa;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class Fixtures {

    public static final Long ACCOUNT_ID = 1L;

    public static Account account() {
        var account = new Account("000000000001", 1_000L);
        account.setId(ACCOUNT_ID);
        return account;
    }

    public static Payment payment(Long value) {
        return Payment.builder()
                .id(UUID.randomUUID().toString())
                .status(PaymentStatus.NOT_CONFIRMED)
                .timestamp(Instant.now())
                .value(LocalMoney.of(value))
                .properties(Map.of("cardNumber", "1234567890"))
                .build();
    }

}
