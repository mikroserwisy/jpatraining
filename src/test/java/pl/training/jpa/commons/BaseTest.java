package pl.training.jpa.commons;

import org.junit.jupiter.api.AfterAll;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.function.Consumer;

public class BaseTest {

    protected final static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("training-hibernate-unit");

    protected void withTransaction(Consumer<EntityManager> task) {
        var entityManager = entityManagerFactory.createEntityManager();
        var transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            task.accept(entityManager);
            transaction.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
            transaction.rollback();
        }
        entityManager.close();
    }

    @AfterAll
    public static void onClose() {
        entityManagerFactory.close();
    }

}
