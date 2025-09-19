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
                Thread handleClient = new Thread(() -> readFromClient(clientSocket));
                handleClient.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static List<ObjectOutputStream> outputStreams = new ArrayList<>();

    private static void readFromClient(Socket socket){
        try (
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ){

            while (true){
                String messageFromClient = (String) in.readObject();
                System.out.println(BLUE + "client message: " + messageFromClient + RESET);

                String[] command = messageFromClient.split(" ");
                String name = command[1];
                switch(command[0]){
                    case "JOIN" -> newPlayer(out, name);
                    case "MOVE" -> movePlayer();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    private static void newPlayer(ObjectOutputStream out, String joinName) {
        Player newPlayer = null;

        if (GameInformation.isValidName(joinName)){
            newPlayer = GameInformation.addNewPlayer(joinName);
        }else{
            new Exception("Det må du ikke hedde!!!");
        }
        try {
            outputStreams.add(out);
            StringBuilder sb = new StringBuilder();
            sb.append("JOINED ");
            for (Player serverPlayer : GameInformation.getServerPlayers()) {
                sb.append(serverPlayer.toString());
            }

            String toSend = sb.toString();

            for (ObjectOutputStream outputStream : outputStreams) {
                outputStream.flush();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
