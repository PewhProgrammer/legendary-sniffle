package de.unisaarland.sopra.zugumzug.model;

import static org.junit.Assert.*;

import java.io.IOException;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import de.unisaarland.sopra.MersenneTwister;
import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.MapFactory;

public class GameBoardTests {

	GameBoard gb;
	GameState gs;
	City cityA;
	Track t, t0;
	int sizeCityList;
	Player p;
	MapFactory mapFactory = new MapFactory();

	@Before
	public void setup() throws IOException {
		p = new Player(2, "Frank");
		cityA = new City((short) 6, 2, 3, "Berlin");
		MersenneTwister rng = new MersenneTwister(888);
		gs = new GameState();
		mapFactory.initializeGameState("./src/Mapfiles/Smallboard", 1, rng, gs);
		gs.addPlayerInPlayerMap(p);
		gb = gs.getGameBoard();
		t = new Track((short) 8, TrackKind.BLUE, gb.getCityMap().get((short) 0), cityA, 5, true);
		t0 = new Track((short) 8, TrackKind.BLUE, gb.getCityMap().get((short) 0), gb.getCityMap().get((short) 1), 4, true);
		sizeCityList = gb.getCitySet().size();
	}

	@Test
	public void buildTrackSimpleTest() {
		gb.buildTrack((short) 1, p);
		assertEquals(gb.getTrackMap().get((short) 1).getOwner(), 2);
		assertEquals(gb.getOwnerFromTrack((short) 1), 2);
	}

	@Test
	public void getLongestTrackSimpleTest() {
		gb.buildTrack((short) 0, p);
		gb.buildTrack((short) 2, p);
		gb.buildTrack((short) 1, p);
		assertEquals(gb.getLongestTrackFromPlayer(2), 7);
	}

	@Test
	public void getLongestTrackTest() {
		gb.buildTrack((short) 0, p);
		gb.buildTrack((short) 3, p);
		gb.buildTrack((short) 4, p);
		gb.buildTrack((short) 5, p);

		assertEquals(gb.getLongestTrackFromPlayer(2), 13);
	}

	@Test
	public void buildParallelTrack() {
		gb.addTrack(t0);
		gb.buildTrack((short) t0.getID(), p);
		try {
			gb.buildTrack((short) 0, p);
		} catch (IllegalArgumentException e) {

		}
		assertEquals(gb.getOwnerFromTrack(t0.getID()), 2);
		assertNotEquals(gb.getOwnerFromTrack((short) 0), 2);
	}

	@Test
	public void getLongestTrackAllTracksTest() {
		gb.buildTrack((short) (short) 0, p);
		gb.buildTrack((short) (short) 1, p);
		gb.buildTrack((short) (short) 2, p);
		gb.buildTrack((short) (short) 3, p);
		gb.buildTrack((short) (short) 4, p);
		gb.buildTrack((short) (short) 5, p);
		gb.buildTrack((short) (short) 6, p);
		gb.buildTrack((short) (short) 7, p);

		assertEquals(gb.getLongestTrackFromPlayer(2), 20);
	}

	@Test
	public void isMissionAccomplishedSimpleTest() {
		Mission m = new Mission(0, 4, (short) 0, (short) 1);
		LinkedList<Mission> list = new LinkedList<Mission>();
		list.add(m);
		gs.getMissionManager().insertMissions(list);
		gb.buildTrack((short) 0, p);

		assertTrue(gb.isMissionAccomplished((short)2, (short)0, (short)1));
	}

	@Test
	public void isMissionAccomplishedTest() {
		Mission m = new Mission(0, 4, (short) 0, (short) 4);
		LinkedList<Mission> list = new LinkedList<Mission>();
		list.add(m);
		gs.getMissionManager().insertMissions(list);
		gb.buildTrack((short) 7, p);
		gb.buildTrack((short) 6, p);
		gb.buildTrack((short) 1, p);
		gb.buildTrack((short) 0, p);

		assertTrue(gb.isMissionAccomplished((short)2, (short)0, (short)4));
	}
	
	@Test
	public void MissionLogfileTest(){
		Mission[] mis = new Mission[7];
		mis[0] = new Mission(0, 6, (short) 0, (short) 4);
		mis[1] = new Mission(1, 6, (short) 5, (short) 3);
		mis[2] = new Mission(2, 0, (short) 3, (short) 5);
		mis[3] = new Mission(3, 8, (short) 0, (short) 5);
		mis[4] = new Mission(4, 6, (short) 5, (short) 1);
		mis[5] = new Mission(5, 5, (short) 2, (short) 3);
		mis[6] = new Mission(6, 5, (short) 1, (short) 4);
		
		Player Test0 = new Player(0, "Test");
		Player Test1 = new Player(1, "Tests");
		
		gb.buildTrack((short) 7, Test0);
		gb.buildTrack((short) 3, Test1);
		
		
		for (int i = 0; i<4; i++){
			assertFalse(gb.isMissionAccomplished(0, mis[i].getCityA(), mis[i].getCityB()));
		}
		for (int i = 4; i<7; i++){
			assertFalse(gb.isMissionAccomplished(1, mis[i].getCityA(), mis[i].getCityB()));
		}
	}

	@Test
	public void isMissionNotAccomplishedTest() {
		Mission m = new Mission(0, 4, (short) 0, (short) 4);
		LinkedList<Mission> list = new LinkedList<Mission>();
		list.add(m);
		gs.getMissionManager().insertMissions(list);

		assertFalse(gb.isMissionAccomplished((short)2, (short)0, (short)4));
	}

	@Test
	public void addParallelTrackTest() {
		gb.addTrack(t0);

		assertEquals(gb.getTrackMap().size(), 9);
		assertTrue(gb.getTrackMap().containsKey((short) 8));
	}

	@Test
	public void addCityTest() {
		gb.addCity(cityA);

		assertEquals(gb.getCitySet().size(), sizeCityList + 1);
		assertTrue(gb.getCitySet().contains(cityA));
		assertTrue(gb.getCityMap().get((short) 6).equals(cityA));
	}

	@Test
	public void longestTrackZeroTest() {
		assertEquals(gb.getLongestTrackFromPlayer(2), 0);
	}

	@Test
	public void buildTrackAlreadyOwnedTest() {
		gb.buildTrack((short) 0, p);
		Player p1 = new Player(1, "Sinatra");

		try {
			gb.buildTrack((short) 0, p1);
		} catch (IllegalArgumentException e) {

		}

		assertEquals(gb.getOwnerFromTrack((short) 0), 2);
		assertNotEquals(gb.getOwnerFromTrack((short) 0), 1);
	}

	@Test
	public void buildTrackTwiceTest() {
		gb.buildTrack((short) 0, p);
		try {
			gb.buildTrack((short) 0, p);
		} catch (IllegalArgumentException e) {

		}

		assertEquals(gb.getLongestTrackFromPlayer(2), 2);
		assertEquals(p.getTrackCounter(), 2);
	}

	@Test
	public void notRelatedTracksTest() {
		gb.buildTrack((short) 0, p);
		gb.buildTrack((short) 7, p);

		assertEquals(gb.getLongestTrackFromPlayer(2), 2);
	}

	@Test
	public void getLongestTrackCircleTest() {
		gb.buildTrack((short) 0, p);
		gb.buildTrack((short) 4, p);
		gb.buildTrack((short) 5, p);

		assertEquals(gb.getLongestTrackFromPlayer(2), 10);
	}

	@Test
	public void buildTrackNegativeIdTest() {
		Player p1 = new Player(-1, "Sinatra");
		gb.buildTrack((short) 2, p1);
		assertEquals(gb.getTrackFromId((short) 2).getOwner(), -1);
	}

	@Test
	public void buildTrackInvalidTIDTest() {
		try {
			gb.buildTrack((short) 10, p);
		} catch (IllegalArgumentException e) {

		}

		assertEquals(p.getTrackCounter(), 0);
	}

}
