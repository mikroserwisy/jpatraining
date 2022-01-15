package pl.training.jpa.trainings;

import lombok.extern.java.Log;
import pl.training.jpa.commons.TransactionTemplate;
import pl.training.jpa.trainings.repository.*;

import javax.persistence.Persistence;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Log
public class Application {

    public static void main(String[] args) {
        var entityManagerFactory = Persistence.createEntityManagerFactory("training");
        new TransactionTemplate(entityManagerFactory.createEntityManager()).withTransaction(entityManager -> {
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

            System.out.println("##############################################################################################");
            return null;
        });


        var result = new TransactionTemplate(entityManagerFactory.createEntityManager()).withTransaction(entityManager -> {

            // zwróć listę szkoleń, zapewnij stronicowanie
            var pageNumber = 0;
            var pageSize = 10;
            var query1 =  entityManager.createQuery("select t from Training t", TrainingEntity.class)
                    .setHint("javax.persistence.fetchgraph",  entityManager.getEntityGraph(TrainingEntity.EAGER))
                    .setFirstResult(pageNumber * pageSize)
                    .setMaxResults(pageSize);

            // zwróć listę kodów i tytułów (warianty: przez pola, przez wyrażenie konstruktorowe)
            var query2 =  entityManager.createQuery("select t.code, t.title from Training t");
            var query3 =  entityManager.createQuery("select new pl.training.jpa.trainings.repository.TrainingView(t.code, t.title) from Training t", TrainingView.class);


            // zwróć listę szkoleń posiadających tagi: java i oop, posortowaną po kodach, użyj złączenia


            // zwróć listę szkoleń trwających od 10 do 15 godzin


            // zwróć listę szkoleń o poziomie trudności BASIC i ADVANCED, użyj operatora IN


            // zwróć nazwiska autorów i liczbę ich szkoleń, weź pod uwagę autorów, którzy stworzyli min. 2 szkolenia



            // ----------------------------------------------------

            return query1.getResultList();
        });

        log.info("#####################################################################################################");
        result.stream().map(TrainingEntity::getTitle)
                .forEach(log::info);

    }

    public static String getNextId() {
        return UUID.randomUUID().toString();
    }

}
