package de.unisaarland.sopra.zugumzug.gui.view;

import java.util.ArrayList;
import java.util.HashMap;

import de.unisaarland.sopra.TrackKind;
import javafx.scene.control.Button;
import javafx.scene.image.Image;

public class RessourceCards {
	
	final HashMap<TrackKind, Image> buttonImageMap;
	ArrayList<Button> openRessourceButtons = new ArrayList<Button>();
	
	public RessourceCards() {
		
		buttonImageMap = new HashMap<TrackKind, Image>();
		buttonImageMap.put(TrackKind.RED, new Image(getClass().getResourceAsStream("../Images/openRed.png")));
		buttonImageMap.put(TrackKind.ORANGE, new Image(getClass().getResourceAsStream("../Images/openOrange.png")));
		buttonImageMap.put(TrackKind.YELLOW, new Image(getClass().getResourceAsStream("../Images/openYellow.png")));
		buttonImageMap.put(TrackKind.GREEN, new Image(getClass().getResourceAsStream("../Images/openGreen.png")));
		buttonImageMap.put(TrackKind.BLUE, new Image(getClass().getResourceAsStream("../Images/openBlue.png")));
		buttonImageMap.put(TrackKind.VIOLET, new Image(getClass().getResourceAsStream("../Images/openViolet.png")));
		buttonImageMap.put(TrackKind.WHITE, new Image(getClass().getResourceAsStream("../Images/openWhite.png")));
		buttonImageMap.put(TrackKind.BLACK, new Image(getClass().getResourceAsStream("../Images/openBlack.png")));
		buttonImageMap.put(TrackKind.ALL, new Image(getClass().getResourceAsStream("../Images/openLoco.png")));
		
		double y = 120.0;
		for(int i=0; i<5; i++) {
			openRessourceButtons.add(new Button());
			openRessourceButtons.get(i).setStyle("-fx-background-color: transparent");
			openRessourceButtons.get(i).setLayoutX(1214.0);
			openRessourceButtons.get(i).setLayoutY(y);
			y += 90.0;
		}
	}
	
	public HashMap<TrackKind, Image> getButtonImages() {
		return buttonImageMap;
	}
	
	public ArrayList<Button> getOpenRessourceButtons() {
		return openRessourceButtons;
	}
	
}