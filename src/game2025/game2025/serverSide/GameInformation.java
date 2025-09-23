package game2025.game2025.serverSide;

import game2025.game2025.clientSide.GameEngine.Player;

import java.util.*;

public class GameInformation {
    private static List<Player> serverPlayers = new ArrayList<>();

    public static List<Player> getServerPlayers() {
        return serverPlayers;
    }

    public static boolean isValidName(String name){
        return !getNames().contains(name);
    }

    public static Player addNewPlayer(String name){
        Random random = new Random();
        int randomNumber = random.nextInt(3);

        int xPosition = xPos.get(randomNumber);
        int yPosition = yPos.get(randomNumber);

        Player newPlayer = new Player(name, xPosition, yPosition, "up", 0);
        serverPlayers.add(newPlayer);
        return newPlayer;
    }

    private static List<Integer> xPos = new ArrayList<>(List.of(3, 7, 17));
    private static List<Integer> yPos = new ArrayList<>(List.of(3, 8, 12));

    public static List<String> getNames(){
        return serverPlayers
                .stream()
                .map(Player::getName)
                .toList();
    }
}


