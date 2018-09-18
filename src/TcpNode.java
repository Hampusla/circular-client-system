import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.*;
import java.util.Scanner;

public class TcpNode extends Node{

    public static void main(String argc[]) {
        /*Argument should be in the following format:
         *Java: java {TcpNode,UdpNode} local-port next-host next-port*/
        int localPort, nextPort;
        /*Check that the number of arguments are correct.*/
        if (argc.length != 3) {
            System.err.println("Invalid number of arguments! Input should be on the following format: " +
                    "local-port next-host next-port");
            return;
        }
        /*Save the first argument as localPort.
         *If the argument is not in the correct format print error message and return*/
        try {
            localPort = Integer.parseInt(argc[0]);
        }catch (NumberFormatException e) {
            System.err.println("Argument " + argc[0] + " must be an integer!");
            return;
        }
        /*Save the third argument as nextPort.
         *If the argument is not in the correct format print error message and return*/
        try {
            nextPort = Integer.parseInt(argc[2]);
        }catch (NumberFormatException e) {
            System.err.println("Argument " + argc[2] + " must be an integer!");
            return;
        }
        /*Create an InetAdress "ipAddress" using the second argument,
         *Then create an InetSocketAddress using the InetAddress,
         *If the InetSocketAdress can not be created print error message and return*/
        InetAddress ipAddress;
        InetSocketAddress nextHostAddress;
        try {
            ipAddress = InetAddress.getByName(argc[1]);
            nextHostAddress = new InetSocketAddress(ipAddress, nextPort);
        } catch (UnknownHostException e) {
            //TODO Write a better error message
            System.err.println("Fail when creating a Socket adress from arguments!");
            e.printStackTrace();
            return;
        }


        /*Create the inSocket*/
        /*serverSocket is the socket that is listening to the port and waiting for a request to make a connection*/
        ServerSocket serverSocket;
        /*inSocket is the socket that will be connected to the client of the previous node when a request for connection is made to the serverSocket*/
        /*outSocket is the socket that will try to connect to the server at the next node.*/
        //TODO not really a todo but should i change the names to something more descriptive?
        Socket inSocket, outSocket;
        InputStream inputStream;
        try {
            //Lyssnar efter en förfrågan om en koppling till porten localPort....
            serverSocket = new ServerSocket(localPort);
            //När en koppling har gjorts skapas en socket som kan kommunicera över den etablerade TCP-kopplingen.
            //TODO fråga! Måste detta vara i run() på en separat tråd? Eftersom den kommer att vänta på en return från accept()
            inSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Fail: Could not create the serverSocket on port: " + localPort + ". Exiting...");
            e.printStackTrace();
            return;
        }
        /*Create the outSocket*/
        try {
            /*Create an outSocket*/
            outSocket = new Socket(ipAddress, nextPort);
            /*Connect the outSocket to the IP-address and port of the next Node*/
            outSocket.connect(nextHostAddress);
        } catch (UnknownHostException e) {
            System.err.println("Fail: Error when making and connecting the outSocket");
            e.printStackTrace();
            return;
        } catch (IOException e) {
            System.err.println("Fail: Error when making and connecting the outSocket");
            e.printStackTrace();
            return;
        }

        Thread inputThread = new Thread() {
            @Override
            public void run() {
                /*Listen to the inputSocket. If a message is received use protocol to decide what to do */
                /**
                 * Gets the input stream object of the accepted
                 * connection to enable reading from the socket.
                 */
                InputStream inputStream = null;
                try {
                    inputStream = inSocket.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /**
                 * Wrap the input stream in a Scanner for easier handling of text.
                 */
                Scanner scanner = new Scanner(inputStream);
                /**
                 * While there is a line (ended by '\n'), read it from stream and
                 * print it to stdout.
                 */
                while (scanner.hasNextLine()) {
                    System.out.println(scanner.nextLine());
                }
            }
        };
        inputThread.start();
        Thread outputThread = new Thread() {
            @Override
            public void run() {
                //TODO Add a read from stdin, and send it to the next node.
                //While... Decide what we are going to send to the port...
            }
        };
        outputThread.start();
    }
}
