package de.unisaarland.sopra.zugumzug.client;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Test;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.GameControllerTests;
import de.unisaarland.sopra.zugumzug.Utility;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.NewCards;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.NewMissions;
import de.unisaarland.sopra.zugumzug.model.GameState;
import de.unisaarland.sopra.zugumzug.model.Mission;
import de.unisaarland.sopra.zugumzug.model.Player;
import de.unisaarland.sopra.zugumzug.model.RessourceManager;

public class ClientGameControllerTests extends GameControllerTests {
	
	@Test
	public void testOnNull() throws InterruptedException, ExecutionException, IOException {
		assertNotNull(getClientGameController());
	}
	
	@Test
	public void testSimulateSetup() throws IOException {
		
		ClientGameController cgc = getClientGameController();
		int[] listIds = { 45, 89, 3, 2, 0, 125 };
	
		super.testSetup(cgc, listIds);
		
	}
	
	@Test
	public void testFailureCounter() throws IOException {

		ClientGameController cgc = getClientGameController();
		
		// initial 0
		Assert.assertTrue(cgc.getFailureCount() == 0);
		
		cgc.incrementFailureCounter();
		cgc.incrementFailureCounter();
		cgc.incrementFailureCounter();
		
		Assert.assertTrue(cgc.getFailureCount() == 3);
		
		cgc.resetFailureCounter();
		
		Assert.assertTrue(cgc.getFailureCount() == 0);
		
	}
	
	@Test
	public void testIsGameOverFalse() throws IOException {
		
		ClientGameController cgc = getClientGameController();
		
		int[] listIds = { 45, 89, 3, 2, 0, 125 };
		cgc = (ClientGameController) setupGameControllerWithPlayers(cgc, listIds);
		
		cgc.setID(3);
		
		Assert.assertTrue(!cgc.isGameOver());
		
	}
	
	@Test
	public void testIsMyTurnTrue() throws IOException {
		
		ClientGameController cgc = getClientGameController();
		
		int[] listIds = { 45, 89, 3, 2, 125 };
		cgc = (ClientGameController) setupGameControllerWithPlayers(cgc, listIds);
		
		cgc.setID(0);
		
//		Assert.assertTrue(cgc.isMyTurn());
	}
	
	@Test
	public void testIsMyTurnFalse() throws IOException {
		
		ClientGameController cgc = getClientGameController();
		
		int[] listIds = { 45, 89, 3, 2, 125 };
		cgc = (ClientGameController) setupGameControllerWithPlayers(cgc, listIds);
		
		cgc.setID(3);
		
//		Assert.assertTrue(!cgc.isMyTurn());
	}
	
	/**
	 * Scenario:
	 * 	First player in ActivePlayers will be deleted,
	 * 	we are next. So it's our turn but the
	 * 	currentPlayerIndex stays the same
	 */
	@Test
	public void testDeletePlayer1() throws IOException {
		
		ClientGameController gc = getClientGameController();
		
		int[] listIds = { 33, 89, 3, 2, 125, 109 };
		gc = (ClientGameController) setupGameControllerWithPlayers(gc, listIds);
		
		gc.setID(2);
		gc.setCurrentPlayerIndex(0);
		gc.deletePlayer(0);			// delete current player

		Assert.assertTrue(gc.getCurrentPlayerIndex() == 0);
		Assert.assertTrue(gc.getId() == 2);
		
	}
	
	/**
	 * Scenario:
	 * 	Any player in ActivePlayers will be deleted,
	 * 	we are not next. So it's not our turn and the
	 * 	currentPlayerIndex stays the same
	 */
	@Test
	public void testDeletePlayer2() throws IOException {
		
		ClientGameController cgc = getClientGameController();
		
		int[] listIds = { 33, 89, 3, 2, 125, 109 };
		cgc = (ClientGameController) setupGameControllerWithPlayers(cgc, listIds);
		
		cgc.setID(89);
		cgc.setCurrentPlayerIndex(1);
		cgc.deletePlayer(2);

		Assert.assertTrue(cgc.getCurrentPlayerIndex() == 1);
		Assert.assertTrue(cgc.getId() == 89);
		
	}
	
	/**
	 * Scenario:
	 * 	It's this clients turn. This player draws a locomotive.
	 * 	So we expect changes in RessourceManager
	 * 
	 */
	@Test
	public void testDrawCardValid() throws IOException {
		
		int[] listIds = { 2, 3, 33, 89, 109, 125 };
//		final int  = 2;
		TrackKind desiredColor = TrackKind.ALL;
		
		ClientGameController gc = getClientGameController();
		gc = (ClientGameController) setupGameControllerWithPlayers(gc, listIds);
		gc.setID(OWN_ID);
		
		super.testDrawCardValid(gc, desiredColor, OWN_ID);
		
		Assert.assertTrue(gc.getFailureCount() == 0);
//		Assert.assertTrue(!gc.isMyTurn());
		
	}
	
	/**
	 * Scenario:
	 * 	It's another clients turn. This player draws a locomotive.
	 * 	So we expect changes in RessourceManager
	 * 
	 */
	@Test(timeout = 200)
	public void testDrawCardValidFromAnotherPlayer() throws IOException {
		
		ClientGameController cgc = getClientGameController();
		RessourceManager rm = gameState.getRessourceManager();
		final int otherPlayerID = 3;
		TrackKind currentColor = TrackKind.ALL;
		
		// One or Two TrackKind.ALL must be in the open deck
		// available, because player must be able to draw
		int countAllCard = rm.existsCard(currentColor);
		while (countAllCard < 1) {
			rm.refillOpenDeck();
			countAllCard = rm.existsCard(currentColor);
		}
		
		int[] listIds = { 2, 3, 33, 89, 109, 125 };
		cgc = (ClientGameController) setupGameControllerWithPlayers(cgc, listIds);
		cgc.setID(OWN_ID);
		cgc.setCurrentPlayerIndex(3);
		cgc.setLastValidAction(null);
//		cgc.drawCard(otherPlayerID, currentColor);
		
		Assert.assertTrue(cgc.getLastValidAction() == null);
		
		// If the player draws a card either one card of the same
		// type will be refilled or another. So it must be either
		// equal the former size or one less
		Assert.assertTrue(rm.existsCard(currentColor) == countAllCard
							|| rm.existsCard(currentColor) == countAllCard - 1);
		
		Assert.assertTrue(cgc.getFailureCount() == 0);
		
		// Test other Player
		Player player = gameState.getPlayerFromId(otherPlayerID);
		HashMap<TrackKind, Integer> prMap = player.getRessourceMap();
		Assert.assertTrue(player.getMissionList().size() == 0);
		Assert.assertTrue(player.getScore() == 0);
		Assert.assertTrue(player.getTrackCounter() == 0);
		
		// Test own Player
		player = gameState.getPlayerFromId(OWN_ID);
		prMap = player.getRessourceMap();
		Assert.assertTrue(player.getMissionList().size() == 0);
		Assert.assertTrue(player.getScore() == 0);
		Assert.assertTrue(player.getSumRessourceCards() == 0);
		Assert.assertTrue(player.getTrackCounter() == 0);
	}
	
	/**
	 * Scenario:
	 * 	It's this clients turn. This player builds a track.
	 * 	The track costs 2 and he gains 2 points.
	 */
	@Test
	public void testBuildTrack() throws IOException {
		
		final short trackID = 1;
		TrackKind desiredColor = TrackKind.ALL;
		
		ClientGameController cgc = getClientGameController();
		int[] listIds = { 2, 3, 33, 89, 109, 125 };
		cgc = (ClientGameController) setupGameControllerWithPlayers(cgc, listIds);
		cgc.setID(OWN_ID);
		cgc.setCurrentPlayerIndex(1);
		
		super.testBuildTrack(cgc, OWN_ID, trackID, desiredColor);
		
	}
	
	/**
	 * Scenario:
	 * 	It's this clients turn. This player draws a secret card.
	 * 
	 */
	@Test
	public void testDrawSecretCard() throws IOException {
		
		ClientGameController cgc = getClientGameController();
		int[] listIds = { 2, 3, 33, 89, 109, 125 };
		cgc = (ClientGameController) setupGameControllerWithPlayers(cgc, listIds);
		cgc.setID(OWN_ID);
		cgc.setCurrentPlayerIndex(1);
		cgc.setLastValidAction(null);
		
		super.testDrawSecretCard(cgc, OWN_ID);
		
	}
	
	/**
	 * Scenario:
	 * 	It's another clients turn. This player draws a secret card.
	 * 	We expect changes but just in sumRessourceCards, because
	 * 	we don't know which card was taken.
	 */
	@Test
	public void testDrawSecretCardAnotherPlayer() throws IOException {
		
		final int otherPlayerID = 2;
		
		ClientGameController cgc = getClientGameController();
		int[] listIds = { 2, 3, 33, 89, 109, 125 };
		cgc = (ClientGameController) setupGameControllerWithPlayers(cgc, listIds);
		cgc.setID(0);
		cgc.setCurrentPlayerIndex(1);	// another player
		cgc.setLastValidAction(null);
		
		super.testDrawSecretCardWrongPlayer(cgc, OWN_ID, otherPlayerID);
		
	}
	
}
