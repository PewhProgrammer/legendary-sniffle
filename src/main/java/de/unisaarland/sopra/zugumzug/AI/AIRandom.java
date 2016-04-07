package de.unisaarland.sopra.zugumzug.AI;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.Gamer;
import de.unisaarland.sopra.zugumzug.Validator;
import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.client.*;
import de.unisaarland.sopra.zugumzug.actions.server.*;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.*;
import de.unisaarland.sopra.zugumzug.model.Player;
import de.unisaarland.sopra.zugumzug.model.RessourceManager;
import de.unisaarland.sopra.zugumzug.model.Track;

/**
 * This is an 'AI' which gives random input. This is just
 * for testing purposes to compare with other AIs.
 * 
 * @author Timo Guehring
 *
 */
public class AIRandom implements Gamer, ActionServerVisitor<Void> {

	private class TrackComparator implements Comparator<Track> {

		@Override
		public int compare(Track t1, Track t2) {
			if (t1.getCosts() > t2.getCosts())
				return -1;
			else if (t1.getCosts() < t2.getCosts())
				return 1;
			else
				return 0;
		}
		
	}
	
	enum ActionType {
		NewMissions,
		TakeCard,
		TakeSecretCard,
		BuildTrack,		// only need for getRndAction
		GetMissions,	// only need for getRndAction
		GameOver,
		None
	}
	
	private static List<ActionType> actionList = new ArrayList<>();
	
	static {
		actionList.add(ActionType.BuildTrack);
		actionList.add(ActionType.TakeCard);
		actionList.add(ActionType.TakeSecretCard);
	}
	
	
	private ActionType followingAction = ActionType.NewMissions;
	private ServerAction lastReceivedAction = null;
	private ClientAction lastSendAction = null;
	
	private final TrackComparator trackComp = new TrackComparator();
	
	private boolean gameStart = false;
	
	// There is no GameOver Event. So if FinalPoints,
	// FinalMissions, LongestTrack and Winner was sent
	// the game ends
	private boolean isGameOver = false;
	// In this state we still wait for Events (comment above)
	// and perform no actions.
	// So if one of these was sent, set to true
	private boolean waitingForGameOver = false;
	
	private boolean wasFinalMissionsSent = false;
	private boolean wasFinalPointsSent = false;
	private boolean wasLongestTrackSent = false;
	private boolean wasWinnerSent = false;
	
	private int ownId = 0;
	private Player ownPlayer = null;
	
	private final de.unisaarland.sopra.zugumzug.model.GameState gameState;
	private Validator validator;
	private RessourceManager rm;
	
	public AIRandom(de.unisaarland.sopra.zugumzug.model.GameState gameState, int myId) {
		this.gameState = gameState;
		this.ownId = myId;
	}
	
	@Override
	public void update(ServerAction a) {
		
		a.accept(this);
		
		if (!isGameOver && gameStart) {
			
			if (!waitingForGameOver) {
				// A better AI would precalc something here
			}
			
			if (followingAction == ActionType.None)
				lastSendAction = null;
		}
		
		lastReceivedAction = a;
	}
	
	@Override
	public void initialize() {
		this.ownPlayer = gameState.getPlayerFromId(ownId);
		this.validator = new Validator(gameState);
		this.lastSendAction = new GetMissions(ownId, null);
		this.rm = gameState.getRessourceManager();
	}
	
	@Override
	public String getName() {
		return "AIRandom";
	}
	
	/**
	 * Updates isGameOver and waitingForGameOver
	 */
	private void updateGameOverStates() {
		isGameOver = wasFinalMissionsSent
						&& wasFinalPointsSent
						&& wasLongestTrackSent
						&& wasWinnerSent;
		waitingForGameOver = wasFinalMissionsSent
						|| wasFinalPointsSent
						|| wasLongestTrackSent
						|| wasWinnerSent;
		if (isGameOver)
			followingAction = ActionType.GameOver;
	}
	
	private int getRandomNumber(int max) {
		return ThreadLocalRandom.current().nextInt(0, max + 1); // TODO test range
	}
	
	/**
	 * Returns a list containing all tracks,
	 * the AI can afford at the moment,
	 * without tunnels,
	 * descending sorted, because you get more points
	 * the longer the tracks are.
	 * 
	 * @return
	 */
	private List<Track> getTracks() {
		
		List<Track> listReturn = new ArrayList<>();
		Set<Track> listOrigTracks = gameState.getGameBoard().getTrackSet();
		HashMap<TrackKind, Integer> ressMap = ownPlayer.getRessourceMap();
		
		for (Track t : listOrigTracks) {
			if (ressMap.get(t.getColor()) >= t.getCosts()
					&& !t.isTunnel()
					&& !t.isParallelTrackOwned(ownId)) {
				listReturn.add(t);
			}
		}
		
		listReturn.sort(trackComp);
		
		return listReturn;
	}
	
	private <E> Set<E> cloneSet(Set<E> set) {
		Set<E> newSet = new HashSet<>();
		
		for (E e : set) {
			newSet.add(e);
		}
		
		return newSet;
	}
	
	@Override
	public ClientAction getInput() {
		
		ClientAction nextAction;
		
		if (lastSendAction instanceof ReturnMissions)
			lastSendAction = null;
		
		switch (followingAction) {
			case None:
				nextAction = getRandomAction();
				break;
			case TakeCard:
				TrackKind[] deck = gameState.getRessourceManager().getOpenRessourceDeck();
				TrackKind kind = TrackKind.ALL;
				
				while (kind != TrackKind.ALL) {
					int rndIndx = getRandomNumber(4);
					kind = deck[rndIndx];
				}
				
				followingAction = ActionType.None;
				nextAction = new TakeCard(ownId, kind);
				
				break;
			case TakeSecretCard:
				followingAction = ActionType.None;
				if (ownPlayer.getSumRessourceCards() >= gameState.getMaxHandSize()) {
					followingAction = ActionType.None;
					return getInput();
				}
				nextAction = new TakeSecretCard(ownId);
				break;
			case NewMissions:
				if (lastReceivedAction instanceof NewMissions) {
					NewMissions newMissions = (NewMissions) lastReceivedAction;
					int countKeepingCards = getRandomNumber(2);
					Set<de.unisaarland.sopra.zugumzug.model.Mission> missions
						= (Set<de.unisaarland.sopra.zugumzug.model.Mission>) cloneSet(newMissions.missions);
					
					Iterator<de.unisaarland.sopra.zugumzug.model.Mission> it = missions.iterator();
					for (it.next(); countKeepingCards >= 0 && it.hasNext(); it.next(), countKeepingCards--) {
						it.remove();
					}
					
					followingAction = ActionType.None;
					nextAction = new ReturnMissions(ownId, missions);
					
				} else {
					throw new IllegalArgumentException("AIRandom lastReceived Action was: " + lastReceivedAction);
				}
				
				break;
			case GameOver:
				return new Leave(ownId);
			default:
				throw new IllegalArgumentException("AIRandom default");
		}
		
		if (lastSendAction instanceof GetMissions) {
			if (lastReceivedAction instanceof NewMissions) {
				NewMissions newMissions = (NewMissions) lastReceivedAction;
				lastSendAction = new GetMissions(ownId, newMissions.missions);
			} else {
				throw new IllegalArgumentException("AIRandom GetMissions NewMissions");
			}
		}
		if (!validator.test(ownId, lastSendAction, nextAction))
			return getInput();
		
		lastSendAction = nextAction;
		
		return nextAction;
	}
	
	private ClientAction getRandomAction() {
		
		int rndAction = getRandomNumber(actionList.size() - 1);

		switch (rndAction) {
			// BuildTrack
			case 0:
				List<Track> tracks = getTracks();
				
				if (tracks.isEmpty()) {
					return getRandomAction();
				} else {
					int rndIndx = getRandomNumber(tracks.size() - 1);
					Track t = tracks.get(rndIndx);
					
					followingAction = ActionType.None;
					
					return new BuildTrack(ownId
											,t.getID()
											,t.getColor()
											,0);
				}	
			// TakeCard
			case 1:
				if (rm.getCurrentOverallNumberOfCards() < 2)
					return getRandomAction();
				
				TrackKind card = null;
				TrackKind[] deck = gameState.getRessourceManager().getOpenRessourceDeck();
				while (card == null) {
					int rndIndx = getRandomNumber(4);
					card = deck[rndIndx];
				}
				
				followingAction = card == TrackKind.ALL ? ActionType.None : ActionType.TakeCard;
				
				return new TakeCard(ownId, card);
			// TakeSecretCard
			case 2:
				if (rm.getCurrentSizeClosedDeck() <= 1)
					return getRandomAction();
				
				if (lastSendAction instanceof TakeSecretCard) {
					followingAction = ActionType.None;
				} else {
					followingAction = ActionType.TakeSecretCard;
				}
				return new TakeSecretCard(ownId);
			default:
				throw new IllegalArgumentException();
		}
		
	}
	
	
	//////////////////////////////////
	//	Setup Events
	//////////////////////////////////
	
	@Override
	public Void visit(GameStart a) {
		gameStart = true;
		return null;
	}
	
	@Override
	public Void visit(SendId a) {
		return null;
	}
	
	@Override
	public Void visit(GameState a) {
		return null;
	}
	
	
	//////////////////////////////////
	//	Server Events
	//////////////////////////////////

	@Override
	public Void visit(ServerAction a) {
		return a.accept(this);
	}

	@Override
	public Void visit(Broadcast a) {
		return a.accept(this);
	}
	
	@Override
	public Void visit(MaxTrackLength a) {
		return null;
	}

	@Override
	public Void visit(NewCity a) {
		return null;
	}

	@Override
	public Void visit(NewOpenCard a) {
		return null;
	}

	@Override
	public Void visit(NewPlayer a) {
		return null;
	}

	@Override
	public Void visit(NewTrack a) {
		return null;
	}

	@Override
	public Void visit(PlayerLeft a) {
		return null;
	}

	@Override
	public Void visit(TookCard a) {
		return null;
	}

	@Override
	public Void visit(TookMissions a) {
		return null;
	}

	@Override
	public Void visit(TookSecretCard a) {
		return null;
	}

	@Override
	public Void visit(TrackBuilt a) {
		return null;
	}

	@Override
	public Void visit(TunnelBuilt a) {
		return null;
	}

	@Override
	public Void visit(TunnelNotBuilt a) {
		
		// if followingAction was set to PayTunnel
		// and the AIRandom couldn't pay it, reset
		// followingAction to null
		if (a.pid == ownId)
			followingAction = ActionType.None;
		
		return null;
	}
	
	
	//////////////////////////////////
	//	Conceal Events
	//////////////////////////////////

	@Override
	public Void visit(Conceal a) {
		return a.accept(this);
	}

	@Override
	public Void visit(AdditionalCost a) {
		return null;
	}

	@Override
	public Void visit(Die a) {
		this.isGameOver = true;
		return null;
	}

	@Override
	public Void visit(Failure a) {
		return null;
	}

	@Override
	public Void visit(Mission a) {
		return null;
	}

	@Override
	public Void visit(NewCards a) {
		return null;
	}

	@Override
	public Void visit(NewMissions a) {
		return null;
	}
	
	@Override
	public Void visit(NewPlayersToObserver a) {
		return null;
	}
	
	@Override
	public Void visit(ObserverLeft a) {
		return null;
	}

	
	//////////////////////////////////
	//	Game Over Events
	//////////////////////////////////
	
	@Override
	public Void visit(FinalMissions a) {
		wasFinalPointsSent = true;
		updateGameOverStates();
		return null;
	}

	@Override
	public Void visit(FinalPoints a) {
		wasFinalMissionsSent = true;
		updateGameOverStates();
		return null;
	}
	
	@Override
	public Void visit(LongestTrack a) {
		wasLongestTrackSent = true;
		updateGameOverStates();
		return null;
	}
	
	@Override
	public Void visit(Winner a) {
		wasWinnerSent = true;
		System.out.println("score: "+this.ownPlayer.getScore());
		updateGameOverStates();
		return null;
	}

	@Override
	public void updateActivePlayer(Player p) {
		// TODO Auto-generated method stub
		
	}

}
