package de.unisaarland.sopra.zugumzug.gui.view;

public class PlayerView {
	String name;
	String color;
	String points;
	String sumCards;
	String sumMission;

	public PlayerView(String name, String color, int points, int sumCards, int sumMission) {
		this.name = name;
		this.color = color;
		this.points = Integer.toString(points);
		this.sumCards = Integer.toString(sumCards);
		this.sumMission = Integer.toString(sumMission);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getPoints() {
		return points;
	}

	public void setPoints(String points) {
		this.points = points;
	}

	public String getSumCards() {
		return sumCards;
	}

	public void setSumCards(String sumCards) {
		this.sumCards = sumCards;
	}

	public String getSumMission() {
		return sumMission;
	}

	public void setSumMission(String sumMission) {
		this.sumMission = sumMission;
	}

}