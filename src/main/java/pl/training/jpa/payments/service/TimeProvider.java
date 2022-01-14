package pl.training.jpa.payments.service;

import java.time.Instant;

public interface TimeProvider {

    Instant getTimestamp();

}
