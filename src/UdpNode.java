import java.io.IOException;
import java.net.*;

/**
 *
 */
public class UdpNode extends Node {

    public static void main(String argc[])
         throws UnknownHostException, InterruptedException{

        UdpNode butWhyyy = new UdpNode();

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

        DatagramPacket rcdp = new DatagramPacket(new byte[100], 100);
        DatagramPacket sndp;


        //TODO Create loop for setup
        /**
         *  *Try to send
         *  *Timeout 1 sec
         *  *Check if received anything.
         */

        // Send first time
        String startMessage = messageProtocol.processInput("NEW_MODE");
        byte[] startBytes = butWhyyy.serialization(startMessage);
        sndp = new DatagramPacket(
            startBytes, startBytes.length, nextHost, nextPort);

        try {
            datagramSocket.send(sndp);
        } catch (IOException e) {
            System.out.println("Ooops IOException when sending");
        }

        // Continue to send first message until a packet is received
        while (rcdp.getData() == null) {

            startMessage = messageProtocol.processInput("RESEND_FIRST");
            startBytes = butWhyyy.serialization(startMessage);

            sndp = new DatagramPacket(
                startBytes, startBytes.length, nextHost, nextPort);

            try {
                datagramSocket.send(sndp);
            } catch (IOException e) {
                System.out.println("Ooops IOException when sending");
            }

            Thread.sleep(1000);
        }

        //TODO Create receive and send loop
        while (true) {


            //Receive packet
            try {
                datagramSocket.receive(rcdp);
            } catch (IOException e) {
                System.out.println("Ooops IOException");
            }


            //deserialize
            String input = butWhyyy.deserialization(rcdp.getData());

            //Check what to do
            String output = messageProtocol.processInput(input);

            //serialize
            byte[] outData = butWhyyy.serialization(output);

            // pack Data
            sndp = new DatagramPacket(
                outData, outData.length, nextHost, nextPort);

            //send
            try {
                datagramSocket.send(sndp);
            } catch (IOException e) {
                System.out.println("Ooops IOException");
            }


            //Create packet to receive in
            rcdp = new DatagramPacket(new byte[100], 100);
        }

        //TODO
    }
}
