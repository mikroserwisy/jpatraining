package pl.training.jpa;

import lombok.extern.java.Log;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.training.jpa.commons.BaseTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pl.training.jpa.Fixtures.*;

@Log
public class EntityManagerTests extends BaseTest {

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

}
