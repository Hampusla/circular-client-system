import java.io.IOException;
import java.net.*;


public class UdpNode {

    public static void main(String argc[]) throws UnknownHostException {

        //Validate the number of arguments
        if (argc.length != 3) {
            System.err.println("Invalid number of arguments! Input should be on the following format: " +
                    "local-port next-host next-port");
            return;
        }

        //Validate (arg[0], arg[2]) and save as ports
        int port, nextPort;
        try {

            port = Integer.parseInt(argc[0]);
            nextPort = Integer.parseInt(argc[2]);
        } catch (NumberFormatException e) {
            System.err.println("Argument 1 and 3 must be integers!");
            return;
        }

        //Create InetAdress using arg[1]
        InetAddress host, nextHost;
        try {

            host = InetAddress.getLocalHost();
            nextHost = InetAddress.getByName(argc[1]);
        } catch (UnknownHostException e) {
            System.err.println("Error when creating a InetAdress from argc[1]! Exiting...");
            return;
        }

        System.out.println("Arguments validated. Creating Node");

        //Create MessageProtocol
        String socketID = Inet4Address.getLocalHost().getCanonicalHostName() + "," + port;
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
