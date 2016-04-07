package de.unisaarland.sopra.zugumzug.gui.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class PlayerGui {
	private final IntegerProperty playerID;
	private final Color color;
	private final StringProperty name;
	private final IntegerProperty points;
	private final IntegerProperty sumCards;
	private final IntegerProperty sumMission;

	public PlayerGui(String name, int pid, Color color, int points, int sumCards, int sumMission) {
		this.color = color;
		this.playerID = new SimpleIntegerProperty(pid);
		this.name = new SimpleStringProperty(name);
		this.points = new SimpleIntegerProperty(points);
		this.sumCards = new SimpleIntegerProperty(sumCards);
		this.sumMission = new SimpleIntegerProperty(sumMission);
	}

	public int getPlayerID(){
		return playerID.get();
	}
	
	public IntegerProperty getPlayerIDProperty(){
		return playerID;
	}
	
	public String getName() {
		return name.get();
	}
	
	public void setName(String name) {
		this.name.set(name);
	}
	
	public StringProperty nameProperty(){
		return name;
	}
	
	public int getPoints() {
		return points.get();
	}
	
	public void setPoints(int points) {
		this.points.set(points);
	}
	
	public IntegerProperty getPointsProperty(){
		return points;
	}
	public int getSumCards() {
		return sumCards.get();
	}

	public void setSumCards(int sumCards) {
		this.sumCards.set(sumCards);
	}
	
	public IntegerProperty getSumCardsProperty(){
		return sumCards;
	}
	
	public int getSumMission() {
		return sumMission.get();
	}

	public void setSumMission(int sumMission) {
		this.sumMission.set(sumMission);
	}
	
	public IntegerProperty getSumMissionsProperty(){
		return sumMission;
	}
	
	public Color getColor(){
		return color;
	}

}