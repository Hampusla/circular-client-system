import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TcpNodeTest {

    @Test
    void serializationAndDeserialization() {
        String message = "Message";
        String answer = new TcpNode().deserialization(
            new TcpNode().serialization(message));
        assertEquals(message , answer);
    }

    @Test
    void deserialization() {

    }
}