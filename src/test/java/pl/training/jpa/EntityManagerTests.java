package pl.training.jpa;

import org.junit.jupiter.api.Assertions;
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

}
