package de.unisaarland.sopra.zugumzug.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GameBoard {

	private final HashMap<Short, City> cityMap = new HashMap<>();
	private final HashMap<Short, Track> trackMap = new HashMap<>();

	public GameBoard() {
	}

	public HashMap<Short, City> getCityMap() {
		return cityMap;
	}

	public HashMap<Short, Track> getTrackMap() {
		return trackMap;
	}

	public Set<City> getCitySet() {
		Set<City> cSet = new HashSet<City>();
		for (short cid : cityMap.keySet()) {
			cSet.add(this.cityMap.get(cid));
		}
		return cSet;
	}

	public Set<City> getCitySet(int pid) {
		Set<City> cSet = new HashSet<City>();
		for (Track t : this.getTrackSet(pid)) {
			cSet.add(t.getCityA());
			cSet.add(t.getCityB());
		}
		return cSet;
	}

	public Set<Track> getTrackSet() {
		Set<Track> tSet = new HashSet<Track>();
		for (short tid : trackMap.keySet()) {
			tSet.add(this.trackMap.get(tid));
		}
		return tSet;
	}

	public Set<Track> getTrackSet(int pid) {
		Set<Track> tSet = new HashSet<Track>();
		for (short tid : trackMap.keySet()) {
			Track t = this.trackMap.get(tid);
			if (t.getOwner() == pid)
				tSet.add(t);
		}
		return tSet;
	}

	public void addCity(City city) {
		assert!this.cityMap.containsValue(city);
		this.cityMap.put(city.getID(), city);
	}

	public void addTrack(Track track) {
		City cityA = track.getCityA();
		City cityB = track.getCityB();
		this.trackMap.put(track.getID(), track);
		cityA.addTrack(track);
		cityB.addTrack(track);
	}

	public void buildTrack(final short tid, Player p) {
		if (!trackMap.containsKey(tid))
			throw new IllegalArgumentException();
		if (trackMap.get(tid).getOwner() != Track.NO_OWNER)
			throw new IllegalArgumentException();
		if (trackMap.get(tid).isParallelTrackOwned(p.getPid()))
			throw new IllegalArgumentException();

		this.trackMap.get(tid).setOwner(p.getPid());

		p.incrementTrackCounter(this.trackMap.get(tid).getCosts());
	}

	public int getOwnerFromTrack(final short tid) {
		return this.trackMap.get(tid).getOwner();
	}

	public Track getTrackFromId(final short tid) {
		return this.trackMap.get(tid);
	}

	public City getCityFromId(final short cid) {
		return this.cityMap.get(cid);
	}

	/**
	 * 
	 * @return a Coordinate with X = North and Y = East. The Coordinate is the
	 *         NW-Corner of the Map
	 */
	public Coordinate getNW() {
		int x = Integer.MAX_VALUE;
		int y = Integer.MAX_VALUE;
		for (City c : this.getCitySet()) {
			x = (x < c.getX()) ? x : c.getX();
			y = (y < c.getY()) ? y : c.getY();
		}
		return new Coordinate(x, y);
	}

	/**
	 * 
	 * @return a Coordinate with X = South and Y = West. The Coordinate is the
	 *         SE-Corner of the Map
	 */
	public Coordinate getSE() {
		int x = Integer.MIN_VALUE;
		int y = Integer.MIN_VALUE;
		for (City c : this.getCitySet()) {
			x = (x > c.getX()) ? x : c.getX();
			y = (y > c.getY()) ? y : c.getY();
		}
		return new Coordinate(x, y);
	}

	// TODO:
	public int getLongestTrackFromPlayer(int pid) {
		Set<City> cSet = this.getCitySet();
		Set<Track> tSet = this.getTrackSet();

		// filter out cities unreachable by player
		Iterator<City> cit = cSet.iterator();
		while (cit.hasNext()) {
			City c = cit.next();
			if (c.getReachable(pid).isEmpty())
				cit.remove();
		}

		// filter out tracks not owned by the player
		Iterator<Track> tit = tSet.iterator();
		while (tit.hasNext()) {
			Track t = tit.next();
			if (t.getOwner() != pid)
				tit.remove();
		}

		// call longestTrack for all Cities
		cit = cSet.iterator();
		int max = 0;
		while (cit.hasNext()) {
			City c = cit.next();
			Set<Track> Tracks = new HashSet<Track>(tSet);
			int i = longestTrack(Tracks, new HashSet<Track>(), c);
			max = (max < i) ? i : max;
		}

		return max;
	}

	private int longestTrack(Set<Track> white, Set<Track> black, City c) {

		// get all possible exits from this City
		Set<Track> fringe = new HashSet<Track>(c.getTrackSet());
		fringe.retainAll(white);
		fringe.removeAll(black);

		int max = 0;

		Iterator<Track> it = fringe.iterator();

		while (it.hasNext()) {
			Track t = it.next();
			City nc = t.getOther(c);
			Set<Track> orange = new HashSet<Track>(black);
			orange.add(t);
			int i = longestTrack(white, orange, nc) + t.getCosts();
			max = (max < i) ? i : max;
		}

		return max;
	}

	public boolean isMissionAccomplished(int pid, short cidA, short cidB) {
		// start a breadth first search. If it is found (distance >= 0), it can
		// be reached.
		return bfSearch(cidA, cidB, pid) >= 0;
	}

	/**
	 * 
	 * @param pid
	 * @param cidA
	 * @param cidB
	 * @return returns true, if the mission cannot be accomplished by building
	 *         empty tracks and using the tracks the player already owns
	 */
	public boolean isMissionImpossible(int pid, short cidA, short cidB) {
		return bfSearchBuild(cidA, cidB, pid) < 0;
	}

	/**
	 * breadth-first search including only tracks from one player
	 * 
	 * @param source,
	 *            dest, pid
	 * @return -1 if the destination was not found, distance if the destination
	 *         was reached
	 * 
	 */
	public int bfSearchBuild(short source, short dest, int pid) {

		Set<Short> fringe = new HashSet<Short>();
		fringe.add(source);
		Set<Short> visited = new HashSet<Short>();
		return bfsb(fringe, visited, dest, pid);
	}

	private int bfsb(Set<Short> fringe, Set<Short> visited, short dest, int pid) {
		if (fringe.isEmpty()) // Node not found
			return -1;
		if (fringe.contains(dest))
			return 0;
		visited.addAll(fringe); // update visited
		City city;
		Set<Short> newfringe = new HashSet<Short>();
		for (short cid : fringe) { // update fringe
			city = this.getCityFromId(cid);
			newfringe.addAll(city.getReachableBuild(pid));
		}
		newfringe.removeAll(visited);
		int next = bfsb(newfringe, visited, dest, pid);
		return (next == -1) ? -1 : next + 1;
	}

	/**
	 * breadth-first search including all tracks
	 * 
	 * @param source,
	 *            dest
	 * @return -1 if the destination was not found, distance if the destination
	 *         was reached
	 * 
	 */
	public int bfSearch(short source, short dest) {

		Set<Short> fringe = new HashSet<Short>();
		fringe.add(source);
		Set<Short> visited = new HashSet<Short>();
		return bfs(fringe, visited, dest);
	}

	private int bfs(Set<Short> fringe, Set<Short> visited, short dest) {
		if (fringe.isEmpty()) // Node not found
			return -1;
		if (fringe.contains(dest))
			return 0;
		visited.addAll(fringe); // update visited
		City city;
		Set<Short> newfringe = new HashSet<Short>();
		for (short cid : fringe) { // update fringe
			city = this.getCityFromId(cid);
			newfringe.addAll(city.getReachable());
		}
		newfringe.removeAll(visited);
		int next = bfs(newfringe, visited, dest);
		return (next == -1) ? -1 : next + 1;
	}

	/**
	 * breadth-first search including only tracks from one player
	 * 
	 * @param source,
	 *            dest, pid
	 * @return -1 if the destination was not found, distance if the destination
	 *         was reached
	 * 
	 */
	public int bfSearch(short source, short dest, int pid) {

		Set<Short> fringe = new HashSet<Short>();
		fringe.add(source);
		Set<Short> visited = new HashSet<Short>();
		return bfs(fringe, visited, dest, pid);
	}

	private int bfs(Set<Short> fringe, Set<Short> visited, short dest, int pid) {
		if (fringe.isEmpty()) // Node not found
			return -1;
		if (fringe.contains(dest))
			return 0;
		visited.addAll(fringe); // update visited
		City city;
		Set<Short> newfringe = new HashSet<Short>();
		for (short cid : fringe) { // update fringe
			city = this.getCityFromId(cid);
			newfringe.addAll(city.getReachable(pid));
		}
		newfringe.removeAll(visited);
		int next = bfs(newfringe, visited, dest, pid);
		return (next == -1) ? -1 : next + 1;
	}

}
