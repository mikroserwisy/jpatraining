package pl.training.jpa;

import javax.persistence.EntityManager;
import java.util.concurrent.CountDownLatch;

public interface TestTask extends Runnable {

    void setEntityManager(EntityManager entityManager);

    void setCountDownLatch(CountDownLatch countDownLatch);

}
