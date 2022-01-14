package pl.training.jpa.payments.adapters;

import lombok.RequiredArgsConstructor;
import pl.training.jpa.payments.api.PaymentsRepository;
import pl.training.jpa.payments.repository.JpaPaymentsRepository;
import pl.training.jpa.payments.service.Payment;

import java.util.Optional;

@RequiredArgsConstructor
public class JpaPaymentsRepositoryAdapter implements PaymentsRepository {

    private final PaymentsMapper mapper;
    private final JpaPaymentsRepository paymentsRepository;

    @Override
    public Payment save(Payment payment) {
        var entity = mapper.toEntity(payment);
        var savedEntity = paymentsRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Payment> getById(Long id) {
        return  paymentsRepository.getById(id)
                .map(mapper::toDomain);
    }

}
