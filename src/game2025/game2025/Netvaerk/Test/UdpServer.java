package game2025.game2025.Netvaerk.Test;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UdpServer {
    private static DatagramSocket datagramSocket;
    private static byte[] receiveBuffer = new byte[1024];
    private static BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        try {
            // Server listens on port 12005
            datagramSocket = new DatagramSocket(12005);
            datagramSocket.setBroadcast(true); // Enable broadcast on server socket

            Thread readThread = new Thread(UdpServer::readFromClient);
            Thread broadcastThread = new Thread(UdpServer::broadcastToClients);

            readThread.start();
            broadcastThread.start();

            System.out.println("Server started on port 12005");

        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    private static void readFromClient() {
        System.out.println("Reading thread started");
        DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);

        while (true) {
            try {
                datagramSocket.receive(packet);
                System.out.println("Message received from client");

                String messageFromClient = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Client message: " + messageFromClient);

                // Add to queue for broadcasting
                messageQueue.offer(messageFromClient);

                // Reset packet length for next receive
                packet.setLength(receiveBuffer.length);

            } catch (IOException e) {
                System.err.println("Error reading from client: " + e.getMessage());
            }
        }
    }

    private static void broadcastToClients() {
        System.out.println("Broadcast thread started");

        while (true) {
            try {
                // Wait for message in queue (blocking call)
                String messageToSend = messageQueue.take();
                System.out.println("Broadcasting: " + messageToSend);

                byte[] sendBuffer = messageToSend.getBytes();
                DatagramPacket broadcastPacket = new DatagramPacket(
                        sendBuffer,
                        sendBuffer.length,
                        InetAddress.getByName("255.255.255.255"), // Broadcast address
                        12000 // Client listening port
                );

                datagramSocket.send(broadcastPacket);
                System.out.println("Broadcast sent successfully");

            } catch (InterruptedException e) {
                System.err.println("Broadcast thread interrupted");
                break;
            } catch (IOException e) {
                System.err.println("Error broadcasting: " + e.getMessage());
            }
        }
    }
}