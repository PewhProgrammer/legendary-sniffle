package de.unisaarland.sopra.zugumzug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.actions.Action;
import de.unisaarland.sopra.zugumzug.actions.ActionIdHelper;
import de.unisaarland.sopra.zugumzug.actions.ActionValidatorVisitor;
import de.unisaarland.sopra.zugumzug.actions.client.*;
import de.unisaarland.sopra.zugumzug.actions.server.Broadcast;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.AdditionalCost;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.Conceal;
import de.unisaarland.sopra.zugumzug.model.GameBoard;
import de.unisaarland.sopra.zugumzug.model.GameState;
import de.unisaarland.sopra.zugumzug.model.Mission;
import de.unisaarland.sopra.zugumzug.model.Player;
import de.unisaarland.sopra.zugumzug.model.RessourceManager;
import de.unisaarland.sopra.zugumzug.model.Track;

public class Validator implements ActionValidatorVisitor {
	
	private class NullObj {
		public static final int actionId = -10;
	}
	
	private static final HashMap<Integer, List<Integer>> ruleMap = new HashMap<>();
	
	// Determines what is allowed as a next Action
	// put lastValidAction in this map to get all
	// valid following actions
	static {
		// -----TakeCard
		List<Integer> listTakeCard = new ArrayList<>();
		listTakeCard.add(TakeCard.actionId);
		listTakeCard.add(TakeSecretCard.actionId);
		ruleMap.put(TakeCard.actionId, listTakeCard);
		
		// -----TakeSecretCard
		List<Integer> listTakeSecretCard = new ArrayList<>();
		listTakeSecretCard.add(TakeCard.actionId);
		listTakeSecretCard.add(TakeSecretCard.actionId);
		ruleMap.put(TakeSecretCard.actionId, listTakeSecretCard);
		
		// -----GetMissions
		List<Integer> listGetMissions = new ArrayList<>();
		listGetMissions.add(ReturnMissions.actionId);
		ruleMap.put(GetMissions.actionId, listGetMissions);
		
		// -----BuildTrack
		// Happens only if Track is a tunnel
		List<Integer> listBuildTrack = new ArrayList<>();
		listBuildTrack.add(AdditionalCost.actionId);
		ruleMap.put(BuildTrack.actionId, listBuildTrack);
		
		// -----AdditionalCost
		List<Integer> listAdditionalCost = new ArrayList<>();
		listAdditionalCost.add(PayTunnel.actionId);
		ruleMap.put(AdditionalCost.actionId, listAdditionalCost);
		
		// -----NullObj
		// This is just a dummy object when lastValidAction
		// is null, to be able to add this state in the map
		List<Integer> listNullObj = new ArrayList<>();
		listNullObj.add(BuildTrack.actionId);
		listNullObj.add(GetMissions.actionId);
		listNullObj.add(TakeCard.actionId);
		listNullObj.add(TakeSecretCard.actionId);
		ruleMap.put(NullObj.actionId, listNullObj);
	}
	

	private final GameState gameState;
	private final GameBoard gameBoard;
	private final RessourceManager rm;
	private final ActionIdHelper idHelper = new ActionIdHelper();

	public Validator(GameState gameState) {
		this.gameState = gameState;
		this.gameBoard = gameState.getGameBoard();
		this.rm = gameState.getRessourceManager();
	}
	
	public boolean test(final int curPid, Action lastAction, Action currentAction) {

		if (matchesPidWithAction(curPid, currentAction)) {
			
			int actionId = lastAction == null ? NullObj.actionId : lastAction.accept(idHelper);
			List<Integer> actions = ruleMap.get(actionId);
			
			if (actions != null
					&& actions.contains(currentAction.accept(idHelper))) {
				return currentAction.accept(this, lastAction);
			} else {
				return false;
			}
			
		}
		
		return false;
	}
	
	private boolean matchesPidWithAction(final int curPid, Action action) {
		
		if (action instanceof Conceal) {
			return ((Conceal) action).pid == curPid;
		} else if (action instanceof ClientAction) {
			return ((ClientAction) action).pid == curPid;
		} else {
			throw new IllegalArgumentException();
		}
		
	}
	
	@Override
	public Boolean visit(BuildTrack a, Action b) {

		if (b == null) {
			
			HashMap<Short, Track> trackMap = gameBoard.getTrackMap();
			Track t = trackMap.get(a.tid);
			Player p = gameState.getPlayerFromId(a.pid);
			
			int sumLocomotives = a.kind == TrackKind.ALL ? t.getCosts() : a.locomotives; 
			
			return t != null							// exists
					&& t.getOwner() == Track.NO_OWNER	// doesn't belong to somebody
					&& p.isCardRemovalValid(TrackKind.ALL, sumLocomotives)
					&& isPassedColorValid(t.getColor(), a.kind)
					&& p.isCardRemovalValid(a.kind, t.getCosts() - a.locomotives)
					&& p.getTrackCounter() + t.getCosts() <= gameState.getMaxTracks()
					&& !t.isParallelTrackOwned(a.pid);
			
		} else {
			return false;
		}

	}
	
	private boolean isPassedColorValid(TrackKind expectedCol, TrackKind actualCol) {
		return actualCol == TrackKind.ALL
				|| actualCol == expectedCol
				|| expectedCol == TrackKind.ALL;
	}

	@Override
	public Boolean visit(GetMissions a, Action lastAction) {
		return lastAction == null
				&& !gameState.getMissionManager().isEmpty();
	}

	/**
	 * Note: The field locomotives in PayTunnel is just an
	 * additional cost and not the overall number of locomotives
	 * to pay the tunnel.
	 */
	@Override
	public Boolean visit(PayTunnel a, Action lastAction) {

		if (lastAction instanceof AdditionalCost
				&& matchesPidWithAction(a.pid, lastAction)) {
			
			AdditionalCost cost = (AdditionalCost) lastAction;
			BuildTrack tunnel = cost.track;
			
			Player p = gameState.getPlayerFromId(a.pid);
			Track t = gameBoard.getTrackFromId(tunnel.tid);
			
			int locomotives = a.locomotives + tunnel.locomotives;
			
			return t != null							// exists
					&& t.getOwner() == Track.NO_OWNER	// doesn't belong to somebody
					&& p.isCardRemovalValid(TrackKind.ALL, locomotives)
					&& p.isCardRemovalValid(tunnel.kind, cost.additionalCosts + t.getCosts() - locomotives);
			
		} else {
			return false;
		}
		
	}

	@Override
	public Boolean visit(ReturnMissions a, Action lastAction) {

		if (lastAction instanceof GetMissions
				&& matchesPidWithAction(a.pid, lastAction)) {
			
			GetMissions getMissions = (GetMissions) lastAction;
			Set<Mission> origList = getMissions.missions;
			Set<Mission> retList = a.missions;
			
			if (retList.size() >= origList.size()) return false;
			
			for (Mission m : retList) {
				if (!origList.contains(m))
					return false;
			}
			
			return true;
			
		} else {
			return false;
		}
		
	}
	
	private boolean isTakingCardValid(Player p, boolean lastActionNull) {
		if (lastActionNull) {
			return p.getSumRessourceCards() < gameState.getMaxHandSize();
		} else {
			return p.getSumRessourceCards() <= gameState.getMaxHandSize();
		}
	}
	
	private boolean isTakingCardValid(Player p, TrackKind c, boolean lastActionNull) {
		if (lastActionNull) {
			return isTakingCardValid(p, lastActionNull)
					&& rm.existsCard(c) > 0
					&& rm.getCurrentOverallNumberOfCards() > 1;
		} else {
			if (rm.isClosedDeckEmpty()) {
				if (isLocomotive(c)) {
					return isTakingCardValid(p, lastActionNull)
							&& rm.existsCard(c) > 0
							&& rm.areTheOnlyRemainingOpenCardsLocomotives();
				} else {
					return isTakingCardValid(p, lastActionNull)
							&& rm.existsCard(c) > 0;
				}
			} else {
				return isTakingCardValid(p, lastActionNull)
						&& rm.existsCard(c) > 0
						&& !isLocomotive(c);
			}
		}
	}
	
	private boolean isLocomotive(TrackKind card) {
		return card == TrackKind.ALL;
	}

	@Override
	public Boolean visit(TakeCard a, Action lastAction) {
		
		Player p = gameState.getPlayerFromId(a.pid);
		
		if (lastAction == null) {
			return isTakingCardValid(p, a.kind, true);
		} else if (!matchesPidWithAction(a.pid, lastAction)) {
			return false;
		}
		
		if (lastAction instanceof TakeSecretCard
				|| lastAction instanceof TakeCard) {
			return isTakingCardValid(p, a.kind, false);
		} else {
			throw new IllegalArgumentException();
		}
		
	}

	@Override
	public Boolean visit(TakeSecretCard a, Action lastAction) {
		
		Player p = gameState.getPlayerFromId(a.pid);
		
		if (rm.isClosedDeckEmpty())
			return false;
		if (lastAction == null) {
			return isTakingCardValid(p, true);
		} else if (lastAction instanceof TakeSecretCard) {
			return isTakingCardValid(p, false);
		} else if (lastAction instanceof TakeCard) {
			
			if (isLocomotive(((TakeCard) lastAction).kind)
					|| !matchesPidWithAction(a.pid, lastAction))
				return false;
			else
				return isTakingCardValid(p, false);
			
		} else {
			throw new IllegalArgumentException();
		}
		
	}

	@Override
	public Boolean visit(AdditionalCost a, Action lastAction) {

		if (lastAction instanceof BuildTrack) {
			
			BuildTrack buildTrack = (BuildTrack) lastAction;
			
			Player p = gameState.getPlayerFromId(a.pid);
			Track t = gameBoard.getTrackFromId(buildTrack.tid);
			
			int sum = p.getRessourceMap().get(TrackKind.ALL);
			
			if (buildTrack.kind != TrackKind.ALL) {
				sum += p.getRessourceMap().get(buildTrack.kind);
			}
			
			return t.isTunnel()
					&& sum >= a.additionalCosts + t.getCosts();
			
		} else {
			return false;
		}
		
	}
	
	@Override
	public Boolean visit(ClientAction a, Action lastAction) {
		return a.accept(this, lastAction);
	}
	
	@Override
	public Boolean visit(Broadcast a, Action lastAction) {
		return a.accept(this, lastAction);
	}

	@Override
	public Boolean visit(Conceal a, Action lastAction) {
		return a.accept(this, lastAction);
	}

}
