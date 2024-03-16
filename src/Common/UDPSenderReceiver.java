package Common;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class UDPSenderReceiver {

    private DatagramPacket sendPacket, receivePacket;
    private DatagramSocket sendReceiveSocket;
    private int destinationPort;
    private int lastSenderPort;

    public UDPSenderReceiver(int socketPort, int destinationPort) {
        try {
            sendReceiveSocket = new DatagramSocket(socketPort);
        } catch (SocketException se) {   // Can't create the socket.
            se.printStackTrace();
            System.exit(1);
        }
        this.destinationPort = destinationPort;
        this.lastSenderPort = 0;
    }

    /**
     * Uses the sendReceiveSocket to receive a packet from
     * the IntermediateHost
     *
     * @return sdfgdsgfsdgfs
     */
    public byte[] receiveResponse() {

        // Construct a DatagramPacket for receiving packets up
        // to 1000 bytes long (the length of the byte array).
        byte data[] = new byte[1000];
        receivePacket = new DatagramPacket(data, data.length);

        try {
            // Block until a datagram is received via sendReceiveSocket.
            sendReceiveSocket.receive(receivePacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Print the detail of the received datagram.
//        System.out.println("Packet received:");
//        System.out.print("\tString -> ");
//        System.out.println(new String(receivePacket.getData(), 0, receivePacket.getLength()));
//        System.out.print("\tByte -> ");
//        for (int j = 0; j < receivePacket.getLength(); j++) {
//            System.out.print(receivePacket.getData()[j] + " ");
//        }

        this.lastSenderPort = receivePacket.getPort();
        return Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength());
    }

    public SystemRequest receiveSystemRequest() {
        return SystemRequest.deserializeRequest(receiveResponse());
    }

    /**
     * Uses the sendReceiveSocket to receive a packet from the
     * IntermediateHost with the given packet message
     *
     * @param systemRequest The message that will be sent to the IntermediateHost
     */
    public void sendSystemRequest(SystemRequest systemRequest) {
        sendSystemRequest(systemRequest, destinationPort);
    }

    public void sendSystemRequest(SystemRequest systemRequest, int destinationPort) {
        byte[] msg = SystemRequest.serializeRequest(systemRequest);
//        if(systemRequest.getType() != SystemRequestType.ADD_NEW_REQUEST){
//            LogPrinter.print(systemRequest.getId(), "Sending request: " + systemRequest);
//        }else{
//            System.out.println("Sending request: " + systemRequest);
//        }
        sendResponse(msg, destinationPort);
    }

    public void sendResponse(byte[] msg) {
        sendResponse(msg, destinationPort);
    }

    public void sendResponse(byte[] msg, int destinationPort) {

        // Construct a datagram packet that is to be sent to a specified port
        // on a specified host.
        try {
            sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), destinationPort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Print the details of the packet
//        System.out.println("Sending packet:");
//        System.out.print("\tString -> ");
//        System.out.println(new String(sendPacket.getData(), 0, sendPacket.getLength()));
//        System.out.print("\tByte -> ");
//        for (byte b : msg) {
//            System.out.print(b + " ");
//        }
//        System.out.println();

        // Send the datagram packet to the IntermediateHost via the send/receive socket.
        try {
            sendReceiveSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    public int getLastSenderPort() {
        return lastSenderPort;
    }
}
