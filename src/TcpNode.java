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
        } catch (IOException e) {
            System.err.println("Fail: Could not create the serverSocket on port: " + localPort + ". Exiting...");
            e.printStackTrace();
            return;
        }
        //TODO Byt kanske ut true till typ boolean serverOn så att man kan stänga av den och sedan köra ServerSocket.close
        while (true) {
            /*Try creating a connection to a client sending a request to the port that serverSocket is listening to.*/
            try {
                //TODO fråga handledarna! Måste detta vara i run() på en separat tråd? Eftersom den kommer att vänta på en return från accept()
                inSocket = serverSocket.accept();
                Thread inThread = new Thread() {
                    @Override
                    public void run() {
                        //Read incoming byte[]
                        //Validate the incomming message
                        //Translate the message
                    }
                };
                inThread.start();
            } catch (IOException e) {
                //TODO write an actual error message and decide what action to take..
                System.err.println("Exception when accepting....");
            }
            /*Try making a socket which connects to the next Node*/
            try {
                outSocket = new Socket(ipAddress, nextPort);
                Thread outThread = new Thread() {
                    @Override
                    public void run() {
                        //Use protocol to decide what to do based on the message, and what state we are in.
                        //Translate message back to bytes
                        //Send the message to the next node
                    }
                };
            } catch (IOException e) {
                //TODO fix what happens when an exception happens..
                e.printStackTrace();
                //TODO FRÅGA! är kopplingen gjord efter detta eller ska jag använda metoden Socket.connect(nextHostAdress)
            }
        }
    }
}
