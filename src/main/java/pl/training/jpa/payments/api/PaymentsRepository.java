package pl.training.jpa.payments.api;

import pl.training.jpa.payments.service.Payment;

import java.util.Optional;

public interface PaymentsRepository {

    Payment save(Payment payment);

    Optional<Payment> getById(Long id);

}
