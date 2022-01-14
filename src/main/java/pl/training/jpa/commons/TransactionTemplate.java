package pl.training.jpa.commons;

import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.function.Function;

@RequiredArgsConstructor
public class TransactionTemplate {

    private final EntityManager entityManager;

    public <T> T withTransaction(Function<EntityManager, T> task) {
        var transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            T result = task.apply(entityManager);
            transaction.commit();
            return result;
        } catch (RuntimeException exception) {
            transaction.rollback();
            throw exception;
        }
    }

}
