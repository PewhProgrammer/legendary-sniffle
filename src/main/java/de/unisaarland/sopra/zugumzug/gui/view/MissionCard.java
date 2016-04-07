package de.unisaarland.sopra.zugumzug.gui.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.unisaarland.sopra.zugumzug.model.City;
import de.unisaarland.sopra.zugumzug.model.Mission;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MissionCard {
	
	HashMap<Short, City> cityMap;
	AnchorPane missionCardAnchor = new AnchorPane();
	Image missionStaticBackground = new Image(getClass().getResourceAsStream(("../Images/missionCardBackground.png")));
	ImageView missionCard = new ImageView(missionStaticBackground);
	
	List<AnchorPane> drawnMissionCardAnchors = new ArrayList<AnchorPane>();
	List<Button> drawnMissionCardButtons = new ArrayList<Button>();
	
	public MissionCard(Mission mission, HashMap<Short, City> cityMap) {
		
		this.cityMap = cityMap;
		missionCardAnchor.setPrefSize(200.0, 105.0);
		missionCardAnchor.getChildren().add(missionCard);
		Text cityA = new Text(cityMap.get(mission.getCityA()).getName());
		Text cityB = new Text(cityMap.get(mission.getCityB()).getName());
		Text points = new Text(Integer.toString(mission.getPoints()));
		cityA.setFont(Font.font("Yu Mincho Regular", 20));
		cityB.setFont(Font.font("Yu Mincho Regular", 20));
		points.setFont(Font.font("Yu Mincho Regular", 50));
		missionCardAnchor.getChildren().add(cityA);
		missionCardAnchor.getChildren().add(cityB);
		missionCardAnchor.getChildren().add(points);
		cityA.setLayoutX(175.0);
		cityA.setLayoutY(35.0);
		cityB.setLayoutX(175.0);
		cityB.setLayoutY(155.0);
		points.setLayoutX(40.0);
		points.setLayoutY(155.0);
	}
	
	public void setDrawnMissionCards(List<Mission> missionList) {
		drawnMissionCardAnchors = new ArrayList<AnchorPane>();
		drawnMissionCardButtons.clear();
		
		for(int i=0; i<missionList.size(); i++) {
			MissionCard missionCard = new MissionCard(missionList.get(i), cityMap);
			missionCard.getAnchorPane().setEffect(new DropShadow(25, Color.BLACK));
			drawnMissionCardAnchors.add(missionCard.getAnchorPane());
			
			Button chooseMissionButton = new Button();
			chooseMissionButton.setStyle("-fx-background-color: transparent");
			chooseMissionButton.setPrefSize(340.0, 180.0);
			chooseMissionButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> missionCard.getAnchorPane().setEffect(new DropShadow(25, Color.GOLD)));
			drawnMissionCardButtons.add(chooseMissionButton);
		}
	}
	
	public AnchorPane getAnchorPane() {
		return this.missionCardAnchor;
	}
	
	public List<AnchorPane> getDrawnMissionCardAnchors() {
		return drawnMissionCardAnchors;
		
	}
	
	public List<Button> getDrawnMissionCardButtons() {
		return drawnMissionCardButtons;
		
	}
}