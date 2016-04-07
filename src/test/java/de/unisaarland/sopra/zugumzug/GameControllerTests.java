package de.unisaarland.sopra.zugumzug;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import de.unisaarland.sopra.MersenneTwister;
import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.actions.client.BuildTrack;
import de.unisaarland.sopra.zugumzug.actions.client.GetMissions;
import de.unisaarland.sopra.zugumzug.actions.client.NewObserver;
import de.unisaarland.sopra.zugumzug.actions.client.Register;
import de.unisaarland.sopra.zugumzug.actions.client.ReturnMissions;
import de.unisaarland.sopra.zugumzug.actions.client.TakeCard;
import de.unisaarland.sopra.zugumzug.actions.client.TakeSecretCard;
import de.unisaarland.sopra.zugumzug.actions.server.TookCard;
import de.unisaarland.sopra.zugumzug.actions.server.TookMissions;
import de.unisaarland.sopra.zugumzug.actions.server.TookSecretCard;
import de.unisaarland.sopra.zugumzug.actions.server.TrackBuilt;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.NewCards;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.NewMissions;
import de.unisaarland.sopra.zugumzug.client.ClientCommunicator;
import de.unisaarland.sopra.zugumzug.client.ClientCommunicatorDummy;
import de.unisaarland.sopra.zugumzug.client.ClientGameController;
import de.unisaarland.sopra.zugumzug.client.GamerDummy;
import de.unisaarland.sopra.zugumzug.model.GameState;
import de.unisaarland.sopra.zugumzug.model.Mission;
import de.unisaarland.sopra.zugumzug.model.Player;
import de.unisaarland.sopra.zugumzug.model.RessourceManager;
import de.unisaarland.sopra.zugumzug.server.ServerCommunicatorDummy;
import de.unisaarland.sopra.zugumzug.server.ServerGameController;

public abstract class GameControllerTests {

	protected final static String STANDARD_MAP = "./src/MapFiles/SmallBoard";
	protected final static int SEED = 42;
	protected final static int PLAYER_NUM = 2;
	protected final static int OWN_ID = 0;
	
	protected GameState gameState;
	protected Validator validator;
	
	protected ClientCommunicatorDummy clientComm;
	protected ServerCommunicatorDummy serverComm;
	
	protected GamerDummy gamer;
	
	protected MersenneTwister rng;
	
	protected final MapFactory mapFactory = new MapFactory();
	
	@Before
	public void setup() throws IOException {
		serverComm = new ServerCommunicatorDummy(new ArrayList<>());
		try {
			clientComm = new ClientCommunicatorDummy(OWN_ID, "localhost", 7779, new ArrayList<>());
		} catch (ConnectException e) {
			// everything alright
		}
		gameState = new GameState();
		gamer = new GamerDummy();
		MersenneTwister rng = new MersenneTwister(42);
		mapFactory.initializeGameState(STANDARD_MAP, 1, rng, gameState); // TODO must be null for client?! then no constructor necessary
		validator = new Validator(gameState);
	}
	
	@After
	public void after() {
		serverComm.close();
	}
	
	protected ClientGameController getClientGameController() throws IOException {
		return new ClientGameController(gameState, clientComm, gamer);
	}
	
	protected ServerGameController getServerGameController(int numPlayers) throws IOException {
		return new ServerGameController(gameState, STANDARD_MAP, serverComm, PLAYER_NUM, SEED);
	}
	
	protected GameController setupGameControllerWithPlayers(GameController gc, int[] listIds) {
		insertListToActivePlayers(gc, listIds);
		return gc;
	}
	
	private void insertListToActivePlayers(GameController gc, int[] list) {
		
		if (gc instanceof ServerGameController) {
			for (int i : list) {
				((ServerGameController) gc).visit(new NewObserver(i));
				((ServerGameController) gc).visit(new Register("Frank" + i, i));
			}
		} else {
			ClientGameController cgc = (ClientGameController) gc;
			List<Integer> aList = cgc.getActivePlayers();
			GameState gs = cgc.gameState;
 			for (int i : list) {
 				aList.add(i);
 				gs.addPlayerInPlayerMap(new Player(i, "Frank" + i));
			}
		}
		
	}
	
	protected List<Mission> getMissionList(GameState gameState) {
		
		List<Mission> ml = new ArrayList<Mission>();
		
		for (int i = 0; i < 3; i++)
			ml.add(gameState.getMissionManager().drawMission());
		
		return ml;
	}
	
	private class IntComparator implements Comparator<Integer> {

	    @Override
	    public int compare(Integer v1, Integer v2) {
	        return Integer.compare(v1, v2);
	    }
	}
	
	
	////////////////////////////////////
	//	tests
	////////////////////////////////////
	
	public void testSetup(GameController gc, int[] listIds) {
		
		// setup
		Assert.assertTrue(gc.getActivePlayers().size() == 1);
		
		gc = setupGameControllerWithPlayers(gc, listIds);
		
		List<Integer> activePlayers = gc.getActivePlayers();
		Assert.assertTrue(activePlayers.size() == listIds.length + 1);
		
		Assert.assertTrue(gc.getLastValidAction() == null);
		gc.setLastValidAction(null);
		Assert.assertTrue(gc.getLastValidAction() == null);
		
	}
	
	public void testDrawCardValid(GameController gc, TrackKind desiredColor, final int pid) {
		
		RessourceManager rm = gameState.getRessourceManager();
		
		// The desired color must be in the open deck
		// available, because player must be able to draw
		int countAllCard = rm.existsCard(desiredColor);
		while (countAllCard < 1) {
			rm.refillOpenDeck();
			countAllCard = rm.existsCard(desiredColor);
		}
		
		gc.setCurrentPlayerIndex(1);
		gc.setLastValidAction(null);
		
		if (gc instanceof ServerGameController) {
			((ServerGameController) gc).visit(new TakeCard(pid, desiredColor));
		} else {
			((ClientGameController) gc).visit(new TookCard(pid, desiredColor));
		}
		
		Assert.assertTrue(gc.getLastValidAction() == null);
		
		// If the player draws a card either one card of the same
		// type will be refilled or another. So it must be either
		// equal the former size or one less
		Assert.assertTrue(rm.existsCard(desiredColor) == countAllCard
							|| rm.existsCard(desiredColor) == countAllCard - 1);
		
		// Test own Player
		Player player = gameState.getPlayerFromId(pid);
		HashMap<TrackKind, Integer> prMap = player.getRessourceMap();
		Assert.assertTrue(player.getMissionList().size() == 0);
		Assert.assertTrue(player.getScore() == 0);
		Assert.assertTrue(player.getSumRessourceCards() == 1);
		Assert.assertTrue(player.getTrackCounter() == 0);
		
		for (TrackKind t : Utility.colorMap.values()) {
			if (t == desiredColor)
				Assert.assertTrue(prMap.get(t) == 1);
			else
				Assert.assertTrue(prMap.get(t) == 0);
		}
		
	}
	
	public void testDrawCardWrongPlayer(ServerGameController gc, TrackKind desiredColor, int pid) {

		RessourceManager rm = gameState.getRessourceManager();
		
		// The desired color must be in the open deck
		// available, because player must be able to draw
		int countAllCard = rm.existsCard(desiredColor);
		while (countAllCard == 0) {
			rm.refillOpenDeck();
			countAllCard = rm.existsCard(desiredColor);
		}
		
		gc.setLastValidAction(null);
		
		gc.visit(new TakeCard(pid, desiredColor));
		
		Assert.assertTrue(gc.getLastValidAction() == null);
		
		// If the player draws a card either one card of the same
		// type will be refilled or another. So it must be either
		// equal the former size or one less
		Assert.assertTrue(rm.existsCard(desiredColor) == countAllCard);
		
		// Test own Player
		Player player = gameState.getPlayerFromId(pid);
		HashMap<TrackKind, Integer> prMap = player.getRessourceMap();
		Assert.assertTrue(player.getMissionList().size() == 0);
		Assert.assertTrue(player.getScore() == 0);
		Assert.assertTrue(player.getSumRessourceCards() == 0);
		Assert.assertTrue(player.getTrackCounter() == 0);
		
		for (TrackKind t : Utility.colorMap.values()) {
				Assert.assertTrue(prMap.get(t) == 0);
		}
		
	}
	
	public void testBuildTrack(GameController gc, final int pid, final short trackID, TrackKind desiredColor) {
		
		gc.setLastValidAction(null);
		
		Player player = gameState.getPlayerFromId(pid);
		HashMap<TrackKind, Integer> prMap = player.getRessourceMap();
		
		Assert.assertTrue(player.getSumRessourceCards() == 0);
		Assert.assertTrue(!player.isCardRemovalValid(desiredColor, 2));
		Assert.assertTrue(!player.isCardRemovalValid(desiredColor, 3));
		Assert.assertTrue(player.getScore() == 0);
		Assert.assertTrue(player.getTrackCounter() == 0);
		
		prMap.put(desiredColor, 2);
		
		Assert.assertTrue(player.isCardRemovalValid(desiredColor, 2));
		
		if (gc instanceof ServerGameController) {
			((ServerGameController) gc).visit(new BuildTrack(pid, trackID, desiredColor, 0));
		} else {
			((ClientGameController) gc).visit(new TrackBuilt(pid, trackID, desiredColor, 0));
		}
		
		Assert.assertTrue(prMap.get(desiredColor) == 0);
		Assert.assertTrue(player.getScore() == 2);
		Assert.assertTrue(player.getTrackCounter() == 2);
		Assert.assertTrue(!gc.isGameOver());
			
	}
	
	public void testBuildTrackWrongPlayer(ServerGameController sgc, final int pid, final short trackID, TrackKind desiredColor) {
		
		sgc.visit(new BuildTrack(pid, trackID, desiredColor, 0));
//		sgc.buildTrack(pid, trackID, desiredColor, 2, 0);
		
		sgc.setLastValidAction(null);
		
		Player player = gameState.getPlayerFromId(pid);
		HashMap<TrackKind, Integer> prMap = player.getRessourceMap();
		
		Assert.assertTrue(player.getSumRessourceCards() == 0);
		Assert.assertTrue(!player.isCardRemovalValid(desiredColor, 2));
		Assert.assertTrue(!player.isCardRemovalValid(desiredColor, 3));
		Assert.assertTrue(player.getScore() == 2);
		Assert.assertTrue(player.getTrackCounter() == 0);
		
		// Simulating physically he could build it
		prMap.put(desiredColor, 2);
		
		Assert.assertTrue(player.isCardRemovalValid(desiredColor, 2));
		Assert.assertTrue(player.getSumRessourceCards() == 2);
		
//		sgc.buildTrack(pid, trackID, desiredColor, 2, 0);
		
		// The state should be same
		Assert.assertTrue(prMap.get(desiredColor) == 2);
		Assert.assertTrue(player.getScore() == 0);
		Assert.assertTrue(player.isCardRemovalValid(desiredColor, 2));
		Assert.assertTrue(player.getSumRessourceCards() == 2);
		Assert.assertTrue(player.getTrackCounter() == 0);
		Assert.assertTrue(!sgc.isGameOver());
		
	}
	
	public void testDrawSecretCard(GameController gc, final int pid) {
		
		gc.setLastValidAction(null);
		
		Player player = gameState.getPlayerFromId(pid);
		HashMap<TrackKind, Integer> prMap = player.getRessourceMap();
		
		Assert.assertTrue(prMap.size() == 9);
		Assert.assertTrue(player.getSumRessourceCards() == 0);
		Assert.assertTrue(player.getScore() == 0);
		Assert.assertTrue(player.getTrackCounter() == 0);
		
		if (gc instanceof ServerGameController) {
			((ServerGameController) gc).visit(new TakeSecretCard(pid));
		} else {
			((ClientGameController) gc).visit(new NewCards(pid, TrackKind.BLACK, 1));
			Assert.assertTrue(prMap.get(TrackKind.ALL) == 0
					&& prMap.get(TrackKind.BLACK) == 1
					&& prMap.get(TrackKind.BLUE) == 0
					&& prMap.get(TrackKind.GREEN) == 0
					&& prMap.get(TrackKind.ORANGE) == 0
					&& prMap.get(TrackKind.RED) == 0
					&& prMap.get(TrackKind.VIOLET) == 0
					&& prMap.get(TrackKind.WHITE) == 0
					&& prMap.get(TrackKind.YELLOW) == 0);
		}
			
		Assert.assertTrue(prMap.size() == 9);
		Assert.assertTrue(player.getSumRessourceCards() == 1);
		
		Assert.assertTrue(player.getScore() == 0);
		Assert.assertTrue(player.getTrackCounter() == 0);
		
		Assert.assertTrue(!gc.isGameOver());
		
	}
	
	/**
	 * A secret card will be taken, but not from the own player
	 * so no changes in RessourceMap from player.
	 * 
	 * @param gc
	 * @param otherPid
	 */
	public void testDrawSecretCardWrongPlayer(GameController gc, final int ownPId, final int otherPid) {
		
		Player player = gameState.getPlayerFromId(ownPId);
		HashMap<TrackKind, Integer> prMap = player.getRessourceMap();
		
		Assert.assertTrue(prMap.size() == 9);
		Assert.assertTrue(player.getSumRessourceCards() == 0);
		Assert.assertTrue(player.getScore() == 0);
		Assert.assertTrue(player.getTrackCounter() == 0);
		
		
		if (gc instanceof ServerGameController) {
			((ServerGameController) gc).visit(new TakeSecretCard(otherPid));
		} else {
			((ClientGameController) gc).visit(new TookSecretCard(otherPid));
		}
		
		Assert.assertTrue(prMap.size() == 9);
		Assert.assertTrue(player.getSumRessourceCards() == 0);
		Assert.assertTrue(player.getScore() == 0);
		Assert.assertTrue(player.getTrackCounter() == 0);
		
		Assert.assertTrue(!gc.isGameOver());
		
	}
	
	public void testGiveMissionsToPlayer(GameController gc, final int pid, NewMissions newMissions) {
		
		Player player = gameState.getPlayerFromId(pid);
		List<Mission> missionList = player.getMissionList();

		final int size = gameState.getMissionManager().getClosedMissionDeck().size();
		
		Assert.assertTrue(missionList.size() == 0);
		
		Mission m1 = new Mission(0, 2, (short) 1, (short) 2);
		Mission m2 = new Mission(1, 2, (short) 2, (short) 3);
		Mission m3 = new Mission(2, 2, (short) 3, (short) 4);
		
		Set<Mission> set = new HashSet<>();
		set.add(m1);
		set.add(m2);
		set.add(m3);
		
		gc.setLastValidAction(new GetMissions(pid, set));
		
		if (gc instanceof ServerGameController) {
			((ServerGameController) gc).visit(new ReturnMissions(pid, new HashSet<>()));
		} else {
			((ClientGameController) gc).visit(new TookMissions(pid, 3));
//		gc.giveMissionsToPlayer(pid);
		}
		
		Assert.assertTrue(missionList.size() == 3);
		// must have still the same size
		Assert.assertTrue(gameState.getMissionManager().getClosedMissionDeck().size()
							== size);
		Assert.assertTrue(!gc.isGameOver());
		
	}
	
	public void testGiveMissionsToWrongPlayer(GameController gc, final int pid, NewMissions newMissions) {
		
		Player player = gameState.getPlayerFromId(pid);
		List<Mission> missionList = player.getMissionList();

		final int size = gameState.getMissionManager().getClosedMissionDeck().size();
		gc.setLastValidAction(null);
//		gc.setCurrentAction(newMissions);
		
//		Assert.assertTrue(gc.getCurrentAction() == newMissions);
		Assert.assertTrue(missionList.size() == 0);
		
//		gc.giveMissionsToPlayer(pid);
		
		// no changes expected
		Assert.assertTrue(missionList.size() == 0);
		// must have still the same size
		Assert.assertTrue(gameState.getMissionManager().getClosedMissionDeck().size()
							== size);
		Assert.assertTrue(!gc.isGameOver());
		
	}

}
