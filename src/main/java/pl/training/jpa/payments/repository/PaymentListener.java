package pl.training.jpa.payments.repository;

import lombok.extern.java.Log;
import pl.training.jpa.payments.repository.PaymentEntity;

import javax.persistence.*;

@Log
public class PaymentListener {

    @PrePersist
    public void prePersist(PaymentEntity paymentEntity) {
        log.info("prePersist");
    }

    @PostPersist
    public void postPersist(PaymentEntity paymentEntity) {
        log.info("postPersist");
    }

    @PreRemove
    public void preRemove(PaymentEntity paymentEntity) {
        log.info("preRemove");
    }

    @PostRemove
    public void postRemove(PaymentEntity paymentEntity) {
        log.info("postRemove");
    }

    @PreUpdate
    public void preUpdate(PaymentEntity paymentEntity) {
        log.info("preUpdate");
    }

    @PostUpdate
    public void postUpdate(PaymentEntity paymentEntity) {
        log.info("postUpdate");
    }

    @PostLoad
    public void postLoad(PaymentEntity paymentEntity) {
        log.info("postLoad");
    }

}
