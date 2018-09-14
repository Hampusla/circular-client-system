import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        /*Create an InetAdress "ipAddress" using argc[1], Then create an InetSocketAddress using the InetAddress*/
        InetAddress ipAddress;
        InetSocketAddress nextHostAddress;
        try {
            ipAddress = InetAddress.getByName(argc[1]);
            nextHostAddress = new InetSocketAddress(ipAddress, nextPort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }
        /*Create the inSocket*/
        ServerSocket serverSocket;
        Socket inSocket, outSocket;
        InputStream inputStream;
        try {
            //Lyssnar efter en förfrågan om en koppling till porten localPort....
            serverSocket = new ServerSocket(localPort);
            //När en koppling har gjorts skapas en socket som kan kommunicera över den etablerade TCP-kopplingen.
            //TODO fråga! Måste detta vara i run() på en separat tråd? Eftersom den kommer att vänta på en return från accept()
            inSocket = serverSocket.accept();
        } catch (IOException e) {
            /*TODO fix the error message!*/
            System.err.println("Fail: Error when making and connecting the inSocket");
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
            }
        };
        inputThread.start();
        Thread outputThread = new Thread() {
            @Override
            public void run() {
                //While... Decide what we are going to send to the port...
            }
        };
        outputThread.start();
    }
}
