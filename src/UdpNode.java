import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 */
public class UdpNode extends Node {

    public static void main(String argc[]) throws UnknownHostException{

        //TODO Create port and host for self
        int port = Integer.parseInt(argc[0]);
        InetAddress Host = InetAddress.getLocalHost();

        //TODO Create port and  ip for receiver
        InetAddress nextHost = InetAddress.getByName(argc[1]);
        int nextPort = Integer.parseInt(argc[2]);

        //TODO Create MessageProtocol
        String socketID = Host + "," + port;
        MessageProtocol messageProtocol = new MessageProtocol(socketID);

        //TODO Create Datagram Socket
        DatagramSocket datagramSocket;
        try {
            datagramSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.out.println("Could not bind to port!");
            return;
        }

        //TODO Create loop for setup

        //TODO Create receive and send loop

        //TODO
    }
}
