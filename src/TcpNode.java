import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class TcpNode extends Node{

    public static void main(String argc[]) {
        /*Argument should be in the following format:
         *Java: java {TcpNode,UdpNode} local-port next-host next-port*/
        int localPort, nextPort;
        String nextHost;
        if (argc.length != 3) {
            //Kasta exception?
            System.err.println("Invalid number of arguments! Input should be on the following format: " +
                    "local-port next-host next-port");
            return;
        }
        try {
            localPort = Integer.parseInt(argc[0]);
        }catch (NumberFormatException e) {
            System.err.println("Argument " + argc[0] + " must be an integer!");
            return;
        }
        try {
            nextPort = Integer.parseInt(argc[2]);
        }catch (NumberFormatException e) {
            System.err.println("Argument " + argc[2] + " must be an integer!");
            return;
        }
        nextHost = argc[1];
        InetSocketAddress nextHostAdress = new InetSocketAddress(nextHost, nextPort);

        /*Jag vill kolla om argument 2 aka argc[1] är skrivet som en IP adress eller som ett namn på en host,
         *och om det är möjligt att ansluta till den angivna adressen/hosten*/

        Thread inputThread = new Thread() {
            @Override
            public void run() {
                /*Skapa en port som TCP-noden ska lyssna till från första delen av args*/
                ServerSocket serverSocket;
                Socket inSocket;
                InputStream inputStream;
                //Scanner scanner;
                try {
                    serverSocket = new ServerSocket(localPort);
                    inSocket = serverSocket.accept();
                    inputStream = inSocket.getInputStream();
                    //scanner = new Scanner(inputStream);
                } catch (IOException e) {
                    /*TODO fix the error message!*/
                    /*TODO add a return?*/
                    System.err.println("Fail: Could not make a serverSocket...");
                    e.printStackTrace();
                    return;
                }
                //while sats?
                //Vänta på ett meddelande och sedan göra något när den får meddelandet.
            }
        };
        inputThread.start();
        Thread outputThread = new Thread() {
            @Override
            public void run() {
                try {
                    Socket outSocket = new Socket(nextHost, nextPort);
                    outSocket.connect(nextHostAdress);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        //int port = Integer.parseInt(args[0]);
        /*Skapa en adress */
        /*Skapa en port att skicka till*/

        /*Skapa socket*/

        /*Skapa output stream*/

        /*Skapa input stream*/

        /*Skapa */
    }
}
