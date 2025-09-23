package game2025.game2025.serverSide;

import game2025.game2025.clientSide.GameEngine.Player;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static game2025.game2025.serverSide.ServerController.*;

public class TcpConnection {
    private static final int PORT = 10_000;
    private static final Object lock = new Object();

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
    private static List<DataOutputStream> outputStreams = new ArrayList<>();

    private static void readFromClient(Socket socket){
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            ){

            while (true){
                String messageFromClient = in.readLine();
                System.out.println(BLUE + "client message: " + messageFromClient + RESET);

                String[] command = messageFromClient.split(" ");
                String name = command[1];
                synchronized (lock){
                    switch(command[0]){
                        case "JOIN" -> newPlayer(out, name);
                        case "MOVE_REQUEST" -> movePlayer(command);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private static void newPlayer(DataOutputStream out, String joinName) {
        Player newPlayer = null;

        if (GameInformation.isValidName(joinName)){
            newPlayer = GameInformation.addNewPlayer(joinName);
            System.out.println(newPlayer);
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

            sendToClients(toSend);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void sendToClients(String toSend) throws IOException {
        for (DataOutputStream outputStream : outputStreams) {
            outputStream.writeBytes(toSend + "\n");
        }
    }

    private static void movePlayer(String[] command) {
        command[0] = "MOVE";

        StringBuilder sb = new StringBuilder();
        for (String s : command) {
            sb.append(s+" ");
        }
        String out = sb.toString();
        for (DataOutputStream outputStream : outputStreams) {
            try {
                outputStream.writeBytes(out + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
