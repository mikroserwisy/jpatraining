package pl.training.jpa.trainings;

import lombok.extern.java.Log;
import pl.training.jpa.commons.TransactionTemplate;

import javax.persistence.Persistence;

@Log
public class Application {

    public static void main(String[] args) {
        var entityManagerFactory = Persistence.createEntityManagerFactory("training");
        var transactionTemplate = new TransactionTemplate(entityManagerFactory.createEntityManager());
        transactionTemplate.withTransaction(entityManager -> {
            return null;
        });
    }

}
