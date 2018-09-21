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
        System.out.println("Arguments validated. Creating Node");
        new TcpNode(localPort, nextHostIP, nextPort);
    }

    private TcpNode(int localPort, InetAddress nextHostIP, int nextPort) {
        //Create a ServerSocket which listens to the given localPort
        BlockingQueue messageQueue = new ArrayBlockingQueue<String>(10);
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(localPort);
            serverSocket.setReuseAddress(true);
        } catch (IOException e) {
            System.err.println("Fail when creating the serverSocket on port: " + localPort + ". Exiting...");
            return;
        }
        System.out.println("Listening to serverSocket for requests on port " + localPort);
        //Create a new Thread for the incoming messages and one for sending of messages
        new outputThread(localPort, nextHostIP, nextPort, messageQueue).start();
        new inputThread(serverSocket, localPort, messageQueue).start();
    }
}
