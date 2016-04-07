package de.unisaarland.sopra.zugumzug.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import de.unisaarland.sopra.MersenneTwister;

public class MissionManager {
	
	private class MissionComparator implements Comparator<Mission> {

		@Override
		public int compare(Mission m1, Mission m2) {
			return Integer.compare(m1.id, m2.id);
		}
		
	}
	
	int numberOfMissions = 0;

	private final List<Mission> closedMissionDeck = new ArrayList<Mission>();
	private final HashMap<Mission, Integer> mapMissionsId = new HashMap<>();
	
	private final MersenneTwister random;
	
	private final MissionComparator comparator = new MissionComparator();

	public MissionManager(MersenneTwister r) {
		this.random = r;
	}

	public Mission drawMission() {
		if(closedMissionDeck.isEmpty()) {
			throw new IllegalArgumentException("no exception expected");
		}
		int index = random.nextInt(closedMissionDeck.size() - 1);
		numberOfMissions--;
		return closedMissionDeck.remove(index);
	}

	public boolean isEmpty() {
		return numberOfMissions == 0;
	}

	/**
	 * returnMissions(List<Mission>) will be called in
	 * the initialization stage because the list is sorted.
	 * Converting to a Set would just cause performance
	 * disadvantages.
	 * 
	 * @param missionList
	 */
	public void returnMissions(List<Mission> missionList) {
		for(Mission m : missionList) {
			closedMissionDeck.add(
						getMissionFromId(m));
		}
		numberOfMissions += missionList.size();
		closedMissionDeck.sort(comparator);
	}
	
	/**
	 * returnMissions(Set<Mission>) will be called during
	 * the game because the player will return only Sets of
	 * missions.
	 * 
	 * Call insertMissions if you want to add a mission for
	 * the first time!
	 * 
	 * @param missionList
	 */
	public void returnMissions(Set<Mission> missionList) {
		for(Mission m : missionList) {
			closedMissionDeck.add(
						getMissionFromId(m));
		}
		numberOfMissions += missionList.size();
		closedMissionDeck.sort(comparator);
	}

	public List<Mission> getClosedMissionDeck() {
		return this.closedMissionDeck;
	}
	
	/**
	 * Updates NumberOfMissions from other players in Client,
	 * because the client doesn't know what cards other players have
	 * @param increm
	 */
	public void incrementNumberOfMissions(int increm) {
		this.numberOfMissions += increm;
	}

	public int getNumberOfMissions() {
		return numberOfMissions;
	}
	
	public Mission getMissionFromId(Mission m) {
		return new Mission(mapMissionsId.get(m), m.getPoints(), m.getCityA(), m.getCityB());
	}
	
	/**
	 * This is a wrapper method for MapFactory to add Missions to
	 * the mapMissionId HashMap to insert it initially.
	 * 
	 * @param missionList
	 */
	public void insertMissions(List<Mission> missionList) {
		for (Mission m : missionList)
			mapMissionsId.put(m, m.id);
		returnMissions(missionList);
	}

}