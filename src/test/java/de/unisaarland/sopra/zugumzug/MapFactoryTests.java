package de.unisaarland.sopra.zugumzug;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.unisaarland.sopra.MersenneTwister;
import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.model.GameState;

public class MapFactoryTests {

	GameState gs;
	MersenneTwister rng;
	MapFactory mapFactory = new MapFactory();

	@Before
	public void setup() {
		gs = new GameState();
		rng = new MersenneTwister(42);
	}

	@Test
	public void testFileSmallBoard() throws IOException {

		GameState gameState = new GameState();
		mapFactory.initializeGameState("./src/MapFiles/SmallBoard", 1, rng, gameState);

		assertNotNull(gameState);
		assertNotNull(gameState.getMissionManager());
		assertNotNull(gameState.getRessourceManager());
		assertNotNull(gameState.getGameBoard());
		assertTrue(gameState.getMaxTracks() == 8);
		
		assertEquals(gameState.getGameBoard().getCityMap().size(), 6);
		assertEquals(gameState.getGameBoard().getTrackMap().size(), 8);
		assertEquals(gameState.getMissionManager().getClosedMissionDeck().size(), 7);
		assertEquals(gameState.getRessourceManager().getClosedRessourceDeck().size(), 9);
	}

	
	//////////////////////////////////
	//	Simulated Nightly Tests
	//////////////////////////////////
	
	/**
	 * Nightly: IllegalMapNoLimitsTest
	 */
	@Test
	public void testIllegalMapNoLimitsTest1() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/NightlyTests/IllegalMapNoLimits1", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("Expected IllegalArgumentException");
	}
	
	/**
	 * Nightly: IllegalMapNoLimitsTest
	 */
	@Test
	public void testIllegalMapNoLimitsTest2() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/NightlyTests/IllegalMapNoLimits2", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("Expected IllegalArgumentException");
	}
	
	/**
	 * Nightly: IllegalMapWrongColorsTest
	 * Wrong color in map
	 */
	@Test
	public void testIllegalMapWrongColorsTest1() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/NightlyTests/IllegalMapWrongColorsTest1", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("Expected IllegalArgumentException");
	}
	
	/**
	 * Nightly: IllegalMapWrongColorsTest
	 * missing color
	 */
	@Test
	public void testIllegalMapWrongColorsTest2() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/NightlyTests/IllegalMapWrongColorsTest2", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("Expected IllegalArgumentException");
	}
	
	/**
	 * Nightly: IllegalMapMaxTracks2Test
	 * missing number
	 */
	@Test
	public void testIllegalMapMaxTracks2Test1() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/NightlyTests/IllegalMapMaxTracks2Test1", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("Expected IllegalArgumentException");
	}
	
	/**
	 * Nightly: IllegalMapMaxTracks2Test
	 * missing MaxTrack line
	 */
	@Test
	public void testIllegalMapMaxTracks2Test2() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/NightlyTests/IllegalMapMaxTracks2Test2", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("Expected IllegalArgumentException");
	}
	
	/**
	 * Nightly: IllegalMapNegativeCostsTest
	 * negative cost for track
	 */
	@Test
	public void testIllegalMapNegativeCostsTest() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/NightlyTests/IllegalMapNegativeCostsTest1", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("Expected IllegalArgumentException");
	}
	
	/**
	 * Nightly: IllegalMapNegativeCostsTest
	 * zero cost for track
	 */
	@Test
	public void testIllegalMapZeroCostsTest() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/NightlyTests/IllegalMapZeroCostsTest", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("Expected IllegalArgumentException");
	}
	
	
	//////////////////////////////////
	//	Own Tests
	//////////////////////////////////
	
	@Test
	public void testNegativeColor() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/SmallBoard_NegativeColor", 1, rng, gs);
		} catch (IOException e) {
			fail("No Exception expected");
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
			fail("No Exception expected");
		}
	}
	
	@Test
	public void notExistingFileTest() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/DasistEinBingoooo", 1, rng, gs);
		} catch (IOException e) {
			return;
		} catch (IllegalArgumentException e) {
			fail("Expected IOException");
		}
		
		fail("IOException expected");
	}

	@Test
	public void wrongCityIDInTrackTest() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/BrokenBoard_WrongCityIDInTrack", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("IllegalArgumentException expected");
	}

	@Test
	public void wrongCityIDInMissionTest() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/BrokenBoard_WrongCityIDInMission", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("IllegalArgumentException expected");
	}

	@Test
	public void noMissionTest() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/BrokenBoard_NoMissions", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("IllegalArgumentException expected");
	}

	@Test
	public void missionImpossibleTest() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/BrokenBoard_MissionImpossible", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("IllegalArgumentException expected");
	}

	@Test
	public void noTracksTest() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/SmallBoard_NoTracks", 0, rng, gs);
			mapFactory.initializeGameState("./src/Mapfiles/SmallBoard_NoTracks", 1, rng, gs);
		} catch (IOException e) {
			fail("No Exception Excepted");
		} catch (IllegalArgumentException e) {
			fail("No Exception Excepted");
		}
	}
	
	@Test
	public void tooLessCards() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/SmallBoard_TooLessCards", 3, rng, gs);
		} catch (IOException e) {
			fail("No Exception Excepted");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail("No Exception Excepted");
		}
		
		try {
			mapFactory.initializeGameState("./src/Mapfiles/SmallBoard_TooLessCards", 4, rng, gs);
		} catch (IOException e) {
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("IllegalArgumentException expected");
	}

	@Test
	public void lessMissionTest() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/BrokenBoard_LessMission", 10, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("IllegalArgumentException expected");
	}

	@Test
	public void missingSignatureTest() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/BrokenBoard_MissingSignature", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("IllegalArgumentException expected");
	}

	@Test
	public void missingTunnelBooleanTest() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/BrokenBoard_MissingTunnelBoolean", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("IllegalArgumentException expected");
	}

	@Test
	public void wrongColorRedTest() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/BrokenBoard_WrongColorRED", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("IllegalArgumentException expected");
	}

	@Test
	public void zeroTest() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/SmallBoard_FromZeroToHero", 0, rng, gs);
		} catch (IOException e) {
			fail("No exception expected");
		} catch (IllegalArgumentException e) {
			fail("No exception expected1");
		}
	}

	@Test
	public void sameMissionTest() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/BrokenBoard_SameMission", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("IllegalArgumentException expected");
	}	
	
	@Test
	public void equalMissionTest() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/SmallBoard_EqualMissions", 0, rng, gs);
		} catch (IOException e) {
			fail("No exception expected");
		} catch (IllegalArgumentException e) {
			fail("No exception expected");
		}
	}

	@Test
	public void mapAuftraegeTest() {
		try {
			mapFactory.initializeGameState("./src/TestScript/Maps/map-Auftraege", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("IllegalArgumentException expected");
	}

	/**
	 * No BLACK cards are defined, mapFactory has to produce
	 * an IllegalArgumentException
	 */
	@Test
	public void mapBlackTest() {
		try {
			mapFactory.initializeGameState("./src/TestScript/Maps/map-BLACK", 1, rng, gs);
		} catch (IOException e) {
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("IllegalArgumentException expected");
	}

	@Test
	public void mapCityNoNameTest() {
		try {
			mapFactory.initializeGameState("./src/TestScript/Maps/map-CityNoName", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("IllegalArgumentException expected");
	}

	@Test
	public void mapCommentTest() {
		try {
			mapFactory.initializeGameState("./src/TestScript/Maps/map-Comment", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("IllegalArgumentException expected");
	}

	@Test
	public void mapGreenTest() {
		try {
			mapFactory.initializeGameState("./src/TestScript/Maps/map-GREEN", 1, rng, gs);
		} catch (IllegalArgumentException e) {
			return;
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		}
		
		fail("IllegalArgumentException expected");
	}

	@Test
	public void mapLastMissionPointsTest() {
		try {
			mapFactory.initializeGameState("./src/TestScript/Maps/map-LastMissionPoints", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("IllegalArgumentException expected");
	}

	@Test
	public void mapLastTrackTest() {
		try {
			mapFactory.initializeGameState("./src/TestScript/Maps/map-LastTrack", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("IllegalArgumentException expected");
	}

	@Test
	public void mapLastTrackSelfTest() {
		try {
			mapFactory.initializeGameState("./src/TestScript/Maps/map-LastTrackSelf", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("IllegalArgumentException expected");
	}
	
	@Test
	public void mapMaxTracksTest() {
		try {
			mapFactory.initializeGameState("./src/TestScript/Maps/map-MaxTracks", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("IllegalArgumentException expected");
	}

	@Test
	public void mapMaxTracksZeroTest() {
		try {
			mapFactory.initializeGameState("./src/TestScript/Maps/map-MaxTracksZero", 0, rng, gs);
		} catch (IOException e) {
			fail("No exception expected");
		} catch (IllegalArgumentException e) {
			fail("No exception expected");
		}
	}

	@Test
	public void mapStaedteTest() {
		try {
			mapFactory.initializeGameState("./src/TestScript/Maps/map-Staedte", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("IllegalArgumentException expected");
	}

	@Test
	public void mapTracksTest() {
		try {
			mapFactory.initializeGameState("./src/TestScript/Maps/map-Tracks", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("IllegalArgumentException expected");
	}

	@Test
	public void mapYellowTest() {
		try {
			mapFactory.initializeGameState("./src/TestScript/Maps/map-YELLOW", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}
		
		fail("IllegalArgumentException expected");
	}

	@Test
	public void commentTest() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/BrokenBoard_Comment", 1, rng, gs);
		} catch (IOException e) {
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			return;
		}

		fail("IllegalArgumentException expected");
	}

	@Test
	public void parallelTracksTest() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/SmallBoard_ParallelTracks", 1, rng, gs);
		} catch (IOException e) {
			fail("No exception expected");
		}
		assertNotNull(gs);
		assertEquals(gs.getGameBoard().getCityMap().size(), 6);
		assertEquals(gs.getGameBoard().getTrackMap().size(), 13);
		assertEquals(gs.getMaxTracks(), 8);
		assertEquals(gs.getMissionManager().getClosedMissionDeck().size(), 6);
		assertEquals(gs.getRessourceManager().getClosedRessourceDeck().size(), 9);
	}

	@Test
	public void hugeCoordinateTest() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/SmallBoard_HugeCoordinate", 1, rng, gs);
		} catch (IOException e) {
			fail("No exception expected");
		}
		assertNotNull(gs);
		assertEquals(gs.getGameBoard().getCityMap().size(), 6);
		assertEquals(gs.getGameBoard().getTrackMap().size(), 8);
		assertEquals(gs.getMaxTracks(), 8);
		assertEquals(gs.getMissionManager().getClosedMissionDeck().size(), 7);
		assertEquals(gs.getRessourceManager().getClosedRessourceDeck().size(), 9);
	}

	@Test
	public void testYellowNegative() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/BrokenBoard_YellowNegative", 1, rng, gs);
		} catch (IOException e) {
			fail("No exception expected");
		} catch (IllegalArgumentException e) {
			fail("No exception expected");
		}
	}

	@Test
	public void sameCitiesTest() {
		try {
			mapFactory.initializeGameState("./src/Mapfiles/SmallBoard_SameCityname", 1, rng, gs);
		} catch (IOException e) {
			fail("No exception expected");
		}
		assertNotNull(gs);
		assertEquals(gs.getGameBoard().getCityMap().size(), 6);
		assertEquals(gs.getGameBoard().getTrackMap().size(), 8);
		assertEquals(gs.getMaxTracks(), 8);
		assertEquals(gs.getMissionManager().getClosedMissionDeck().size(), 7);
		assertEquals(gs.getRessourceManager().getClosedRessourceDeck().size(), 9);
	}

	@Test
	public void hugeMapTest() {
		try {
			mapFactory.initializeGameState("./src/TestScript/Maps/map-Fehlerfrei", 1, rng, gs);
		} catch (IOException e) {
			fail("No exception expected");
		}
		assertNotNull(gs);
		assertEquals(gs.getGameBoard().getCityMap().size(), 2050);
		assertEquals(gs.getGameBoard().getTrackMap().size(), 2499);
		assertEquals(gs.getMaxTracks(), 2507);
		assertEquals(gs.getMissionManager().getClosedMissionDeck().size(), 1000);
		assertEquals(gs.getRessourceManager().getClosedRessourceDeck().size(), 9);
		assertEquals(gs.getRessourceManager().getClosedRessourceDeck().get(TrackKind.ALL).intValue(), 258);
	}

}
