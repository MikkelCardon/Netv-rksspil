package game2025.game2025.clientSide.GameEngine;

import game2025.game2025.clientSide.clientCommunication.ClientController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

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

	public static void playerMoveRequest(int delta_x, int delta_y, String direction) {
		ClientController.sendMoveRequest(delta_x, delta_y, direction);
	}

	public static void movePlayer(int delta_x, int delta_y, String direction, Player player){
		int mePoints = me.point;
		player.direction = direction;
		int x = player.getXpos(),y = player.getYpos();

		if (board[y+delta_y].charAt(x+delta_x)=='w') {
			player.addPoints(-1);
		}
		else {
			Player p = getPlayerAt(x+delta_x,y+delta_y);
			if (p!=null) {
				player.addPoints(10);
				p.addPoints(-10);
			} else {
				player.addPoints(1);

				int finalX = x;
				int finalY = y;
				Platform.runLater(()->{
					fields[finalX][finalY].setGraphic(new ImageView(image_floor));
				});

				x+=delta_x;
				y+=delta_y;

				player.setXpos(x);
				player.setYpos(y);
			}
		}
		int finalX1 = x;
		int finalY1 = y;
		Platform.runLater(()->{
			fields[finalX1][finalY1].setGraphic(getDirection(direction));
			scoreList.setText(getScoreList());
		});
		if (me.getPoint() != mePoints){
			ClientController.sendMove();
		}
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

	public static ImageView getDirection(String direction){
		return switch (direction){
			case "right" -> new ImageView(hero_right);
			case "left" -> new ImageView(hero_left);
			case "up" -> new ImageView(hero_up);
			case "down" -> new ImageView(hero_down);
			default -> null;
		};
	}

	
}

