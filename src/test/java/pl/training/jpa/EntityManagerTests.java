package pl.training.jpa;

import lombok.extern.java.Log;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.training.jpa.blog.Comment;
import pl.training.jpa.blog.Post;
import pl.training.jpa.commons.BaseTest;
import pl.training.jpa.commons.LocalMoney;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pl.training.jpa.Fixtures.*;
import static pl.training.jpa.blog.Post.GET_ALL_EAGER;

@Log
public class EntityManagerTests extends BaseTest {

    private static final long SAMPLES = 10_000;

    private final Comment firstComment = new Comment();
    private final Comment secondComment = new Comment();
    private final Post post = Post.builder()
            .content("Post content")
            .slug("my-post")
            .comments(List.of(firstComment, secondComment))
            .title("My post")
            .build();

    @BeforeEach
    void beforeEach() {
        firstComment.setText("First comment");
        secondComment.setText("Second comment");
    }

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

    @Test
    void should_use_complex_key() {
        var pesel = "919911776651";
        var lastName = "Kowalski";
        var firstName = "Jan";
        var personId = new PersonId(pesel, lastName);
        var person = new Person();
        person.setPersonId(personId);
        //person.setPesel(pesel);
        //person.setLastName(lastName);
        person.setFirstName(firstName);

        withTransaction(entityManager -> entityManager.persist(person));

        withTransaction(entityManager -> {
            var loadedPerson = entityManager.find(Person.class, personId);
            assertEquals(firstName, loadedPerson.getFirstName());
        });
    }

    @Test
    void should_throw_when_persist_only_part_of_relation() {
        withTransaction(entityManager -> {
            entityManager.persist(post);
            assertThrows(IllegalStateException.class, entityManager::flush);
        });
    }

    @Test
    void should_persist_object_with_relation() {
        withTransaction(entityManager -> {
            entityManager.persist(post);
            entityManager.persist(firstComment);
            entityManager.persist(secondComment);
        });
    }

    @Test
    void should_not_load_post_until_property_is_accessed() {
        withTransaction(entityManager -> {
            entityManager.persist(post);
            entityManager.persist(firstComment);
            entityManager.persist(secondComment);
        });
        withTransaction(entityManager -> {
            var postProxy = entityManager.getReference(Post.class, post.getId());
            postProxy.getId();
            assertEquals(0, statistics.getEntityLoadCount());
            postProxy.getTitle();
            assertEquals(1, statistics.getEntityLoadCount());
        });
    }

    private Post loadedPost;

    @Test
    void should_throw_exception_when_loading_comments_after_transaction_is_closed() {
        withTransaction(entityManager -> {
            entityManager.persist(post);
            entityManager.persist(firstComment);
            entityManager.persist(secondComment);
        });
        withTransaction(entityManager -> loadedPost = entityManager.find(Post.class, post.getId()));
        var comments = loadedPost.getComments();
        assertThrows(LazyInitializationException.class, () -> comments.forEach(comment -> log.info("Comment: " + comment.getText())));
    }

    @Test
    void should_eagerly_load_comments_with_fetch() {
        withTransaction(entityManager -> {
            entityManager.persist(post);
            entityManager.persist(firstComment);
            entityManager.persist(secondComment);
        });
        //withTransaction(entityManager -> entityManager.createQuery("select p from Post p join fetch p.comments pc", Post.class).getResultList());
        withTransaction(entityManager -> entityManager.createNamedQuery(GET_ALL_EAGER, Post.class).getResultList());
        assertEquals(3, statistics.getEntityLoadCount());
    }

    @Test
    void should_eagerly_load_comments_using_named_entity_graph() {
        withTransaction(entityManager -> {
            entityManager.persist(post);
            entityManager.persist(firstComment);
            entityManager.persist(secondComment);
        });
        withTransaction(entityManager -> {
            var graph = entityManager.createEntityGraph(Post.class);
            graph.addAttributeNodes("comments");
            // wszystko co jest wymienione jest ładowane zachłannie, pozostałe pola leniwie
            //Map<String, Object> properties = Map.of("javax.persistence.fetchgraph", entityManager.getEntityGraph(Post.WITH_COMMENTS));
            // wszystko co jest wymienione jest ładowane zachłannie, pozostałe pola zgodnie z ustawieniami na poziomie relacji
            Map<String, Object> properties = Map.of("javax.persistence.loadgraph", graph);
            entityManager.find(Post.class, post.getId(), properties);
        });
        assertEquals(3, statistics.getEntityLoadCount());
    }

    @Test
    void should_return_metrics_for_adding_payments_in_single_transaction() {
        var timer = metricRegistry.timer(getClass().getName());
        var startTime = System.nanoTime();
        preparePayments();
        timer.update(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        reporter.report();
    }

    @Test
    void should_return_metrics_for_adding_payments_in_many_transaction() {
        var timer = metricRegistry.timer(getClass().getName());
        var startTime = System.nanoTime();
        for (long sample = 1; sample <= SAMPLES; sample++) {
            withTransaction(entityManager -> {
                var payment = Fixtures.payment(1_000L);
                entityManager.persist(payment);
            });
        }
        timer.update(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        reporter.report();
    }

    @Test
    void should_return_metrics_for_updating_payments_in_many_transaction() {
        preparePayments();
        var timer = metricRegistry.timer(getClass().getName());
        var startTime = System.nanoTime();
        withTransaction(entityManager -> {
            var pageSize = 1_000;
            for (int page = 1; page < (SAMPLES / pageSize); page++) {
                entityManager.createQuery("select p from Payment p", Payment.class)
                        .setFirstResult(page * pageSize)
                        .setMaxResults(pageSize)
                        .getResultList()
                        .forEach(payment -> {
                            payment.getValue().add(LocalMoney.of(10));
                            entityManager.merge(payment);
                            entityManager.flush();
                        });
                entityManager.clear();
            }
        });
        timer.update(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        reporter.report();
    }

    @Test
    void should_return_metrics_for_updating_payments_in_one_update() {
        preparePayments();
        var timer = metricRegistry.timer(getClass().getName());
        var startTime = System.nanoTime();
        withTransaction(entityManager -> {
            entityManager.createQuery("update Payment p set p.value = :value")
                    .setParameter("value", LocalMoney.of(500))
                    .executeUpdate();
        });
        timer.update(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        reporter.report();
    }

    @Test
    void locking_test() {
        var payment = payment(1_000L);
        withTransaction(entityManager -> entityManager.persist(payment));
        var firstTask = new UpdatePaymentTask(payment.getId(), LocalMoney.of(100), 1, 10, LockModeType.PESSIMISTIC_WRITE);
        var secondTask = new UpdatePaymentTask(payment.getId(), LocalMoney.of(200), 3, 5, LockModeType.PESSIMISTIC_READ);
        execute(List.of(firstTask, secondTask));
        withTransaction(entityManager -> {
            var persistedPayment = entityManager.find(Payment.class, payment.getId());
            log.info(persistedPayment.toString());
        });
    }

    private void preparePayments() {
        withTransaction(entityManager -> {
            for (long sample = 1; sample <= SAMPLES; sample++) {
                var payment = Fixtures.payment(1_000L);
                entityManager.persist(payment);
            }
        });
    }

}
