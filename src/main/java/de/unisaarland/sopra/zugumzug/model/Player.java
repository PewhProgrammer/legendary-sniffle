package de.unisaarland.sopra.zugumzug.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import de.unisaarland.sopra.TrackKind;

public class Player {

	private final int pid;
	private String name;
	private int score = 0;
	private int trackCounter = 0;
	private int sumRessourceCards = 0;
	private int sumMissions = 0;		// to know in the client how many missions the others have
	
	private final List<Mission> missionList = new ArrayList<Mission>();
	private final HashMap<TrackKind, Integer> ressourceMap = new HashMap<TrackKind, Integer>();
	
	public Player(int pid, String name) {
		this.pid = pid;
		this.name = name;
		
		ressourceMap.put(TrackKind.ALL, 0);
		ressourceMap.put(TrackKind.BLACK, 0);
		ressourceMap.put(TrackKind.BLUE, 0);
		ressourceMap.put(TrackKind.GREEN, 0);
		ressourceMap.put(TrackKind.ORANGE, 0);
		ressourceMap.put(TrackKind.RED, 0);
		ressourceMap.put(TrackKind.VIOLET, 0);
		ressourceMap.put(TrackKind.WHITE, 0);
		ressourceMap.put(TrackKind.YELLOW, 0);
	}
	
	public void incrementTrackCounter(final int i) {
		
		assert i > 0;
		
		this.trackCounter += i;
	}
	
	/**
	 * Updates the score with positive and negative values
	 * 
	 * @param i
	 */
	public void updateScore(final int i) {
		this.score += i;
	}
	
	/**
	 * Removes resource cards.
	 * 
	 * @param color
	 * TrackKind whose number should be decreased.
	 * 
	 * @param count
	 */
	public void removeCard(TrackKind color, int count) {
		assert(count >= 0);
		
		if(isCardRemovalValid(color, count)) {
			int newCount = ressourceMap.get(color) - count;
			ressourceMap.put(color, newCount);
			this.sumRessourceCards -= count;
		}
	}
	
	/**
	 * Tests whether count > 0 and weather the number of owned
	 * cards of an Color is greater or equals count (prevents
	 * the counter for a color to get lower 0.
	 * 
	 * @param color TrackKind
	 * @param count Integer
	 * 
	 * @return boolean
	 */
	public boolean isCardRemovalValid(TrackKind color, int count) {
		return count >= 0
				&& ressourceMap.get(color) >= count;
	}
	
	/**
	 * Adds resource cards.
	 * 
	 * @param color
	 * Color whose integer should be increased.
	 * @param i
	 * Integer
	 */
	public void giveRessourceCards(TrackKind color, int i) {
		assert(i > 0);
		
		int newCount = ressourceMap.get(color) + i;
		ressourceMap.put(color, newCount);
		this.sumRessourceCards += i;
	}
	
	/**
	 * Adds missions to the mission set.
	 * 
	 * @param missions
	 * Set of missions.
	 */
	public void giveMissions(Set<Mission> missions) {
		for(Mission mission : missions) {
			missionList.add(mission);
			sumMissions++;
		}
	}
	
	public int getPid() {
		return this.pid;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String newName) {
		this.name = newName;
	}
	
	public int getScore() {
		return this.score;
	}
	
	public int getTrackCounter() {
		return this.trackCounter;
	}
	
	public List<Mission> getMissionList() {
		return this.missionList;
	}
	
	public HashMap<TrackKind, Integer> getRessourceMap() {
		return this.ressourceMap;
	}
	
	public int getSumRessourceCards() {
		return this.sumRessourceCards;
	}
	public int getSumMissions() {
		return sumMissions;
	}
	
	/**
	 * To know in the client how many missions the others have
	 * 
	 * @param increm
	 */
	public void incrementSumMissions(int increm) {
		this.sumMissions += increm;
	}														
	
	/**
	 * To know in the client how many missions the others have
	 * 
	 * @param increm
	 */
	public void incrementRessourceCards(int increm){
		this.sumRessourceCards += increm;
	}
}
