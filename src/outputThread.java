import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

class outputThread extends Thread {

    private Socket outSocket;
    private InetAddress nextHostIP;
    private int nextPort;
    private int localport;
    BlockingQueue<String> messageQueue;

    outputThread(int localport, InetAddress nextHostIP, int nextPort, BlockingQueue messageQueue) {
        super();
        this.nextHostIP = nextHostIP;
        this.nextPort = nextPort;
        this.messageQueue = messageQueue;
        this.localport = localport;
    }
    @Override
    public void run() {
        //Make an outSocket
        while (true) {
            try {
                outSocket = new Socket(nextHostIP, nextPort);
                System.out.println("outSocket created");
                break;
            } catch (IOException e) {
                System.out.println("Creating outSocket failed");
                try{ Thread.sleep(2000); } catch (InterruptedException i) {
                    e.printStackTrace();
                }
                //e.printStackTrace();
            }
        }
        MessageProtocol protocol = new MessageProtocol( outSocket.getLocalSocketAddress() + "," + localport);
        String receivedMessage;
        String messageToSend;
        boolean firstMessage = true;
        while (true) {
            if (!firstMessage) {
                try {
                    receivedMessage = messageQueue.take();
                    messageToSend = protocol.processInput(receivedMessage);
                } catch (InterruptedException e) {
                    System.err.println("Something went wrong when trying to retrieve message from queue");
                    e.printStackTrace();
                    return;
                }catch (IllegalArgumentException e) {
                    System.out.println("Message given was not following format");
                    e.printStackTrace();
                    return;
                }
            }else {

                try {
                    messageToSend = protocol.processInput("NEW_NODE");
                } catch (IllegalArgumentException e) {
                    System.out.println("Message given was not following format");
                    e.printStackTrace();
                    return;
                }
                firstMessage = false;
            }
            if (messageToSend != null ) {
                byte[] byteMessage = messageToSend.getBytes();
                try {
                    OutputStream outputStream = outSocket.getOutputStream();
                    outputStream.write(byteMessage);
                } catch (IOException e) {
                    System.out.printf("Socket disconnected. Shutting down");
                    break;
                }
            }
        }
    }
}