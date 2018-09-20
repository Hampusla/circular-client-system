//TODO fix javadoc
//TODO ska vi ha något som avslutar programmet eller ska det endast göras om man terminerar processen?
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TcpNode {

    public static void main(String argc[]) {
        //Validate the number of arguments
        if (argc.length != 3) {
            System.err.println("Invalid number of arguments! Input should be on the following format: " +
                    "local-port next-host next-port");
            return;
        }
        //Validate (arg[0], arg[2]) and save as ports
        int localPort, nextPort;
        try {
            localPort = Integer.parseInt(argc[0]);
            nextPort = Integer.parseInt(argc[2]);
        } catch (NumberFormatException e) {
            System.err.println("Argument 1 and 3 must be integers!");
            return;
        }
        //Create InetAdress using arg[1]
        InetAddress nextHostIP;
        try {
            nextHostIP = InetAddress.getByName(argc[1]);
        } catch (UnknownHostException e) {
            System.err.println("Error when creating a InetAdress from argc[1]! Exiting...");
            return;
        }
        System.out.println("Arguments validated creating Node");
        new TcpNode(localPort, nextHostIP, nextPort);
    }

    private TcpNode(int localPort, InetAddress nextHostIP, int nextPort) {
        BlockingQueue messageQueue = new ArrayBlockingQueue<String>(10);
        ServerSocket serverSocket;
        try {
            //Skapar en ServerSocket som lyssnar efter en förfrågan om en koppling till porten localPort....
            serverSocket = new ServerSocket(localPort);
        } catch (IOException e) {
            System.err.println("Fail when creating the serverSocket on port: " + localPort + ". Exiting...");
            return;
        }
        //TODO not really a todo but should i change the names of inSocket and outSocket to something more descriptive?
        System.out.println("Listening to serverSocket for requests on port " + localPort);
        /*Create the outSocket*/
        new outputThread(nextHostIP, nextPort, messageQueue).start();
        new inputThread(serverSocket, localPort, messageQueue).start();
    }

    class inputThread extends Thread {
        ServerSocket serverSocket;
        int localPort;
        Socket inSocket;
        BlockingQueue messageQueue;
        inputThread(ServerSocket serverSocket, int localPort, BlockingQueue messageQueue) {
            super();
            this.serverSocket = serverSocket;
            this.localPort = localPort;
            this.messageQueue = messageQueue;
        }
        @Override
        public void run() {
            /*Create the inSocket*/
            while (true) {
                /*Try creating a connection to a client sending a request to the port that serverSocket is listening to.*/
                try {
                    inSocket = serverSocket.accept();
                    System.out.println("Created connection to inSocket!");
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
                    //TODO Use lengthOfByteMessage int for verification?
                    //Read what is in the inputStream and store as a byte[]
                    int lengthOfByteMessage = inputStream.read(byteMessage, 0, 100);
                    //TODO Validate the incoming message
                    //Translate the byte[] to a String message and put it in the messageQueue
                    String message = new String(byteMessage);
                    System.out.println("Got a new message: " + message + " putting it in messageQueue");
                    //TODO If Queue is full put() will wait until there is room for the message... Can create packet loss
                    messageQueue.put(message);
                } catch (InterruptedException | IOException e) {
                    System.err.println("Oh no! Something went wrong when getting message from inSocket");
                    e.printStackTrace();
                }
            }
        }
    }
    class outputThread extends Thread {
        Socket outSocket;
        InetAddress nextHostIP;
        int nextPort;
        BlockingQueue<String> messageQueue;
        outputThread(InetAddress nextHostIP, int nextPort, BlockingQueue messageQueue) {
            super();
            this.nextHostIP = nextHostIP;
            this.nextPort = nextPort;
            this.messageQueue = messageQueue;
        }
        @Override
        public void run() {
            /*Try making a socket which connects to the next Node*/
            while (true) {
                try {
                    System.out.println("Creating outSocket to nextHost: " + nextHostIP + " on nextPort: " + nextPort);
                    outSocket = new Socket(nextHostIP, nextPort);
                    System.out.println("outSocket created");
                    break;
                } catch (IOException e) {
                    System.out.println("Creating outSocket failed");
                    //e.printStackTrace();
                }
            }
            MessageProtocol protocol = new MessageProtocol( outSocket.getLocalSocketAddress() + "," + nextPort);
            String receivedMessage;
            String messageToSend;
            boolean firstMessage = true;
            while (true) {
                if (!firstMessage) {
                    try {
                        receivedMessage = messageQueue.take();
                        messageToSend = protocol.processInput(receivedMessage);
                    } catch (InterruptedException e) {
                        //TODO make a really good catch here or put in while??
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
                    System.out.println("Sending message: " + messageToSend + "To next node");
                    byte[] byteMessage = messageToSend.getBytes();
                    try {
                        OutputStream outputStream = outSocket.getOutputStream();
                        outputStream.write(byteMessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
