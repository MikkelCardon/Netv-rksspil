package game2025.game2025.clientSide.clientCommunication;

import game2025.game2025.clientSide.GameEngine.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.List;

public class ClientController {
    private static String serverIp = "localhost";
    private static final int PORT_IN = 10_000;
    private static final int PORT_OUT = 10_000;

    public static List<Player> initialRequest(){
        List<Player> players;

        try(Socket clientSocket = new Socket(serverIp, PORT_OUT)) {

            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            out.writeObject("JOIN");
            out.flush();
            System.out.println("TCP - Initial Request...");

            players = (List<Player>) in.readObject();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return players;
    }

    private static DatagramSocket clientSocket;
    private static byte[] receiveBuffer = new byte[1024];
    private static byte[] sendBuffer = new byte[1024];

    public static void runReadFromServer() {
        try{
            DatagramPacket packet = new DatagramPacket(receiveBuffer, 0, receiveBuffer.length);

            while(true){
                System.out.println("UDP - Listening on port: " + PORT_IN);
                clientSocket.receive(packet);
                String message = new String(packet.getData(), 0 , packet.getLength());
                System.out.println(message);
                receiveBuffer = new byte[1024];
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    public static void setUpDatagramSocket() {
        if (clientSocket == null){
            try {
                clientSocket = new DatagramSocket(PORT_IN);
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
