package game2025.game2025.Netvaerk;
import game2025.game2025.GUI;

import game2025.game2025.GUI;
import jdk.swing.interop.SwingInterOpUtils;

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
            try {
                System.out.println("Waiting");
                clientSocket.receive(packet);
                System.out.println("Packet recieved");
                String message = new String(packet.getData(), 0 , packet.getLength());
                receiveBuffer = new byte[1024];
                System.out.println(message);
                String[] splitMessage = message.split(" ");
                if (splitMessage[1].equals("Move")){
                    movePlayer(splitMessage[1],Integer.getInteger(splitMessage[2]), Integer.getInteger(splitMessage[3]), splitMessage[4]);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void movePlayer(String name, int xDelta, int yDelta, String direction){
        GUI.updatePlayer(name, xDelta, yDelta, direction);
    }

    public static void writeToServer(){
        try {
            String testMessage = "HELLO FROM THE CLIENT";
            sendBuffer = testMessage.getBytes();
            DatagramPacket packet = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getByName("10.10.138.143"), 10_005);
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
