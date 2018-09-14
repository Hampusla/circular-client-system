import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
            //Throw exception?
            System.err.println("Invalid number of arguments! Input should be on the following format: " +
                    "local-port next-host next-port");
            return;
        }
        /*Create the local port... Check that the arguments are valid*/
        try {
            localPort = Integer.parseInt(argc[0]);
        }catch (NumberFormatException e) {
            System.err.println("Argument " + argc[0] + " must be an integer!");
            return;
        }
        /*Create the port that we are going to connect to... Check that the arguments are valid*/
        try {
            nextPort = Integer.parseInt(argc[2]);
        }catch (NumberFormatException e) {
            System.err.println("Argument " + argc[2] + " must be an integer!");
            return;
        }
        /*Skapa en adress */
        nextHost = argc[1];
        InetSocketAddress nextHostAdress = new InetSocketAddress(nextHost, nextPort);

        /*Check if argument 2 aka argc[1] is on the form of an IP adress or as a name of a host,
         *Check if it is possible to connect to the adress/host*/

        Thread inputThread = new Thread() {
            @Override
            public void run() {
                /*Create a port that the TCP-node listens to*/
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
                    /*TODO find out what to do with the outPutStream*/
                    OutputStream outputStream = new OutputStream() {
                        @Override
                        public void write(int b) throws IOException {

                        }
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //While... Decide what we are going to send to the port...
        };
        outputThread.start();
    }
}
