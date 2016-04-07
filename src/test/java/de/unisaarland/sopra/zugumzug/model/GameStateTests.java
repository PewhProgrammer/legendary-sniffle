package de.unisaarland.sopra.zugumzug.model;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.unisaarland.sopra.MersenneTwister;

public class GameStateTests {

	private GameState gs;

	@Before
	public void setUp() {

		gs = new GameState();
		gs.setGameBoard(new GameBoard());
		gs.setMaxTracks(10);
		gs.setMissionManager(new MissionManager(new MersenneTwister(0)));
		gs.setRessourceManager(new RessourceManager(new MersenneTwister(0)));
		gs.setMaxHandSize(23);

	}

	@Test
	public void testInitial() {

		assertTrue(gs.getMaxTracks() == 10);
		assertTrue(gs.getMaxHandSize() == 23);
		assertTrue(!gs.getGameBoard().equals(null));
		assertTrue(!gs.getMissionManager().equals(null));
		assertTrue(!gs.getRessourceManager().equals(null));

	}

	@Test
	public void testAddGetPlayer() {

		Player p1 = new Player(1, "Frank");
		gs.addPlayerInPlayerMap(p1);
		assertTrue(gs.getPlayerFromId(2) == null);
		assertTrue(gs.getPlayerFromId(0) == null);
		assertTrue(gs.getPlayerFromId(1).equals(p1));

	}

}
