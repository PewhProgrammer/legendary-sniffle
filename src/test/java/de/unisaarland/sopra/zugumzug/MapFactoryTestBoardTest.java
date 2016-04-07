package de.unisaarland.sopra.zugumzug;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.unisaarland.sopra.MersenneTwister;
import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.MapFactory;
import de.unisaarland.sopra.zugumzug.model.City;
import de.unisaarland.sopra.zugumzug.model.GameState;
import de.unisaarland.sopra.zugumzug.model.Mission;
import de.unisaarland.sopra.zugumzug.model.Track;

public class MapFactoryTestBoardTest {

	GameState gs = new GameState();
	Mission m1, m2, m3, m4;
	City city0, city1, city2, city3;
	Track track0, track1, track2, track3;
	HashMap<Short, City> cityMap;
	HashMap<Short, Track> trackMap;
	List<Mission> missionList;
	HashMap<TrackKind, Integer> colorMap;

	MapFactory mapFactory = new MapFactory();
	
	@Before
	public void setup() {

		MersenneTwister rng = new MersenneTwister(42);
		try {
			mapFactory.initializeGameState("./src/Mapfiles/TestBoard", 1, rng, gs);
		} catch (IOException e) {
			fail("No exception expected");
		}

		assertNotNull(gs);

		cityMap = gs.getGameBoard().getCityMap();
		trackMap = gs.getGameBoard().getTrackMap();
		missionList = gs.getMissionManager().getClosedMissionDeck();
		colorMap = gs.getRessourceManager().getClosedRessourceDeck();

		// cities
		city0 = new City((short) 0, 1, 1, "Oberhausen");
		city1 = new City((short) 1, 3, 1, "Essen");
		city2 = new City((short) 2, 5, 2, "Bochum");
		city3 = new City((short) 3, 2, 3, "Berlin");

		// tracks
		track0 = new Track((short) 0, TrackKind.ALL, cityMap.get((short) 0), cityMap.get((short) 1), 2, false);
		track1 = new Track((short) 1, TrackKind.ALL, cityMap.get((short) 1), cityMap.get((short) 2), 2, false);
		track2 = new Track((short) 2, TrackKind.BLACK, cityMap.get((short) 2), cityMap.get((short) 3), 3, true);
		track3 = new Track((short) 3, TrackKind.RED, cityMap.get((short) 3), cityMap.get((short) 1), 3, true);

		// add tracks to cities
		city0.addTrack(track0);
		city1.addTrack(track0);
		city1.addTrack(track1);
		city1.addTrack(track3);
		city2.addTrack(track1);
		city2.addTrack(track2);
		city3.addTrack(track2);
		city3.addTrack(track3);

		// missions
		m1 = new Mission(0, 4, (short) 0, (short) 2);
		m2 = new Mission(1, 6, (short) 3, (short) 1);
		m3 = new Mission(2, 5, (short) 0, (short) 3);
		m4 = new Mission(3, 8, (short) 1, (short) 2);
	}

	@Test
	public void cardTest() {
		assertTrue(colorMap.size() == 9);
		assertTrue(colorMap.get(TrackKind.ALL) == 7);
		assertTrue(colorMap.get(TrackKind.BLACK) == 6);
		assertTrue(colorMap.get(TrackKind.BLUE) == 6);
		assertTrue(colorMap.get(TrackKind.GREEN) == 6);
		assertTrue(colorMap.get(TrackKind.ORANGE) == 6);
		assertTrue(colorMap.get(TrackKind.RED) == 6);
		assertTrue(colorMap.get(TrackKind.WHITE) == 6);
		assertTrue(colorMap.get(TrackKind.YELLOW) == 6);
		assertTrue(colorMap.get(TrackKind.VIOLET) == 6);
	}

	@Test
	public void missionTest() {
		assertEquals(missionList.size(), 4);
		assertTrue(missionList.contains(m1));
		assertTrue(missionList.contains(m2));
		assertTrue(missionList.contains(m3));
		assertTrue(missionList.contains(m4));
	}

	@Test
	public void trackTest() {
		assertEquals(gs.getMaxTracks(), 8);
		assertEquals(trackMap.size(), 4);
		assertTrue(trackMap.containsValue(track0));
		assertTrue(trackMap.containsValue(track1));
		assertTrue(trackMap.containsValue(track2));
		assertTrue(trackMap.containsValue(track3));
	}

	@Test
	public void cityTest() {
		assertEquals(cityMap.size(), 4);
		assertTrue(cityMap.containsValue(city0));
		assertTrue(cityMap.containsValue(city1));
		assertTrue(cityMap.containsValue(city2));
		assertTrue(cityMap.containsValue(city3));
	}

}
