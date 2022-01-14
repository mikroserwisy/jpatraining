package pl.training.jpa.payments.service;

import java.time.Instant;

public class SystemTimeProvider implements TimeProvider {

    @Override
    public Instant getTimestamp() {
        return Instant.now();
    }

}
