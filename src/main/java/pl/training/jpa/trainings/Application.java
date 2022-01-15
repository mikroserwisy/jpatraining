package pl.training.jpa.trainings;

import lombok.extern.java.Log;
import pl.training.jpa.commons.TransactionTemplate;
import pl.training.jpa.trainings.repository.*;

import javax.persistence.Persistence;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Log
public class Application {

    public static void main(String[] args) {
        var entityManagerFactory = Persistence.createEntityManagerFactory("training");
        var transactionTemplate = new TransactionTemplate(entityManagerFactory.createEntityManager());
        transactionTemplate.withTransaction(entityManager -> {
            var javaTag = new TagEntity();
            javaTag.setId(getNextId());
            javaTag.setName("java");

            var oopTag = new TagEntity();
            oopTag.setId(getNextId());
            oopTag.setName("oop");

            var firstAuthor = new PersonEntity();
            firstAuthor.setId(getNextId());
            firstAuthor.setFirstName("Jan");
            firstAuthor.setLastName("Kowalski");
            firstAuthor.setEmails(Set.of("jan.kowalski@trainig.pl"));

            var secondAuthor = new PersonEntity();
            secondAuthor.setId(getNextId());
            secondAuthor.setFirstName("Marek");
            secondAuthor.setLastName("Nowak");
            secondAuthor.setEmails(Set.of("marek.nowak@trainig.pl", "mnowak@gmail.com"));

            var firstTrainingDuration = new Duration();
            firstTrainingDuration.setValue(40L);
            firstTrainingDuration.setUnit(DurationUnit.HOURS);

            var firstTraining = new TrainingEntity();
            firstTraining.setId(getNextId());
            firstTraining.setAuthors(List.of(firstAuthor));
            firstTraining.setCode("JPR");
            firstTraining.setDifficulty(Difficulty.BASIC);
            firstTraining.setDescription("Programming in Java");
            firstTraining.setTags(Set.of(oopTag, javaTag));
            firstTraining.setTitle("Programming in Java");
            firstTraining.setType(TrainingType.STATIONARY);
            firstTraining.setDuration(firstTrainingDuration);

            entityManager.persist(javaTag);
            entityManager.persist(oopTag);
            entityManager.persist(firstAuthor);
            entityManager.persist(secondAuthor);
            entityManager.persist(firstTraining);

            // ----------------------------------------------------

            return null;
        });
    }

    public static String getNextId() {
        return UUID.randomUUID().toString();
    }

}
