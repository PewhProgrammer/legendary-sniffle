package de.unisaarland.sopra.zugumzug.server;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.GameControllerTests;
import de.unisaarland.sopra.zugumzug.Utility;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.NewMissions;
import de.unisaarland.sopra.zugumzug.model.GameState;
import de.unisaarland.sopra.zugumzug.model.Mission;
import de.unisaarland.sopra.zugumzug.model.Player;
import de.unisaarland.sopra.zugumzug.model.RessourceManager;

public class ServerGameControllerTests extends GameControllerTests {

	// TODO test numPlayer 0
	
	private boolean wasChangeInIncrementCounter(ServerGameController gc) {
		
		for (int i : gc.getFailureCounter().values()) {
			if (i != 0) return true;
		}
		
		return false;
	}
	
	@Test
	public void testOnNull() throws IOException {
		assertNotNull(getServerGameController(7));
	}
	
	/**
	 * Scenario:
	 * 	First player in ActivePlayers will be deleted,
	 * 	we are next. So it's our turn but the
	 * 	currentPlayerIndex stays the same
	 */
	@Test
	public void testDeletePlayer1() throws IOException {
		
		ServerGameController cgc = getServerGameController(7);
		
		int[] listIds = { 33, 89, 3, 2, 0, 125, 109 };
		cgc = (ServerGameController) setupGameControllerWithPlayers(cgc, listIds);
		
		List<Integer> activePlayers = cgc.getActivePlayers();
		Assert.assertTrue(Utility.containTwoListsSameValues(activePlayers, Utility.asList(listIds)));
		
		cgc.setCurrentPlayerIndex(0);
		cgc.deletePlayer(0);			// delete current player

		int[] listIdsDelete = { 33, 89, 3, 2, 125, 109 };
		List<Integer> activePlayersDelete = cgc.getActivePlayers();
		Assert.assertTrue(Utility.containTwoListsSameValues(activePlayersDelete,
							Utility.asList(listIdsDelete)));
		
		Assert.assertTrue(cgc.getCurrentPlayerIndex() == 0);
		
	}
	
	/**
	 * Scenario:
	 * 	Any player in ActivePlayers will be deleted,
	 * 	we are not next. So it's not our turn and the
	 * 	currentPlayerIndex stays the same
	 */
	@Test
	public void testDeletePlayer2() throws IOException {
		
		ServerGameController cgc = getServerGameController(7);
		
		int[] listIds = { 33, 89, 3, 2, 0, 125, 109 };
		cgc = (ServerGameController) setupGameControllerWithPlayers(cgc, listIds);
		
		List<Integer> activePlayers = cgc.getActivePlayers();
		Assert.assertTrue(Utility.containTwoListsSameValues(activePlayers, Utility.asList(listIds)));
		
		cgc.setCurrentPlayerIndex(1);
		cgc.deletePlayer(2);

		int[] listIdsDelete = { 33, 89, 3, 0, 125, 109 };
		List<Integer> activePlayersDelete = cgc.getActivePlayers();
		Assert.assertTrue(Utility.containTwoListsSameValues(activePlayersDelete,
							Utility.asList(listIdsDelete)));
		
		Assert.assertTrue(cgc.getCurrentPlayerIndex() == 1);
		
	}
	
	@Test
	public void testFailureCounter() throws IOException {
		
		ServerGameController sgc = getServerGameController(8);
		int[] listIds = { 45, 89, 3, 2, 0, 125, 999, 876 };
		
		sgc = (ServerGameController) setupGameControllerWithPlayers(sgc, listIds);

		HashMap<Integer, Integer> failureCounter = sgc.getFailureCounter();
		
		Assert.assertTrue(failureCounter.size() == listIds.length);

		for (int i : listIds) {
			Assert.assertTrue(failureCounter.get(i) == 0);
		}
		
		// increment
		sgc.incrementFailureCounter(89);
		sgc.incrementFailureCounter(89);
		sgc.incrementFailureCounter(89);
		sgc.incrementFailureCounter(999);
		
		Assert.assertTrue(failureCounter.get(89) == 3);
		Assert.assertTrue(failureCounter.get(999) == 1);
		Assert.assertTrue(failureCounter.get(45) == 0
							&& failureCounter.get(3) == 0
							&& failureCounter.get(2) == 0
							&& failureCounter.get(0) == 0
							&& failureCounter.get(125) == 0
							&& failureCounter.get(999) == 1
							&& failureCounter.get(876) == 0);
		
		// reset
		sgc.resetFailureCounter(45);
		sgc.resetFailureCounter(89);
		
		Assert.assertTrue(failureCounter.get(999) == 1);
		Assert.assertTrue(failureCounter.get(45) == 0
							&& failureCounter.get(89) == 0
							&& failureCounter.get(3) == 0
							&& failureCounter.get(2) == 0
							&& failureCounter.get(0) == 0
							&& failureCounter.get(125) == 0
							&& failureCounter.get(999) == 1
							&& failureCounter.get(876) == 0);
		
	}
	
	@Test
	public void testIsGameOverFalse() throws IOException {
		
		ServerGameController sgc = getServerGameController(8);
		int[] listIds = { 45, 89, 3, 2, 0, 125, 999, 876 };
		
		sgc = (ServerGameController) setupGameControllerWithPlayers(sgc, listIds);
		
		Assert.assertTrue(!sgc.isGameOver());
		
	}
	
	@Test
	public void testIsGameOverTrue() throws IOException {
		
		int[] list = { 1, 2, 3, 4 };
		GameState gameState = Utility.getGameOverGameState(list);
		ServerGameController sgc = new ServerGameController(gameState, STANDARD_MAP, serverComm, 1, SEED);
		
		Assert.assertTrue(sgc.isGameOver());
		
	}
	
	@Test(timeout = 1000)
	public void testDrawCardValid() throws IOException {
		
		int[] listIds = { 0, 2, 3, 33, 89, 109, 125 };
		final int pid = 2;
		TrackKind desiredColor = TrackKind.ALL;
		
		ServerGameController gc = getServerGameController(7);
		gc = (ServerGameController) setupGameControllerWithPlayers(gc, listIds);
		
		super.testDrawCardValid(gc, desiredColor, pid);
		
		Assert.assertTrue(!wasChangeInIncrementCounter(gc));
		
	}
	
	/**
	 * Scenario:
	 * 	It's this clients turn. This player draws a
	 * 	non existing black card.
	 * 
	 */
	@Test
	public void testDrawNonExistigCard() throws IOException {
		
		final int[] pid = { 2 };
		ServerGameController gc = getServerGameController(1);
		gc = (ServerGameController) setupGameControllerWithPlayers(gc, pid);
		RessourceManager rm = gameState.getRessourceManager();
		Player player = gameState.getPlayerFromId(pid[0]);
		HashMap<TrackKind, Integer> prMap = player.getRessourceMap();
		TrackKind currentColor = TrackKind.BLACK;
		
		// count of black cards = 0
		while (rm.existsCard(currentColor) > 0) {
			rm.refillOpenDeck();
		}
		
		int[] listIds = { 0, 2, 3, 33, 89, 109, 125 };
		gc = (ServerGameController) setupGameControllerWithPlayers(gc, listIds);
		gc.setCurrentPlayerIndex(1);
		
		// ----Draw black card
		gc.setLastValidAction(null);
		boolean wasExceptionThrown = false;
		try {
			gc.drawCard(pid[0], currentColor);
		} catch (IllegalArgumentException e) {
			wasExceptionThrown = true;
		}
		Assert.assertTrue(wasExceptionThrown);
		
		Assert.assertTrue(gc.getLastValidAction() == null);
		Assert.assertTrue(rm.existsCard(currentColor) == 0);
		
		Assert.assertTrue(gc.getFailureCounter().get(pid[0]) == 1);
		
		// Test own Player
		Assert.assertTrue(player.getMissionList().size() == 0);
		Assert.assertTrue(player.getScore() == 0);
		Assert.assertTrue(player.getSumRessourceCards() == 0);
		Assert.assertTrue(player.getTrackCounter() == 0);
		
		for (TrackKind t : Utility.colorMap.values()) {
				Assert.assertTrue(prMap.get(t) == 0);
		}
		
		Assert.assertTrue(wasChangeInIncrementCounter(gc));

	}
	
	/**
	 * Scenario:
	 * 	Player with pid 2 builds a track. The track
	 * 	costs 2 and he gains 2 points.
	 * 
	 */
	@Test
	public void testBuildTrackCurrentPlayer() throws IOException {
		
		final short trackID = 1;
		TrackKind desiredColor = TrackKind.ALL;
		
		ServerGameController gc = getServerGameController(7);
		int[] listIds = { 0, 2, 3, 33, 89, 109, 125 };
		gc = (ServerGameController) setupGameControllerWithPlayers(gc, listIds);
		gc.setCurrentPlayerIndex(1);
		
		super.testBuildTrack(gc, 2, trackID, desiredColor);
		
		Assert.assertTrue(!wasChangeInIncrementCounter(gc));
		
	}
	
	@Test
	public void testDrawSecretCardCurrentPlayer() throws IOException {
		
		final int ownPlayerID = 2;
		
		ServerGameController gc = getServerGameController(7);
		int[] listIds = { 0, 2, 3, 33, 89, 109, 125 };
		gc = (ServerGameController) setupGameControllerWithPlayers(gc, listIds);
		gc.setCurrentPlayerIndex(1);
		gc.setLastValidAction(null);
		
		super.testDrawSecretCard(gc, ownPlayerID);
		
		Assert.assertTrue(!wasChangeInIncrementCounter(gc));
		
	}
	
	@Test
	public void testDrawSecretCardWrongPlayer() throws IOException {
		
		final int otherPlayerID = 2;
		final int ownPlayerID = 0;
		
		ServerGameController gc = getServerGameController(7);
		int[] listIds = { 0, 2, 3, 33, 89, 109, 125 };
		gc = (ServerGameController) setupGameControllerWithPlayers(gc, listIds);
		gc.setCurrentPlayerIndex(1);	// another player
		gc.setLastValidAction(null);
		
		super.testDrawSecretCardWrongPlayer(gc, ownPlayerID, otherPlayerID);
		
		Assert.assertFalse(wasChangeInIncrementCounter(gc));
	}
	
	@Test
	public void testGiveMissionsToPlayer() throws IOException {
		
		final int curPlayerID = 2;
		
		ServerGameController gc = getServerGameController(7);
		int[] listIds = { 0, 2, 3, 33, 89, 109, 125 };
		gc = (ServerGameController) setupGameControllerWithPlayers(gc, listIds);
		gc.setCurrentPlayerIndex(1);
		gc.setLastValidAction(null);
		
		List<Mission> missionList = getMissionList(gameState);
		NewMissions newMissions = new NewMissions(curPlayerID, new HashSet<Mission>(missionList));
		
		super.testGiveMissionsToPlayer(gc, curPlayerID, newMissions);
		
		Assert.assertTrue(!wasChangeInIncrementCounter(gc));
		
	}
	
}
