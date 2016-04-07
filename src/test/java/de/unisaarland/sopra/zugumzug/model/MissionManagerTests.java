package de.unisaarland.sopra.zugumzug.model;

import org.junit.*;

import de.unisaarland.sopra.MersenneTwister;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

public class MissionManagerTests {

	MissionManager mm;
	List<Mission> missionList;
	Mission mission = new Mission(0, 8, (short) 1, (short) 2);
	Mission mission2 = new Mission(0, 10, (short) 3, (short) 4);
	Mission mission3 = new Mission(1, 9, (short) 5, (short) 6);
	City A;
	City B;

	@Before
	public void runBeforeMissionManagerTests() {

		mm = new MissionManager(new MersenneTwister(0));
		A = new City((short) 1, 3, 4, "A");
		B = new City((short) 2, 2, 4, "B");
		missionList = new ArrayList<Mission>();
	}

	@Test
	public void testinsertMissionsEmpty() {
		mm.insertMissions(missionList);
		assertTrue(mm.isEmpty());
	}

	@Test
	public void testinsertMissionsSingle() {

		missionList.add(mission);
		mm.insertMissions(missionList);
		assertTrue(mm.isEmpty() == false);
	}

	@Test
	public void testinsertMissionsMore() {
		// Aufbau von 2 weiteren Missionen
		Mission mission2 = new Mission(0, 10, (short) 3, (short) 4);
		Mission mission3 = new Mission(1, 9, (short) 5, (short) 6);
		missionList.add(mission);
		missionList.add(mission2);
		missionList.add(mission3);
		mm.insertMissions(missionList);

		assertTrue(mm.isEmpty() == false);
	}

	@Test
	public void testIsEmptyTrue() {

		assertTrue(mm.isEmpty() == true);
	}

	@Test
	public void testIsEmptyFalse() {

		missionList.add(mission);
		mm.insertMissions(missionList);
		assertTrue(mm.isEmpty() == false);
	}

	@Test
	public void testIsEmptyFalse2() {
		// Aufbau von 2 weiteren Missionen; Test auf mehrfach gefuelltes Deck
		Mission mission2 = new Mission(0, 10, (short) 3, (short) 4);
		Mission mission3 = new Mission(1, 9, (short) 5, (short) 6);
		missionList.add(mission);
		missionList.add(mission2);
		missionList.add(mission3);
		mm.insertMissions(missionList);

		assertTrue(mm.isEmpty() == false);
	}

	@Test
	public void testDrawMissionSingle() {

		missionList.add(mission);
		mm.insertMissions(missionList);

		Mission t = mm.drawMission();
		assertTrue(t.getCityA() == 1 && t.getCityB() == 2 && t.getPoints() == 8);
	}

	@Test
	public void testDrawMissionRandom() {

		missionList.add(mission);
		missionList.add(mission2);
		missionList.add(mission3);
		
		for (int i = 0; i < 5; i++) {
			runDrawMissionRandom();
		}
	}

	public void runDrawMissionRandom() {

		mm.insertMissions(missionList);
		
		Mission drawn = mm.drawMission();
		assertTrue(drawn != null);
		// actual Test
		if (drawn.equals(mission)) {
			if (mm.drawMission().equals(mission2))
				assertTrue(mm.drawMission().equals(mission3));
			else if (mm.drawMission().equals(mission3))
				assertTrue(mm.drawMission().equals(mission2));
		} else if (drawn.equals(mission2)) {
			if (mm.drawMission().equals(mission))
				assertTrue(mm.drawMission().equals(mission3));
			else if (mm.drawMission().equals(mission3))
				assertTrue(mm.drawMission().equals(mission));
		} else {
			if (mm.drawMission().equals(mission))
				assertTrue(mm.drawMission().equals(mission2));
			else if (mm.drawMission().equals(mission2))
				assertTrue(mm.drawMission().equals(mission));
		}
	}
}