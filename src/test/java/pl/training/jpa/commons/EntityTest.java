package pl.training.jpa.commons;

import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// https://vladmihalcea.com/hibernate-facts-equals-and-hashcode
// https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier
public abstract class EntityTest<E extends Identifiable> extends BaseTest{

    protected final Set<E> entities = new HashSet<>();
    protected final EntityManager entityManager = entityManagerFactory.createEntityManager();
    protected final EntityTransaction transaction = entityManager.getTransaction();

    protected E entity;

    protected abstract E initializeEntity();

    @BeforeEach
    void beforeEach() {
        entity = initializeEntity();
        entities.add(entity);
        transaction.begin();
    }

    @Test
    void set_should_contain_entity() {
        assertTrue(entities.contains(entity));
    }

    @Test
    void set_should_contain_entity_after_persist() {
        entityManager.persist(entity);
        assertTrue(entities.contains(entity));
    }

    @Test
    void set_should_contain_entity_after_merge() {
        entityManager.persist(entity);
        var mergedEntity = entityManager.merge(entity);
        entityManager.flush();
        assertTrue(entities.contains(mergedEntity));
    }

    @Test
    void set_should_contain_entity_after_remove() {
        entityManager.persist(entity);
        var entityProxy = entityManager.getReference(entity.getClass(), entity.getId());
        entityManager.remove(entityProxy);
        assertTrue(entities.contains(entityProxy));
    }

    @Test
    void entity_should_equals_proxy() {
        entityManager.persist(entity);
        var entityProxy = entityManager.getReference(entity.getClass(), entity.getId());
        assertEquals(entity, entityProxy);
    }

    @Test
    void proxy_should_equals_entity() {
        entityManager.persist(entity);
        var entityProxy = entityManager.getReference(entity.getClass(), entity.getId());
        assertEquals(entityProxy, entity);
    }

    @Test
    void should_return_entity_from_set_after_refresh() {
        entityManager.persist(entity);
        entityManager.unwrap(Session.class).update(entity);
        assertTrue(entities.contains(entity));
    }

    @Test
    void  set_should_contain_entity_after_find() {
        entityManager.persist(entity);
        var loadedEntity = entityManager.find(entity.getClass(), entity.getId());
        assertTrue(entities.contains(loadedEntity));
    }

    @Test
    void  set_should_contain_entity_after_get_reference() {
        entityManager.persist(entity);
        var entityProxy = entityManager.getReference(entity.getClass(), entity.getId());
        assertTrue(entities.contains(entityProxy));
    }


}
