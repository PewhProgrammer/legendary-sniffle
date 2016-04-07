package de.unisaarland.sopra.zugumzug.AI.AIUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.actions.client.ReturnMissions;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.NewMissions;
import de.unisaarland.sopra.zugumzug.model.City;
import de.unisaarland.sopra.zugumzug.model.GameBoard;
import de.unisaarland.sopra.zugumzug.model.GameState;
import de.unisaarland.sopra.zugumzug.model.Mission;
import de.unisaarland.sopra.zugumzug.model.Player;
import de.unisaarland.sopra.zugumzug.model.Track;
import de.unisaarland.sopra.zugumzug.model.Tupel;

public class Util {

	private GameState gs;
	private GameBoard gb;
	
	public static final int NO_PLAYER = Integer.MAX_VALUE - 5;
	public static final int NOT_CONNECTED = Integer.MAX_VALUE -6;
	public static final short NONE = Short.MIN_VALUE + 6;
	private static final int MAX_TRACK_LENGTH = 6;

	private static final int[] tTable = { 0, 1, 2, 4, 6, 8 };
	private static final int[] Points = { 0, 1, 2, 4, 7, 10, 15 };
	private static final TrackKind[] tK = { TrackKind.ALL, TrackKind.BLACK, TrackKind.BLUE, TrackKind.GREEN,
			TrackKind.ORANGE, TrackKind.RED, TrackKind.VIOLET, TrackKind.WHITE, TrackKind.YELLOW };

	/**
	 * 
	 * @param gs
	 *            the gamestate that is to be considere when making choices
	 * @param pid
	 *            for which player is the choice made
	 */
	public Util(GameState gs) {
		this.gs = gs;
		this.gb = gs.getGameBoard();
	}

	/**
	 * 
	 * @return a copy of a gameboard, without references to the original
	 */
	public static GameBoard copyGB(GameBoard gb) {
		GameBoard newGB = new GameBoard();
		// add all cities to the new gb
		for (City c : gb.getCitySet()) {
			// make a new city, so no references are copied
			newGB.addCity(new City(c.getID(), c.getX(), c.getY(), c.getName()));
		}
		for (Track t : gb.getTrackSet()) {
			// make a new track, so no references are copied
			newGB.addTrack(new Track(t.getID(), t.getColor(), newGB.getCityFromId(t.getCityA().getID()),
					newGB.getCityFromId(t.getCityA().getID()), t.getCosts(), t.isTunnel()));
		}

		return newGB;
	}

	// ##########################################################
	// #################### chooseMissions ######################
	// ##########################################################

	/**
	 * getTracksMappedToColor() returns a HashMap with all
	 * TrackKinds. Every color maps an Array which shows
	 * the length and corresponding existing number of tracks
	 * to this length and color existing on the GameBoard,
	 * owned by nobody.
	 * The index of the array determines the length of the track.
	 * 
	 * Note:	The array with index 0 always contains 0.
	 * 			TrackKind.ALL can be payed with every color.
	 * 
	 * @return
	 */
	public HashMap<TrackKind, Integer[]> getTracksMappedToColor() {
		
		HashMap<TrackKind, Integer[]> map = new HashMap<TrackKind, Integer[]>();
		
		for (TrackKind k : TrackKind.values()) {
			Integer[] tracks = new Integer[MAX_TRACK_LENGTH];
			map.put(k, tracks);
		}
		
		for (Track t : gb.getTrackSet()) {
			if (t.getOwner() != Track.NO_OWNER) {
				Integer[] tracks = map.get(t.getColor());
				tracks[t.getCosts()] += 1;
			}
		}
		
		return map;
	}

	// ##########################################################
	// ###################### Dijkstra ##########################
	// ##########################################################

	private class DComp implements Comparator<City> {

		private HashMap<Short, Integer> dist;

		public DComp(HashMap<Short, Integer> dist) {
			this.dist = dist;
		}

		@Override
		public int compare(City cityA, City cityB) {
			if (dist.get(cityA.getID()) == dist.get(cityB.getID()))
				return 0;
			else 
				if (dist.get(cityA.getID()) < dist.get(cityB.getID()))
					return -1;
				else
					return 1;
		}

	}

	/**
	 * 
	 * @param start
	 *            the node where our search begins
	 * @param trans
	 *            (0-6) 0 = normal costs, 1-6 efficiency-values. basically the
	 *            preferred tracklength is give by this value
	 * @param pid
	 *            the pid of the player whose tracks we can use
	 * 
	 * @return Tupel with Hashmap with cid -> distance from start node, and
	 *         Hashmap cid -> pred with every path from the start node
	 * 
	 *         A modified Dijkstra Algorithm, that returns the wanted path and
	 *         the efficiency to every city in the Graph.
	 * 
	 */
	public Tupel<HashMap<Short, Integer>, HashMap<Short, Short>> Dijkstra(short start, int trans, int pid) {
		// initialize:
		HashMap<Short, Integer> dist = new HashMap<>();
		HashMap<Short, Short> pred = new HashMap<>();
		PriorityQueue<City> Q = new PriorityQueue<City>(gb.getCitySet().size(), new DComp(dist));

		// thin out the graph by removing unreachable cities:
		for (City c : gb.getCitySet()) {
			if (c.getReachableBuild(pid).isEmpty())
				continue;
			dist.put(c.getID(), NOT_CONNECTED);
			pred.put(c.getID(), NONE);
			if (c.getID() == start)
				dist.put(start, 0);

			Q.add(c);
		}
		// search:
		while (!Q.isEmpty()) {
			City cityA = Q.poll();
			for (short cidB : cityA.getReachableBuild(pid)) {
				relax(cityA, gb.getCityFromId(cidB), dist, pred, trans, pid);
				if (Q.remove(gb.getCityFromId(cidB)))
					Q.add(gb.getCityFromId(cidB));
			}
		}

		return new Tupel<HashMap<Short, Integer>, HashMap<Short, Short>>(dist, pred);
	}

	/**
	 * 
	 * @param cityA
	 * @param cityB
	 * @param dist
	 * @param pred
	 * @param trans
	 * @param pid
	 *            the pid of a certain player. All tracks of other players will
	 *            be ignored, as they are not usable, all tracks of this player
	 *            are considere cost 0
	 * 
	 * @return
	 * 
	 * 		A transposed Dijkstra relax Operation. It returns the Track with
	 *         the best 'value'. The trans parameter indicates the preferred
	 *         value. accepted values are 1-6, all other will default to 6.
	 * 
	 *         trans = 0 will behave like the normal Dijkstra relax op
	 * 
	 */
	private void relax(City cityA, City cityB, HashMap<Short, Integer> dist, HashMap<Short, Short> pred, int trans,
			int pid) {
		// get distance from City A to City B
		Set<Track> connections = cityA.getTrackSet(cityB.getID());
		int i = NOT_CONNECTED;
		// get the smallest possible distance. If the track is already built by
		// the player pid, the distance is 0. Ignore tracks already built by
		// other players
		for (Track t : connections) {
			if (t.getOwner() != pid && t.getOwner() != Track.NO_OWNER)
				continue;
			if (t.getOwner() == pid)
				i = (i < 0) ? i : 0;
			else
				i = (i < t.getCosts()) ? i : t.getCosts();
		}
		int newdist = dist.get(cityA.getID()) + transpose(i, trans);
		if (newdist < dist.get(cityB.getID())) {
			dist.put(cityB.getID(), newdist);
			pred.put(cityB.getID(), cityA.getID());
		}
	}

	/**
	 * 
	 * @param value
	 *            (1-6)
	 * @param trans
	 *            (1-6)
	 * @return the value transposed
	 * 
	 *         The value is transposed to give the best result for Dijkstra
	 *         depending on what efficiency is wanted (1-6) (6 is high effiency,
	 *         1 is easy to build)
	 * 
	 */
	private int transpose(int value, int trans) {
		if (trans < 1 || value < 1 || trans > 6 || value > 6)
			return value;

		return 10 + tTable[Math.abs(value - trans)];
	}

}
