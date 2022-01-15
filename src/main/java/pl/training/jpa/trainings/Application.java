package pl.training.jpa.trainings;

import lombok.extern.java.Log;
import pl.training.jpa.commons.TransactionTemplate;
import pl.training.jpa.trainings.repository.*;

import javax.persistence.Persistence;
import java.util.*;

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

            var secondTrainingDuration = new Duration();
            secondTrainingDuration.setValue(13L);
            secondTrainingDuration.setUnit(DurationUnit.HOURS);

            var secondTraining = new TrainingEntity();
            secondTraining.setId(getNextId());
            secondTraining.setAuthors(List.of(firstAuthor, secondAuthor));
            secondTraining.setCode("CPR");
            secondTraining.setDifficulty(Difficulty.BASIC);
            secondTraining.setDescription("Programming in C++");
            secondTraining.setTags(Set.of(oopTag));
            secondTraining.setTitle("Programming in C++");
            secondTraining.setType(TrainingType.STATIONARY);
            secondTraining.setDuration(secondTrainingDuration);

            entityManager.persist(javaTag);
            entityManager.persist(oopTag);
            entityManager.persist(firstAuthor);
            entityManager.persist(secondAuthor);
            entityManager.persist(firstTraining);
            entityManager.persist(secondTraining);

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


            var query4 = entityManager.createQuery("select t from Training t join t.tags tag where tag.name in ('java', 'oop') group by t.id having count(t.id) = 2 order by t.code", TrainingEntity.class);

            // zwróć listę szkoleń trwających od 10 do 15 godzin
            var query5 = entityManager.createQuery("select t from Training t where t.duration.unit = 2 and t.duration.value >= :minHours and t.duration.value <= :maxHours", TrainingEntity.class)
                    .setParameter("minHours", 10L)
                    .setParameter("maxHours", 15L);

            // zwróć listę szkoleń o poziomie trudności BASIC lub ADVANCED, użyj operatora IN
            var query6 = entityManager.createQuery("select t from Training t where t.difficulty in (0, 2)", TrainingEntity.class);

            // zwróć nazwiska autorów i liczbę ich szkoleń, weź pod uwagę autorów, którzy stworzyli min. 2 szkolenia
            var query7 = entityManager.createQuery("select a.lastName, count (t) from Training t join t.authors a group by a having count(t) > 1");

            // ----------------------------------------------------

            return query7.getResultList();
        });

        log.info("#####################################################################################################");
         //result.forEach(training -> log.info(training.getTitle()));
        var arrays = (List<Object[]>) result;
        arrays.forEach(objects -> log.info(Arrays.toString(objects)));


    }

    public static String getNextId() {
        return UUID.randomUUID().toString();
    }

}
