package pl.training.jpa.commons;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterAll;
import org.slf4j.LoggerFactory;
import pl.training.jpa.TestTask;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Log
public class BaseTest {

    protected final static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("training-hibernate-unit");
    protected final Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
    protected final MetricRegistry metricRegistry = new MetricRegistry();
    protected final Slf4jReporter reporter = Slf4jReporter
            .forRegistry(metricRegistry)
            .outputTo(LoggerFactory.getLogger(getClass()))
            .build();

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
            throw exception;
        } finally {
            entityManager.close();
        }
    }

    @SneakyThrows
    protected void execute(List<TestTask> tasks) {
        var userCount = tasks.size();
        var countDownLatch = new CountDownLatch(userCount);
        var executor = Executors.newFixedThreadPool(userCount);
        tasks.forEach(testTask -> {
            testTask.setEntityManager(entityManagerFactory.createEntityManager());
            testTask.setCountDownLatch(countDownLatch);
            executor.execute(testTask);
        });
        countDownLatch.await();
        log.info("Completed");
    }

    @AfterAll
    public static void onClose() {
        entityManagerFactory.close();
    }

}
