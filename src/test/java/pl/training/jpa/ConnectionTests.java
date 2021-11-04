package pl.training.jpa;

import lombok.extern.java.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.training.jpa.commons.BaseTest;

@Log
public class ConnectionTests extends BaseTest {

    @Test
    void should_connect_to_database() {
        withTransaction(Assertions::assertNotNull);
    }

}
