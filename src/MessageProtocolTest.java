import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MessageProtocolTest {

    @Test
    void CreateProtocol() {
        new MessageProtocol("localhost,404");
    }

    @Test
    void RunsProcessInput() {
        MessageProtocol mp = new MessageProtocol("localhost,404");
        mp.processInput("Hello World!");
    }

    @Test
    void NewProtocolSendMessageTypeELECTION() {
        MessageProtocol mp = new MessageProtocol("localhost,404");
        String str = mp.processInput("Hello World!");
        char[] lengt = new char[77];
        Arrays.fill(lengt, '\0');
        String extra = new String(lengt);
        String answer = "ELECTION\n" +
            "localhost,404\n" + extra;

        assertEquals(answer, str);
    }

}