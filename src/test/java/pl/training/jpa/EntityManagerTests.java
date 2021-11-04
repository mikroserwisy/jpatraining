package pl.training.jpa;

import org.junit.jupiter.api.Test;
import pl.training.jpa.commons.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pl.training.jpa.Fixtures.*;

public class EntityManagerTests extends BaseTest {

    @Test
    void should_persist_entity() {
        var account = account();
        withTransaction(entityManager -> entityManager.persist(account));
        withTransaction(entityManager -> assertEquals(account, entityManager.find(Account.class, ACCOUNT_ID)));
    }

    @Test
    void should_persist_complex_entity() {
        var payment = payment(1_000L);
        withTransaction(entityManager -> entityManager.persist(payment));
    }

}
