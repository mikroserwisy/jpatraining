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

            var criteriaBuilder = entityManager.getCriteriaBuilder();

            // zwróć listę szkoleń, zapewnij stronicowanie
            var pageNumber = 0;
            var pageSize = 10;
            var query1 =  entityManager.createQuery("select t from Training t", TrainingEntity.class)
                    .setHint("javax.persistence.fetchgraph",  entityManager.getEntityGraph(TrainingEntity.EAGER))
                    .setFirstResult(pageNumber * pageSize)
                    .setMaxResults(pageSize);

            var cq1 = criteriaBuilder.createQuery(TrainingEntity.class);
            var trainingsRoot = cq1.from(TrainingEntity.class);
            cq1.select(trainingsRoot);
            var query1cb = entityManager.createQuery(cq1)
                    .setHint("javax.persistence.fetchgraph",  entityManager.getEntityGraph(TrainingEntity.EAGER))
                    .setFirstResult(pageNumber * pageSize)
                    .setMaxResults(pageSize);


            // select t from Training t where t.title = 'Programming in C++'
            var cq1b = criteriaBuilder.createQuery(TrainingEntity.class);
            var trainingsRoot2 = cq1b.from(TrainingEntity.class);
            var title = trainingsRoot2.<String>get("title");
            var expectedTitle = "Programming in C++";
            cq1b.select(trainingsRoot2)
                    .where(criteriaBuilder.equal(title, expectedTitle));
            var query2cb = entityManager.createQuery(cq1b);


            // zwróć listę kodów i tytułów (warianty: przez pola, przez wyrażenie konstruktorowe)
            var query2 =  entityManager.createQuery("select t.code, t.title from Training t");

           /* var cq2 = criteriaBuilder.createQuery(Object[].class);
            var trainingsRoot3 = cq2.from(TrainingEntity.class);
            cq2.multiselect(trainingsRoot3.get("code"), trainingsRoot3.get("title"));
            var query3cb = entityManager.createQuery(cq2);*/

            var cq2 = criteriaBuilder.createTupleQuery();
            var trainingsRoot3 = cq2.from(TrainingEntity.class);
            cq2.multiselect(trainingsRoot3.get("code").alias("trainingCode"), trainingsRoot3.get("title"));
            var query3cb = entityManager.createQuery(cq2);

            var query3 =  entityManager.createQuery("select new pl.training.jpa.trainings.repository.TrainingView(t.code, t.title) from Training t", TrainingView.class);

            var cq3 = criteriaBuilder.createQuery(TrainingView.class);
            var trainingsRoot4 = cq3.from(TrainingEntity.class);
            cq3.select(criteriaBuilder.construct(TrainingView.class, trainingsRoot4.get("code"), trainingsRoot4.get("title")));
            var query4cb = entityManager.createQuery(cq3);

            // select a.lastName, t.title from Training t join t.authors a
            var cq4 = criteriaBuilder.createTupleQuery();
            var trainingsRoot5 = cq4.from(TrainingEntity.class);
            var trainingsRoot6 = trainingsRoot5.<TrainingEntity, PersonEntity>join("authors");
            cq4.multiselect(trainingsRoot6.get("lastName").alias("lastName"), trainingsRoot5.get("title").alias("title"));
            var query5cb = entityManager.createQuery(cq4);

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

            return query5cb.getResultList();
        });

        log.info("#####################################################################################################");

        result.forEach(tuple -> log.info(tuple.get("lastName") + " ," + tuple.get("title")));
        //result.forEach(training -> log.info(training.getTitle()));
        //var arrays = (List<Object[]>) result;
        //arrays.forEach(objects -> log.info(Arrays.toString(objects)));


    }

    public static String getNextId() {
        return UUID.randomUUID().toString();
    }

}
