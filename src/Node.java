public abstract class Node {


    protected byte[] serialization(String message) {
        return message.getBytes();
    }

    protected String deserialization(byte[] bytes) {
        return new String(bytes);
    }
}
