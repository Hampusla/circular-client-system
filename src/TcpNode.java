import com.sun.tools.jdeprscan.scan.Scan;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.Scanner;

public class TcpNode extends Node {

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
        } catch (NumberFormatException e) {
            System.err.println("Argument " + argc[0] + " must be an integer!");
            return;
        }
        /*Save the third argument as nextPort.
         *If the argument is not in the correct format print error message and return*/
        try {
            nextPort = Integer.parseInt(argc[2]);
        } catch (NumberFormatException e) {
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
            System.err.println("Error when creating a InetSocketAdress from argc[1]! Exiting...");
            e.printStackTrace();
            return;
        }
        System.out.println("All arguments validated.");

        /*serverSocket is the socket that is listening to the port and waiting for a request to make a connection*/
        ServerSocket serverSocket;
        try {
            //Skapar en ServerSocket som lyssnar efter en förfrågan om en koppling till porten localPort....
            serverSocket = new ServerSocket(localPort);
        } catch (IOException e) {
            System.err.println("Fail: Could not create the serverSocket on port: " + localPort + ". Exiting...");
            e.printStackTrace();
            return;
        }
        //TODO not really a todo but should i change the names of inSocket and outSocket to something more descriptive?
        System.out.println("Listening to serverSocket for requests on port " + localPort);

        /*Create the outSocket*/
        //TODO FRÅGA är det så här man ska skriva? jag la det i en egen tråd för att man ska kunna göra både outSocket kopplingen och inSocket kopplingen samtidigt...
        new Thread() {
            Socket outSocket;

            @Override
            public void run() {
                /*Try making a socket which connects to the next Node*/
                while (true) {
                    try {
                        System.out.println("Creating outSocket to nextHost: " + ipAddress + " on nextPort: " + nextPort);
                        outSocket = new Socket(ipAddress, nextPort);
                        System.out.println("outSocket created");
                        break; //Vet ej om denna ska vara här
                    } catch (IOException e) {
                        System.out.println("Creating outSocket failed");
                        //e.printStackTrace();
                    }
                }
                //When the connection is made the while loop will break and we will start doing the following:
                //TODO Use protocol to decide what to do based on the message, and what state we are in.
                //TODO Translate message to bytes
                //TODO Send the message to the next node
                while (true) {
                    try {
                        OutputStream outputStream = outSocket.getOutputStream();
                        Scanner s = new Scanner(System.in);
                        while (s.hasNextLine()) {
                            outputStream.write(s.next().getBytes());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        /*Create the inSocket*/
        Socket inSocket;
        while (true) {
            /*Try creating a connection to a client sending a request to the port that serverSocket is listening to.*/
            try {
                System.out.println("Calling accept on serverSocket aka accepting requests made to localPort: " + localPort);
                inSocket = serverSocket.accept();
                System.out.println("Created connection to inSocket!");
                break;
            } catch (IOException e) {
                //TODO write an actual error message and decide what action to take..
                System.err.println("Exception when accepting....");
            }
        }
        InputStream inputStream;
        while (true) {
            try {
                inputStream = inSocket.getInputStream();
                System.out.println(inputStream.read());
                //TODO Read incoming messages
                /*Incoming messages will be byte arrays,
                 *and should be in the format that the Message Protocol specifies*/
                //TODO Validate the incomming message (how do i do that before translating it?)
                //TODO Translate the message to String
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
