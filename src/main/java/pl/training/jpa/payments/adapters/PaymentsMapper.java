package pl.training.jpa.payments.adapters;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.training.jpa.commons.FastMoneyMapper;
import pl.training.jpa.payments.repository.PaymentEntity;
import pl.training.jpa.payments.service.Payment;

@Mapper(uses = FastMoneyMapper.class)
public interface PaymentsMapper {

    @Mapping(source = "status", target = "state")
    PaymentEntity toEntity(Payment payment);

    @InheritInverseConfiguration
    Payment toDomain(PaymentEntity entity);

}
