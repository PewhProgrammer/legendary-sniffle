package de.unisaarland.sopra.zugumzug.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GameState {

	private int maxHandSize;
	private int maxTracks;

	private final HashMap<Integer, Player> playerMap = new HashMap<Integer, Player>();

	private GameBoard gameBoard;
	private RessourceManager ressourceManager;
	private MissionManager missionManager;
	
	private int currentPlayerId = -1;
	
	public int getCurrentPlayerId() {
		return currentPlayerId;
	}

	public void setCurrentPlayerId(int currentPlayerId) {
		this.currentPlayerId = currentPlayerId;
	}

	public GameState() {
	}

	public int getMaxHandSize() {
		return maxHandSize;
	}

	public int getMaxTracks() {
		return maxTracks;
	}

	public HashMap<Integer, Player> getPlayerMap() {
		return playerMap;
	}

	public Set<Player> getPlayerSet() {
		return new HashSet<Player>(this.getPlayerMap().values());
	}

	public GameBoard getGameBoard() {
		return gameBoard;
	}

	public RessourceManager getRessourceManager() {
		return ressourceManager;
	}

	public MissionManager getMissionManager() {
		return missionManager;
	}

	public void addPlayerInPlayerMap(Player p) {
		playerMap.put(p.getPid(), p);
	}

	public void setMaxHandSize(int maxHandSize) {
		this.maxHandSize = maxHandSize;
	}

	public Player getPlayerFromId(final int pid) {
		return playerMap.get(pid);
	}

	public void setMaxTracks(final int maxTracks) {
		this.maxTracks = maxTracks;
	}

	public void setGameBoard(GameBoard gameBoard) {
		this.gameBoard = gameBoard;
	}

	public void setRessourceManager(RessourceManager ressourceManager) {
		this.ressourceManager = ressourceManager;
	}

	public void setMissionManager(MissionManager missionManager) {
		this.missionManager = missionManager;
	}

}
