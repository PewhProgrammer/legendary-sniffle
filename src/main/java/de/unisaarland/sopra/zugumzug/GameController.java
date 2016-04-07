package de.unisaarland.sopra.zugumzug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.actions.Action;
import de.unisaarland.sopra.zugumzug.actions.ActionIdHelper;
import de.unisaarland.sopra.zugumzug.actions.client.*;
import de.unisaarland.sopra.zugumzug.actions.server.*;
import de.unisaarland.sopra.zugumzug.model.GameState;
import de.unisaarland.sopra.zugumzug.model.Player;
import de.unisaarland.sopra.zugumzug.model.Track;
import de.unisaarland.sopra.zugumzug.server.ServerGameController;

public abstract class GameController {

	protected final static int GAMEOVER_RANGE = 3;
	protected final static int OPENCCARDS = 5;
	protected final static int LONGEST_TRACK_POINTS = 10;
	protected final static int GAMESTART_CARDS_TO_PLAYER = 4;
	protected final static Integer[] LENGHT_TO_POINTS = {1, 2, 4, 7, 10, 15};
	protected final static int FAILURES_TO_KICK = 4;
	protected final static int MISSION_AMOUNT_GAMESTART = 3;

	protected final ActionIdHelper idHelper = new ActionIdHelper();
	protected final static List<Integer> endsTurn
			= Arrays.asList(ReturnMissions.actionId,
								PayTunnel.actionId,
								TunnelNotBuilt.actionId,
								TrackBuilt.actionId,
								TunnelBuilt.actionId,
								TookMissions.actionId);

	protected Validator validator;
	protected GameState gameState;
	protected final MapFactory mapFactory = new MapFactory();
	protected Action lastValidAction = null;

	protected final List<Integer> activePlayers = new ArrayList<Integer>();

	protected int currentPlayerIndex = 0;

	public GameController() {
	}
	
	abstract public void runGame();
	abstract protected void exit();
	abstract protected void deletePlayer(int pid);
	
	protected void setup(Validator validator, GameState gameState) {
		this.validator = validator;
		this.gameState = gameState;
	}
	
	public boolean isGameOver() {
		boolean isPlayerLeft = activePlayers.size() >= 1;
		boolean isMaxTrackReached = false;
		int maxTrackLenght = gameState.getMaxTracks();
		for (int pid : activePlayers) {
			Player p = gameState.getPlayerFromId(pid);
			if (p.getTrackCounter() >= maxTrackLenght - GAMEOVER_RANGE) {
				isMaxTrackReached = true;
			}
		}
		return !isPlayerLeft || isMaxTrackReached;
	}
	
	/**
	 * isTurnOver() tests if the currentAction ends a turn
	 * 
	 * A turn is over if a player:
	 * - returns missions
	 * - pays a tunnel
	 * - takes a TrackKind.ALL card except
	 * 		the closed deck is empty and all remaining open cards are locomotives
	 * 		(areTheOnlyRemainingOpenCardsLocomotives())
	 * - builds a track except it is a tunnel
	 */
	public boolean isTurnOver(Action currentAction) {
		
		final int curActionId = currentAction.accept(idHelper);
		
		if (ServerGameController.endsTurn.contains(curActionId)) {
			return true;
		} else if (curActionId == TakeCard.actionId || curActionId == TookCard.actionId) {
			
			TrackKind tk = (curActionId == TakeCard.actionId) ? 
					((TakeCard) currentAction).kind : ((TookCard) currentAction).kind;
			if (tk == TrackKind.ALL) {
				return !(gameState.getRessourceManager().areTheOnlyRemainingOpenCardsLocomotives()
						&& lastValidAction == null);
			} else {
				return lastValidAction != null;
			}
		} else if (curActionId == TakeSecretCard.actionId || curActionId == TookSecretCard.actionId) {
			return lastValidAction != null;
		} else if (curActionId == BuildTrack.actionId) {
			short tid = ((BuildTrack) currentAction).tid;
			Track t = gameState.getGameBoard().getTrackFromId(tid);
			if(t.isTunnel()){
				return lastValidAction instanceof TunnelNotBuilt;
			} else {
				return true;
			}
		
		} else {
			return false;
		}
		
	}
	
	/**
	 * 
	 * @param costs
	 * @return the points, which you earn for the build a track
	 *  of the the parameter costs
	 */
	protected int getPoitsOfLenght(int costs) {
		return LENGHT_TO_POINTS[costs -1];
	}

	public void updateCurrentPlayerIndex() {
		if (currentPlayerIndex == activePlayers.size() - 1) {
			currentPlayerIndex = 0;
		} else {
			currentPlayerIndex++;
		}
		int pid = activePlayers.get(currentPlayerIndex);
		gameState.setCurrentPlayerId(pid);
	}
	
	public void decrementCurrentPlayerIndex(){
		if (currentPlayerIndex == 0){
			currentPlayerIndex = activePlayers.size() -1;
		} else {
			currentPlayerIndex -= 1;
		}
	}

	public int getCurrentPlayerId() {
		return activePlayers.get(currentPlayerIndex);
	}

	public Action getLastValidAction() {
		return this.lastValidAction;
	}

	public void setCurrentPlayerIndex(int currentPlayerIndex) {
		this.currentPlayerIndex = currentPlayerIndex;
	}

	public void setLastValidAction(Action action) {
		this.lastValidAction = action;
	}

	public List<Integer> getActivePlayers() {
		return this.activePlayers;
	}

	public int getCurrentPlayerIndex() {
		return this.currentPlayerIndex;
	}
	
	protected void printRessourceMap(Player p) {
		System.out.print("Map: ");
		for (TrackKind k : TrackKind.values()) {
			System.out.print(k + "=" + p.getRessourceMap().get(k) + " ");		
		}
		System.out.println();
	}

}
