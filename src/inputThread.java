import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

class inputThread extends Thread {

    private ServerSocket serverSocket;
    private int localPort;
    private Socket inSocket;
    private BlockingQueue messageQueue;

    inputThread(ServerSocket serverSocket, int localPort, BlockingQueue messageQueue) {
        super();
        this.serverSocket = serverSocket;
        this.localPort = localPort;
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        while (true) {
            //Create a connection to a client using serverSocket.*/
            try {
                inSocket = serverSocket.accept();
                break;
            } catch (IOException e) {
                System.err.println("Oopsie daisy something went wrong in accept()... trying again!");
            }
        }
        System.out.println("Connection via inSocket established!");
        byte[] byteMessage = new byte[100];
        InputStream inputStream;
        try {
            inputStream = inSocket.getInputStream();
        } catch (IOException e) {
            System.err.println("Fail when making getting inputStream");
            e.printStackTrace();
            return;
        }
        while (true) {
            try {
                //Read what is in the inputStream and store as a byte[]
                int lengthOfByteMessage = inputStream.read(byteMessage, 0, 100);
                while (lengthOfByteMessage != 100){
                    lengthOfByteMessage = lengthOfByteMessage +
                            inputStream.read(byteMessage, lengthOfByteMessage, 100);
                }
                //Translate the byte[] to a String message and put it in the messageQueue
                String message = new String(byteMessage);
                messageQueue.put(message);
            } catch (InterruptedException | IOException e) {
                System.err.println("Oh no! Something went wrong when getting message from inSocket");
                e.printStackTrace();
            }
        }
    }
}