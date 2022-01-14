package pl.training.jpa;

import lombok.extern.java.Log;

import javax.persistence.PostPersist;
import javax.persistence.PrePersist;

@Log
public class PostListener {


    @PrePersist
    public void prePersist(Post post) {
        log.info("prePersist " + post);
    }

    @PostPersist
    public void postPersist(Post post) {
        log.info("postPersist " + post);
    }

}
