import java.net.*;

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

        //TODO Create Datagram Packets to be used.

        DatagramPacket rcdp;
        DatagramPacket sndp;

        //TODO Create loop for setup

        //TODO Create receive and send loop
        while (true) {
            //Create packet to receive in
            rcdp = new DatagramPacket(new byte[100], 100);

            //Receive packet
            datagramSocket.receive(rcdp);

            //deserialize
            String input = this.deserialization(rcdp.getData());

            //Check what to do
            String output = messageProtocol.processInput(input);

            //serialize
            byte[] outData = this.serialization(output);

            // pack Data
            sndp = new DatagramPacket(
                outData, outData.length, nextHost, nextPort);

            //send
            datagramSocket.send(sndp);
        }

        //TODO
    }
}
