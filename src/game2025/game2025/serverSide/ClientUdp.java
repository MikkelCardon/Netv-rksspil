package game2025.game2025.serverSide;

import java.io.IOException;
import java.net.*;

public class ClientUdp {
    private static DatagramSocket clientSocket;
    private static byte[] receiveBuffer = new byte[1024];
    private static byte[] sendBuffer = new byte[1024];

    public static void main(String[] args) {
        try {
            clientSocket = new DatagramSocket(10_000);

            Thread writeToServer = new Thread(ClientUdp::writeToServer);
            Thread readFromServer = new Thread(ClientUdp::readFromServer);

            writeToServer.start();
            readFromServer.start();
            System.out.println("threads started");

        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    private static void readFromServer() {
        DatagramPacket packet = new DatagramPacket(receiveBuffer, 0, receiveBuffer.length);

        while (true){
            System.out.println("test");
            try {
                clientSocket.receive(packet);
                System.out.println("Packet recieved");
                String message = new String(packet.getData(), 0 , packet.getLength());
                receiveBuffer = new byte[1024];
                System.out.println(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void writeToServer(){
        try {
            String testMessage = "PLAYER 2 MOVE";
            sendBuffer = testMessage.getBytes();
            DatagramPacket packet = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getByName("localhost"), 10_005);
            clientSocket.send(packet);
            System.out.println("Message sent");
            sendBuffer = new byte[1024];
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
