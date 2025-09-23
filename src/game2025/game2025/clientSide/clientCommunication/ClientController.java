package game2025.game2025.clientSide.clientCommunication;

import game2025.game2025.clientSide.GameEngine.GUI;
import game2025.game2025.clientSide.GameEngine.Player;
import game2025.game2025.clientSide.GameEngine.SetupClass;

import java.io.*;
import java.net.*;

public class ClientController {
    private static String serverIp = "localhost";
    private static final int PORT_IN = 10_000;
    private static final int PORT_OUT = 10_000;
    private static final String NAME = "Flemming";

    private static boolean isJoined = false;
    private static Socket clientSocket;
    private static BufferedReader in;
    private static DataOutputStream out;

    private static Object lock = new Object();

    public static void initialRequest(){
        try{
            clientSocket = new Socket(serverIp, PORT_OUT);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new DataOutputStream(clientSocket.getOutputStream());

            out.writeBytes("JOIN " + NAME + "\n");
            //out.flush();
            //System.out.println("TCP - Initial Request...");

            while (true){
                //System.out.println("Waiting for server...");
                String message = in.readLine();
                //System.out.println(message);

                String[] splittedMessage = message.split(" ");
                String function = splittedMessage[0];

                switch (function){
                    case "JOINED" -> addPlayer(splittedMessage);
                    case "MOVE" -> movePlayer(splittedMessage);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addPlayer(String[] splittedMessage) {
        if (isJoined){
            int lastIndex = splittedMessage.length-1;
            String string = splittedMessage[lastIndex];
            Player player = stringToPlayerMapper(string);
            SetupClass.setupPlayer(player);
        }else{
            isJoined = true;
            for (int i = 1; i < splittedMessage.length; i++) {
                Player player = stringToPlayerMapper(splittedMessage[i]);
                if (player.getName().equals(NAME)){
                    GUI.me = player;
                }
                SetupClass.setupPlayer(player);
            }
        }
        SetupClass.setupScorelist();
    }

    private static Player stringToPlayerMapper(String string){
        String trimedString = string.substring(1, string.length()-1);
        String[] stats = trimedString.split(",");

        Player player = new Player(
                stats[0],
                Integer.parseInt(stats[1]),
                Integer.parseInt(stats[2]),
                stats[3],
                Integer.parseInt(stats[4])
        );
        return player;
    }

    public static void sendMoveRequest(int deltaX, int deltaY, String direction) {
        Player me = GUI.me;
        String messageOut = "MOVE_REQUEST "+me.getName()+" "+deltaX+" "+deltaY+" "+direction;
        try {
            //System.out.println(messageOut);
            synchronized (lock){
                out.writeBytes(messageOut + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendMove() {
        String messageOut = "MOVE "+GUI.me.toString();
        try {
            //System.out.println(messageOut);
            synchronized (lock){
                out.writeBytes(messageOut + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void movePlayer(String[] splittedMessage){
        Player playerToMove = null;
        for (Player player : GUI.players) {
            if (player.getName().equals(splittedMessage[1])){
                playerToMove = player;
                break;
            }
        }
        int x = Integer.parseInt(splittedMessage[2]);
        int y = Integer.parseInt(splittedMessage[3]);

        GUI.movePlayer(x, y, splittedMessage[4], playerToMove);
    }
}
