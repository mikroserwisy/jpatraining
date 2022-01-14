package pl.training.jpa.payments;

import lombok.extern.java.Log;
import pl.training.jpa.commons.LocalMoney;
import pl.training.jpa.commons.TransactionTemplate;
import pl.training.jpa.payments.adapters.JpaPaymentsRepositoryAdapter;
import pl.training.jpa.payments.adapters.PaymentsMapper;
import pl.training.jpa.payments.repository.JpaPaymentsRepository;
import pl.training.jpa.payments.repository.PaymentEntity;
import pl.training.jpa.payments.service.LocalPaymentsService;
import pl.training.jpa.payments.service.SystemTimeProvider;

import javax.persistence.Persistence;
import java.util.Date;

import static org.mapstruct.factory.Mappers.getMapper;

@Log
public class Application {

    public static void main(String[] args) {
        var entityManagerFactory = Persistence.createEntityManagerFactory("training");
        var entityManager = entityManagerFactory.createEntityManager();
        var transactionTemplate = new TransactionTemplate(entityManager);
        var paymentsRepository = new JpaPaymentsRepository(transactionTemplate);
        var paymentsRepositoryAdapter = new JpaPaymentsRepositoryAdapter(getMapper(PaymentsMapper.class), paymentsRepository);
        var timeProvider = new SystemTimeProvider();
        var paymentsService = new LocalPaymentsService(paymentsRepositoryAdapter, timeProvider);
        //--------------------------
        paymentsService.process(LocalMoney.of(1_000));
    }

}
