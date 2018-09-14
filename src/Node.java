public abstract class Node {


    protected byte[] serialization(String Message) {
        return null;
    }

    protected String deserialization(byte[] bytes) {
        return new String(bytes);
    }
}
