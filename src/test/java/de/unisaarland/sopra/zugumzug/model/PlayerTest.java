package de.unisaarland.sopra.zugumzug.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.unisaarland.sopra.TrackKind;

public class PlayerTest {

	private Player p1;

	@Before
	public void setUp() {
		p1 = new Player(1, "Frank");
	}

	@Test
	public void testinitial() {

		assertTrue("Player Init - Wrong name", p1.getName() == "Frank");
		assertTrue("Player Init - Wrong PID", p1.getPid() == 1);
		assertTrue("Player Init - Wrong Score", p1.getScore() == 0);
		assertTrue("Player Init - Wrong SumRessourceCards", p1.getSumRessourceCards() == 0);
		assertTrue("Player Init - Wrong TrackCounter", p1.getTrackCounter() == 0);
		assertTrue("Player Init - ResourceMap null", p1.getRessourceMap() != null);
		assertTrue("Player Init - MissionList null", p1.getMissionList() != null);

	}

	@Test
	public void testresourceManagerInitial() {

		HashMap<TrackKind, Integer> rm = p1.getRessourceMap();
		assertTrue("Player ResourceMap - not all colours are initialized", rm.size() == 9);
		assertTrue("Player ResourceMap - Black not 0", rm.get(TrackKind.BLACK).intValue() == 0);
		assertTrue("Player ResourceMap - All not 0", rm.get(TrackKind.ALL).intValue() == 0);
		assertTrue("Player ResourceMap - Blue not 0", rm.get(TrackKind.BLUE).intValue() == 0);
		assertTrue("Player ResourceMap - Green not 0", rm.get(TrackKind.GREEN).intValue() == 0);
		assertTrue("Player ResourceMap - Red not 0", rm.get(TrackKind.RED).intValue() == 0);
		assertTrue("Player ResourceMap - Orange not 0", rm.get(TrackKind.ORANGE).intValue() == 0);
		assertTrue("Player ResourceMap - Yellow not 0", rm.get(TrackKind.YELLOW).intValue() == 0);
		assertTrue("Player ResourceMap - White not 0", rm.get(TrackKind.WHITE).intValue() == 0);
		assertTrue("Player ResourceMap - Violet not 0", rm.get(TrackKind.VIOLET).intValue() == 0);

	}

	public void testgetSumResourceCards() {

		HashMap<TrackKind, Integer> rm = p1.getRessourceMap();

		rm.put(TrackKind.RED, new Integer(5));
		assertTrue("Wrong Number of Resource Cards Displayed", p1.getSumRessourceCards() == 5);
		assertTrue("Wrong Number of Red Cards", rm.get(TrackKind.RED) == 5);
		rm.put(TrackKind.GREEN, new Integer(7));
		rm.put(TrackKind.ALL, new Integer(45));
		rm.put(TrackKind.YELLOW, new Integer(1));
		rm.put(TrackKind.BLACK, new Integer(9));
		rm.put(TrackKind.WHITE, new Integer(3));
		assertTrue("Wrong Number of Resource Cards Displayed", p1.getSumRessourceCards() == 70);

	}

	@Test
	public void testtrackCounter() {

		p1.incrementTrackCounter(4);
		assertTrue("Player - Wrong TrackCounter", p1.getTrackCounter() == 4);
		p1.incrementTrackCounter(5);
		assertTrue("Player - Wrong TrackCounter", p1.getTrackCounter() == 9);

	}

	@Test
	public void testupdateScore() {

		p1.updateScore(4);
		assertEquals(4, p1.getScore());
		p1.updateScore(5);
		assertEquals(9, p1.getScore());
		p1.updateScore(-13);
		assertEquals(-4, p1.getScore());


	}

	@Test
	public void testgiveResourceCards() {

		HashMap<TrackKind, Integer> rm = p1.getRessourceMap();

		p1.giveRessourceCards(TrackKind.BLACK, 1);
		assertTrue("Wrong Number of Resource Cards Displayed", p1.getSumRessourceCards() == 1);
		assertTrue("Wrong Number for Red Cards", rm.get(TrackKind.BLACK) == 1);

	}

	@Test
	public void testgiveMissions() {

		assertTrue(p1.getMissionList().isEmpty());
		Set<Mission> missions = new HashSet<Mission>();
		Mission mis = new Mission(0, 0, (short) 3, (short) 4);
		missions.add(mis);
		p1.giveMissions(missions);
		assertTrue(!p1.getMissionList().isEmpty());
		assertTrue(p1.getMissionList().get(0).equals(mis));

	}

}
