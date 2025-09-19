package game2025.game2025.clientSide.GameEngine;

import game2025.game2025.clientSide.clientCommunication.ClientController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.List;

import static game2025.game2025.clientSide.GameEngine.GUI.*;

public class SetupClass {
    private GUI gui;
    private Stage primaryStage;

    public SetupClass(GUI gui, Stage primaryStage) {
        this.gui = gui;
        this.primaryStage = primaryStage;

        setupBoard(); //Do first

        List<Player> players = ClientController.initialRequest();
        //Need server(TCP) connection first
        setupPlayers(players);
        setupScorelist();

        ClientController.setUpDatagramSocket();
        //After run UDP socket
        Thread readFromServer = new Thread(() -> ClientController.runReadFromServer());
        readFromServer.start();

        ClientController.writeToServer();
    }

    private void setupScorelist() {
        StringBuffer b = new StringBuffer(100);
        for (Player p : players) {
            b.append(p+"\r\n");
        }
        gui.scoreList.setText(b.toString());
    }

    private void setupPlayers(List<Player> players) {
        players.sort(Comparator.comparing(p -> p.name));
        me = players.getLast(); //todo: Er det den bedste måde at få fat i ens egen player?

        for (Player player : players) {
            System.out.println(player);
            GUI.players.add(player);
            ImageView imageView = gui.getDirection(player.direction);
            gui.fields[player.getXpos()][player.getYpos()].setGraphic(imageView);
        }
    }

    private void setupBoard() {
        try {
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(0, 10, 0, 10));

            Text mazeLabel = new Text("Maze:");
            mazeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

            Text scoreLabel = new Text("Score:");
            scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

            gui.scoreList = new TextArea();

            GridPane boardGrid = new GridPane();

            image_wall = new Image(getClass().getResourceAsStream("Image/wall4.png"), size, size, false, false);
            image_floor = new Image(getClass().getResourceAsStream("Image/floor1.png"), size, size, false, false);

            hero_right = new Image(getClass().getResourceAsStream("Image/heroRight.png"), size, size, false, false);
            hero_left = new Image(getClass().getResourceAsStream("Image/heroLeft.png"), size, size, false, false);
            hero_up = new Image(getClass().getResourceAsStream("Image/heroUp.png"), size, size, false, false);
            hero_down = new Image(getClass().getResourceAsStream("Image/heroDown.png"), size, size, false, false);

            gui.fields = new Label[20][20];
            for (int j = 0; j < 20; j++) {
                for (int i = 0; i < 20; i++) {
                    switch (gui.board[j].charAt(i)) {
                        case 'w':
                            gui.fields[i][j] = new Label("", new ImageView(image_wall));
                            break;
                        case ' ':
                            gui.fields[i][j] = new Label("", new ImageView(image_floor));
                            break;
                        default:
                            throw new Exception("Illegal field value: " + gui.board[j].charAt(i));
                    }
                    boardGrid.add(gui.fields[i][j], i, j);
                }
            }
            gui.scoreList.setEditable(false);


            grid.add(mazeLabel, 0, 0);
            grid.add(scoreLabel, 1, 0);
            grid.add(boardGrid, 0, 1);
            grid.add(gui.scoreList, 1, 1);

            Scene scene = new Scene(grid, scene_width, scene_height);
            primaryStage.setScene(scene);
            primaryStage.show();

            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                switch (event.getCode()) {
                    case UP:
                        gui.playerMoved(0, -1, "up");
                        break;
                    case DOWN:
                        gui.playerMoved(0, +1, "down");
                        break;
                    case LEFT:
                        gui.playerMoved(-1, 0, "left");
                        break;
                    case RIGHT:
                        gui.playerMoved(+1, 0, "right");
                        break;
                    default:
                        break;
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}

