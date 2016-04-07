package de.unisaarland.sopra.zugumzug.gui.view;

import java.util.HashMap;
import java.util.Random;

import de.unisaarland.sopra.zugumzug.model.GameState;
import de.unisaarland.sopra.zugumzug.model.Player;
import javafx.scene.paint.Color;

public class PlayerColor {
	HashMap<Integer, Color> playerColor;

	public PlayerColor(GameState gs) {
		Color[] color = { Color.GRAY, Color.BLUE, Color.INDIANRED, Color.DARKMAGENTA, Color.CORNFLOWERBLUE,
				Color.YELLOW, Color.DARKGREEN, Color.SPRINGGREEN, Color.HOTPINK };
		playerColor = new HashMap<Integer, Color>();
		int i = 0;
		HashMap<Integer, Player> playerMap = gs.getPlayerMap();
		for (Integer id : playerMap.keySet()) {
			if (i < 9) {
				playerColor.put(id, color[i]);
				i++;
			} else
				playerColor.put(id, giveRandomColor());
		}
	}

	public Color giveRandomColor() {
		Random r = new Random();
		Color c = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256), r.nextInt(256));
		if (playerColor.containsValue(c))
			return giveRandomColor();
		else
			return c;
	}

	public Color getColorFromID(int id) {
		return playerColor.get(id);
	}

	public HashMap<Integer, Color> getPlayerColor() {
		return this.playerColor;
	}

}
