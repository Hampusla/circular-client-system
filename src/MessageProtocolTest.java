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
        String str = mp.processInput("Hello World!\n");
        char[] lengt = new char[76];
        Arrays.fill(lengt, '\0');
        String extra = new String(lengt);
        String answer = "ELECTION\n" +
            "localhost,404\n" + extra;

        assertEquals(answer, str);
    }

    @Test
    void NewProtocolRunsElectionSectionIfElectionStart() {
        MessageProtocol mp = new MessageProtocol("localhost,404");
        char[] lengt = new char[76];
        Arrays.fill(lengt, '\0');
        String extra = new String(lengt);
        String message = "ELECTION\n" +
            "localhost,403\n" + extra;
        String str = mp.processInput(message);
        String answer = "ELECTION\n" +
            "localhost,404\n" + extra;

        assertEquals(answer, str);
    }

    @Test
    void ProtocolWillChangeToBetterLeaderUnderELECTION() {
        MessageProtocol mp = new MessageProtocol("localhost, 404");
        mp.processInput("Hello World!\n");
        char[] lengt = new char[76];
        Arrays.fill(lengt, '\0');
        String extra = new String(lengt);
        String message = "ELECTION\n" +
            "localhost,405\n" + extra;
        String mpAnswer = mp.processInput(message);

        assertEquals(message, mpAnswer);
    }

    @Test
    void WillChangeToELECTION_OVERIfGivenOwnID() {
        MessageProtocol mp = new MessageProtocol("localhost, 404");
        mp.processInput("Hello World!\n");
        char[] lengt = new char[76];
        Arrays.fill(lengt, '\0');
        String extra = new String(lengt);
        String message = "ELECTION\n" +
            "localhost,404\n" + extra;
        String mpAnswer = mp.processInput(message);

        lengt = new char[71];
        Arrays.fill(lengt, '\0');
        extra = new String(lengt);
        String answer = "ELECTION_OVER\n" +
            "localhost,404" + extra;

        assertEquals(answer, mpAnswer);
    }

    @Test
    void WIllChanceToEleOverIfGivenELECTION_OVER() {
        MessageProtocol mp = new MessageProtocol("localhost, 404");
        mp.processInput("Hello World!\n");
        char[] lengt = new char[71];
        Arrays.fill(lengt, '\0');
        String extra = new String(lengt);
        String message = "ELECTION_OVER\n" +
            "localhost,405\n" + extra;
        String mpAnswer = mp.processInput(message);

        assertEquals(message, mpAnswer);
    }

    @Test
    void WillContinueEleOverIfNotGivenOwnID() {
        MessageProtocol mp = new MessageProtocol("localhost, 404");
        mp.processInput("Hello World!\n");
        char[] lengt = new char[71];
        Arrays.fill(lengt, '\0');
        String extra = new String(lengt);
        String message = "ELECTION_OVER\n" +
            "localhost,405\n" + extra;
        mp.processInput(message);
        String mpAnswer = mp.processInput(message);

        assertEquals(message, mpAnswer);
    }

    @Test
    void WillStartSendingMessageIfLeader() {
        MessageProtocol mp = new MessageProtocol("localhost, 404");
        mp.processInput("Hello World!\n");
        char[] lengt = new char[76];
        Arrays.fill(lengt, '\0');
        String extra = new String(lengt);
        String message = "ELECTION\n" +
            "localhost,404\n" + extra;
        message = mp.processInput(message);
        String mpAnswer = mp.processInput(message);

        lengt = new char[74];
        Arrays.fill(lengt, '\0');
        extra = new String(lengt);
        String answer = "MESSAGE\n" +
            "This is a message\n" + extra;

        assertEquals(answer, mpAnswer);
    }

    @Test
    void WillSendMessageIfNotLeader() {
        MessageProtocol mp = new MessageProtocol("localhost, 404");
        mp.processInput("Hello World!\n");
        char[] lengt = new char[71];
        Arrays.fill(lengt, '\0');
        String extra = new String(lengt);
        String message = "ELECTION_OVER\n" +
            "localhost,405\n" + extra;
        mp.processInput(message);
        lengt = new char[74];
        Arrays.fill(lengt, '\0');
        extra = new String(lengt);
        message = "MESSAGE\n" +
            "This is a message\n" + extra;
        String mpAnswer = mp.processInput(message);

        assertEquals(message, mpAnswer);
    }

//    MessageProtocol CreateNew(String socketID) {
//
//    }
}