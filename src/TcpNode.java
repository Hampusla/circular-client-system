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
        /*Argument should be in the following format:
         *Java: java {TcpNode,UdpNode} local-port next-host next-port*/
        /*Check that the number of arguments are correct.*/
        if (argc.length != 3) {
            System.err.println("Invalid number of arguments! Input should be on the following format: " +
                    "local-port next-host next-port");
            return;
        }
        /*Save the first argument as localPort.
         *If the argument is not in the correct format print error message and return*/
        int localPort;
        try {
            localPort = Integer.parseInt(argc[0]);
        } catch (NumberFormatException e) {
            System.err.println("Argument " + argc[0] + " is not an integer!");
            return;
        }
        /*Save the third argument as nextPort.
         *If the argument is not in the correct format print error message and return*/
        int nextPort;
        try {
            nextPort = Integer.parseInt(argc[2]);
        } catch (NumberFormatException e) {
            System.err.println("Argument " + argc[2] + " is not an integer!");
            return;
        }
        /*Create an InetAdress "ipAddress" using the second argument,
         *Then create an InetSocketAddress using the InetAddress,
         *If the InetSocketAdress can not be created print error message and return*/
        InetAddress nextHostIP;
        try {
            nextHostIP = InetAddress.getByName(argc[1]);
        } catch (UnknownHostException e) {
            System.err.println("Error when creating a InetAdress from argc[1]! Exiting...");
            e.printStackTrace();
            return;
        }
        System.out.println("All arguments validated.");
        new TcpNode(localPort, nextHostIP, nextPort);
    }
    private TcpNode(int localPort, InetAddress nextHostIP, int nextPort) {
        //TODO Decide if this is a good class for the queue and what capacity is neccessary.
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
        new ClientThread(nextHostIP, nextPort, messageQueue).start();
        new ServerThread(serverSocket, localPort, messageQueue).start();
    }
    class ServerThread extends Thread {
        ServerSocket serverSocket;
        int localPort;
        Socket inSocket;
        BlockingQueue inQueue;
        ServerThread(ServerSocket serverSocket, int localPort, BlockingQueue inQueue) {
            super();
            this.serverSocket = serverSocket;
            this.localPort = localPort;
            this.inQueue = inQueue;
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
                    System.err.println("Oopsy daisy something went wrong in accept()... trying again!");
                }
            }
            while (true) {
                try {
                    byte[] byteMessage = new byte[100];
                    InputStream inputStream = inSocket.getInputStream();
                    //TODO Use lengthOfByteMessage int for verification?
                    int lengthOfByteMessage = inputStream.read(byteMessage, 0, 100);
                    //TODO Validate the incoming message
                    String message = new String(byteMessage);
                    inQueue.put(message);
                } catch (IOException e) {
                    //TODO should we do something more in these catches?
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class ClientThread extends Thread {
        Socket outSocket;
        InetAddress nextHostIP;
        int nextPort;
        BlockingQueue<String> inQueue;
        ClientThread(InetAddress nextHostIP, int nextPort, BlockingQueue inQueue) {
            super();
            this.nextHostIP = nextHostIP;
            this.nextPort = nextPort;
            this.inQueue = inQueue;
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
            //When the connection is made the while loop will break and we will start doing the following:
            MessageProtocol protocol = new MessageProtocol( outSocket.getLocalSocketAddress() + "," + nextPort);
            try {
                inQueue.put("ELECTION");
            } catch (InterruptedException e) {
                //TODO make a better catch or put try catch in while
                e.printStackTrace();
                return;
            }
            String receivedMessage;
            String messageToSend;
            while (true) {
                try {
                    receivedMessage = inQueue.take();
                } catch (InterruptedException e) {
                    //TODO make a really good catch here or put in while??
                    e.printStackTrace();
                    return;
                }
                messageToSend = protocol.processInput(receivedMessage);
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
