package game2025.game2025.clientSide.GameEngine;

import game2025.game2025.serverSide.ClientUdp;
import javafx.application.Application;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GUI extends Application {

	public static final int size = 20; 
	public static final int scene_height = size * 20 + 100;
	public static final int scene_width = size * 20 + 200;

	public static Image image_floor;
	public static Image image_wall;
	public static Image hero_right,hero_left,hero_up,hero_down;

	public static Player me;
	public static List<Player> players = new ArrayList<Player>();

	public static Label[][] fields;
	public static TextArea scoreList;
	
	public static String[] board = {    // 20x20
			"wwwwwwwwwwwwwwwwwwww",
			"w        ww        w",
			"w w  w  www w  w  ww",
			"w w  w   ww w  w  ww",
			"w  w               w",
			"w w w w w w w  w  ww",
			"w w     www w  w  ww",
			"w w     w w w  w  ww",
			"w   w w  w  w  w   w",
			"w     w  w  w  w   w",
			"w ww ww        w  ww",
			"w  w w    w    w  ww",
			"w        ww w  w  ww",
			"w         w w  w  ww",
			"w        w     w  ww",
			"w  w              ww",
			"w  w www  w w  ww ww",
			"w w      ww w     ww",
			"w   w   ww  w      w",
			"wwwwwwwwwwwwwwwwwwww"
	};

	
	// -------------------------------------------
	// | Maze: (0,0)              | Score: (1,0) |
	// |-----------------------------------------|
	// | boardGrid (0,1)          | scorelist    |
	// |                          | (1,1)        |
	// -------------------------------------------

	@Override
	public void start(Stage primaryStage) {
		try {
			SetupClass setupClass = new SetupClass(this, primaryStage);

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void updatePlayer(String name, int xDelta, int yDelta, String direction){
		for (Player player : players){
			if (player.name.equals(name)){
				if (board[player.ypos+=yDelta].charAt(player.xpos+=xDelta)=='w') {
					player.addPoints(-1);
				}
				else {
					Player p = getPlayerAt(player.xpos+=xDelta,player.ypos+=yDelta);
					if (p!=null) {
						player.addPoints(10);
						p.addPoints(-10);
					} else {

						fields[player.xpos][player.ypos].setGraphic(new ImageView(image_floor));
						player.setXpos(player.xpos+=xDelta);
						player.setYpos(player.ypos+=yDelta);


						if (direction.equals("right")) {
							fields[player.xpos][player.ypos].setGraphic(new ImageView(hero_right));
						};
						if (direction.equals("left")) {
							fields[player.xpos][player.ypos].setGraphic(new ImageView(hero_left));
						};
						if (direction.equals("up")) {
							fields[player.xpos][player.ypos].setGraphic(new ImageView(hero_up));
						};
						if (direction.equals("down")) {
							fields[player.xpos][player.ypos].setGraphic(new ImageView(hero_down));
						};
						player.addPoints(1);

					}
				}
				scoreList.setText(getScoreList());

			}
		}
	}
	public void playerMoved(int delta_x, int delta_y, String direction) {
		String sendMessage = "MOVE " + me.name + " " + delta_x + " " + delta_y + " " + direction;
		ClientUdp.setSendBuffer(sendMessage.getBytes());
		ClientUdp.writeToServer();
	}


	public static String getScoreList() {
		StringBuffer b = new StringBuffer(100);
		for (Player p : players) {
			b.append(p+"\r\n");
		}
		return b.toString();
	}

	public static Player getPlayerAt(int x, int y) {
		for (Player p : players) {
			if (p.getXpos()==x && p.getYpos()==y) {
				return p;
			}
		}
		return null;
	}

	public ImageView getDirection(String direction){
		return switch (direction){
			case "right" -> new ImageView(hero_right);
			case "left" -> new ImageView(hero_left);
			case "up" -> new ImageView(hero_up);
			case "down" -> new ImageView(hero_down);
			default -> null;
		};
	}

	
}

