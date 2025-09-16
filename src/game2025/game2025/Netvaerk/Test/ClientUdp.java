package game2025.game2025.Netvaerk.Test;

import java.io.IOException;
import java.net.*;

public class ClientUdp {
    private static DatagramSocket clientSocket;
    private static byte[] receiveBuffer = new byte[1024];
    private static byte[] sendBuffer;

    public static void main(String[] args) {
        try {
            // Client listens on port 12000 for broadcasts
            clientSocket = new DatagramSocket(12000);

            Thread writeToServer = new Thread(ClientUdp::writeToServer);
            Thread readFromServer = new Thread(ClientUdp::readFromServer);

            writeToServer.start();
            readFromServer.start();

            System.out.println("Client started - listening on port 12000");
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    private static void readFromServer() {
        DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);

        while (true) {
            try {
                System.out.println("Waiting for broadcast...");
                clientSocket.receive(packet);
                System.out.println("Broadcast received!");

                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Message: " + message);

                // Reset buffer for next message
                packet.setLength(receiveBuffer.length);

            } catch (IOException e) {
                System.err.println("Error receiving: " + e.getMessage());
            }
        }
    }

    public static void writeToServer() {
        try {
            // Send initial message to server
            String testMessage = "HELLO FROM THE CLIENT";
            sendBuffer = testMessage.getBytes();

            DatagramPacket packet = new DatagramPacket(
                    sendBuffer,
                    sendBuffer.length,
                    InetAddress.getByName("10.10.130.163"), // Server IP
                    12005 // Server port
            );

            clientSocket.send(packet);
            System.out.println("Message sent to server");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}