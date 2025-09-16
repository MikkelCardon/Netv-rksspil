package game2025.game2025.Netvaerk;

import java.io.IOException;
import java.net.*;

public class ClientUdp {
    private static DatagramSocket clientSocket;
    public static void main(String[] args) {
        try {
            clientSocket = new DatagramSocket(12_005);

            Thread writeToServer = new Thread(ClientUdp::writeToServer);
            Thread readFromServer = new Thread(ClientUdp::readFromServer);

            writeToServer.start();

        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    private static void readFromServer() {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);
        try {
            clientSocket.setBroadcast(true);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        while (true){
            try {
                clientSocket.receive(packet);
                String message = new String(packet.getData(), 0 , packet.getLength());
                buffer = new byte[1024];
                System.out.println(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void writeToServer(){
        byte[] buffer = new byte[1024];
        try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("172.25.128.1"), 12_000);
            String testMessage = "HELLO FROM THE CLIENT";
            buffer = testMessage.getBytes();
            clientSocket.send(packet);
            buffer = new byte[1024];
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
