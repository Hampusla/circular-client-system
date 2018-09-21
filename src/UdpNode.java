import java.io.IOException;
import java.net.*;


public class UdpNode {

    public static void main(String argc[]) throws UnknownHostException {

        //Create port and host for self
        int port = Integer.parseInt(argc[0]);
        InetAddress Host = InetAddress.getLocalHost();

        //Create port and  ip for receiver
        InetAddress nextHost = InetAddress.getByName(argc[1]);
        int nextPort = Integer.parseInt(argc[2]);

        //Create MessageProtocol
        String socketID = Host + "," + port;
        MessageProtocol messageProtocol = new MessageProtocol(socketID);

        //Create Datagram Socket
        DatagramSocket datagramSocket;
        try {
            datagramSocket = new DatagramSocket(port);
            datagramSocket.setSoTimeout(1000);
        } catch (SocketException e) {
            System.out.println("Could not bind to port or set timeout!");
            e.printStackTrace();
            return;
        }

        System.out.println("Socket made");

        //Create Datagram Packets to be used.
        DatagramPacket rcdp = new DatagramPacket(new byte[100], 100);
        DatagramPacket sndp;

        // Continue to send first message until a packet is received
        String startMessage;
        byte[] startBytes;
        boolean waitDone = false;

        while (!waitDone) {

            System.out.println("Sending First message");
            waitDone = true;

            startMessage = messageProtocol.processInput("RESEND_FIRST");
            startBytes = startMessage.getBytes();

            sndp = new DatagramPacket(
                startBytes, startBytes.length, nextHost, nextPort);

            try {
                datagramSocket.send(sndp);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            try {
                datagramSocket.receive(rcdp);
            } catch (SocketTimeoutException e) {
                waitDone = false;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

        }

        //Disable sockets timeout
        try {
            datagramSocket.setSoTimeout(0);
        } catch (SocketException e) {
            System.out.println("Socket not working");
            e.printStackTrace();
            return;
        }

        //Receive and send loop
        while (true) {

            //Create new message depending on input
            String input = new String(rcdp.getData());
            String output;

            try {
                output = messageProtocol.processInput(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Message given was not following format");
                e.printStackTrace();
                return;
            }

            if (output != null) {

                byte[] outData = output.getBytes();

                //Pack data and send it to next node
                sndp = new DatagramPacket(
                        outData, outData.length, nextHost, nextPort);

                try {
                    datagramSocket.send(sndp);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }


            //Create packet to receive in and then wait to receive new message
            rcdp = new DatagramPacket(new byte[100], 100);

            try {
                datagramSocket.receive(rcdp);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
