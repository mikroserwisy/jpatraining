package pl.training.jpa.payments.service;

import lombok.RequiredArgsConstructor;
import org.javamoney.moneta.FastMoney;
import pl.training.jpa.payments.api.PaymentsRepository;
import pl.training.jpa.payments.api.PaymentsService;

import static pl.training.jpa.payments.service.PaymentStatus.NOT_CONFIRMED;

@RequiredArgsConstructor
public class LocalPaymentsService implements PaymentsService {

    private final PaymentsRepository paymentsRepository;
    private final TimeProvider timeProvider;

    @Override
    public void process(FastMoney value) {
        var payment = Payment.builder()
                .value(value)
                .timestamp(timeProvider.getTimestamp())
                .status(NOT_CONFIRMED)
                .build();
        paymentsRepository.save(payment);
    }

}
