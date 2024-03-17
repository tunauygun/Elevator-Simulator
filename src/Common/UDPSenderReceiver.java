package Common;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

/**
 * UDPSenderReceiver.java
 * <p>
 * Represents a utility class for sending and receiving data via UDP (User Datagram Protocol) sockets.
 *
 * @version 1.0, March 17, 2024
 */
public class UDPSenderReceiver {

    private DatagramPacket sendPacket, receivePacket;
    private DatagramSocket sendReceiveSocket;
    private int destinationPort;
    private int lastSenderPort;

    /**
     * Initializes a new UDPSenderReceiver instance.
     *
     * @param socketPort      The socket port for sending and receiving data.
     * @param destinationPort The destination port for sending data.
     */
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
     * Receives a response via UDP.
     *
     * @return The received data as a byte array.
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

        this.lastSenderPort = receivePacket.getPort();
        return Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength());
    }

    /**
     * Receives a SystemRequest object via UDP.
     *
     * @return The deserialized SystemRequest object.
     */
    public SystemRequest receiveSystemRequest() {
        return SystemRequest.deserializeRequest(receiveResponse());
    }

    /**
     * Sends a SystemRequest object via UDP.
     *
     * @param systemRequest The SystemRequest to be sent.
     */
    public void sendSystemRequest(SystemRequest systemRequest) {
        sendSystemRequest(systemRequest, destinationPort);
    }

    /**
     * Sends a SystemRequest object to a specific destination port via UDP.
     *
     * @param systemRequest   The SystemRequest to be sent.
     * @param destinationPort The destination port for sending the request.
     */
    public void sendSystemRequest(SystemRequest systemRequest, int destinationPort) {
        byte[] msg = SystemRequest.serializeRequest(systemRequest);
        sendResponse(msg, destinationPort);
    }

    /**
     * Sends a response via UDP to the default destination port.
     *
     * @param msg The response data to be sent.
     */
    public void sendResponse(byte[] msg) {
        sendResponse(msg, destinationPort);
    }

    /**
     * Sends a response via UDP to a specific destination port.
     *
     * @param msg             The response data to be sent.
     * @param destinationPort The destination port for sending the response.
     */
    public void sendResponse(byte[] msg, int destinationPort) {

        // Construct a datagram packet that is to be sent to a specified port
        // on a specified host.
        try {
            sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), destinationPort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Send the datagram packet via the send/receive socket.
        try {
            sendReceiveSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Gets the last sender port used for receiving data.
     *
     * @return The last sender port.
     */
    public int getLastSenderPort() {
        return lastSenderPort;
    }
}
