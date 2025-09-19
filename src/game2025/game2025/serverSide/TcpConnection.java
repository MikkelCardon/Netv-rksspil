package game2025.game2025.serverSide;

import game2025.game2025.clientSide.GameEngine.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import static game2025.game2025.serverSide.ServerController.*;

public class TcpConnection {
    private static final int PORT = 10_000;

    public static void tcpThread(){
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("SERVER SOCKET RUNNING");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println(GREEN+"Client connected from: " + clientSocket.getInetAddress() + RESET);
                Thread handleClient = new Thread(() -> handleClient(clientSocket));
                handleClient.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleClient(Socket socket){
        try (
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ){

            String joinMessage = (String) in.readObject();
            System.out.println(BLUE + "client join message: " + joinMessage + RESET);
            UdpServer.addClient(socket.getInetAddress());

            List<Player> players = new ArrayList<>();
            players.add(new Player("Player1", 2, 4, "up"));
            players.add(new Player("Player1", 2, 4, "up"));

            out.writeObject(players);
            out.flush();

            socket.close();
            System.out.println(RED + "Connection closed" + RESET);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

}
