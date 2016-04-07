package de.unisaarland.sopra.zugumzug.AI.AIUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.model.City;
import de.unisaarland.sopra.zugumzug.model.GameBoard;
import de.unisaarland.sopra.zugumzug.model.GameState;
import de.unisaarland.sopra.zugumzug.model.Player;
import de.unisaarland.sopra.zugumzug.model.Track;

public class Priority implements Comparator<Track> {

	private Player me;
	private GameState gs;

	private final static int FACTOR_COST = 20;

	private final static int FACTOR_CARDS = 4;

	private final static int BONUS_COLOURLESS = -1;

	private final static int FACTOR_ENEMY = 1;

	private final static int FACTOR_LONGEST_PATH = 2;

	public Priority(Player p, GameState gs) {
		this.me = p;
		this.gs = gs;
	}

	@Override
	public int compare(Track t1, Track t2) {
		int v1, v2;
		v1 = evaluate(t1);
		v2 = evaluate(t2);
		// sgn:

		// negative is better than positive
		return (v1 < v2) ? 1 : (v1 > v2) ? -1 : 0;
	}

	public int evaluate(Track track) {
		if (track.getCosts() > gs.getMaxTracks() - me.getTrackCounter())
			return Integer.MIN_VALUE;
		int value = 0;
		// Costs:
		value += track.getCosts() * FACTOR_COST;
		// Number of cards in hand:
		if (track.getColor() == TrackKind.ALL) {
			int maxcolour = 0;
			for (TrackKind tK : TrackKind.values()) {
				if (tK == TrackKind.ALL)
					continue;
				maxcolour = Math.max(maxcolour, me.getRessourceMap().get(tK));
			}

			value += maxcolour * FACTOR_CARDS;
		} else {
			value += me.getRessourceMap().get(track.getColor()) * FACTOR_CARDS;
		}
		// bonus for colourless tracks

		value += BONUS_COLOURLESS;
/*
		// push toward longest track
		City cityA = track.getCityA();
		City cityB = track.getCityB();

		HashMap<Integer,Integer> OwnersA = new HashMap<Integer, Integer>();
		HashMap<Integer,Integer> OwnersB = new HashMap<Integer, Integer>();
		
		for (Player p : gs.getPlayerSet()){
			OwnersA.put(p.getPid(), 0);
			OwnersB.put(p.getPid(), 0);
		}

		Set<Track> tSet = new HashSet<Track>(cityA.getTrackSet());
		tSet.removeAll(cityA.getTrackSet(cityB.getID()));
		
		for (Track t : tSet){
			// check all tracks form city A
			if (t.getOwner() != Track.NO_OWNER){
				OwnersA.put(t.getOwner(), OwnersA.get(t.getOwner())+1);
			}
		}
		
		tSet = new HashSet<Track>(cityB.getTrackSet());
		tSet.removeAll(cityB.getTrackSet(cityA.getID()));
		
		for (Track t : tSet){
			// check all tracks form city B
			if (t.getOwner() != Track.NO_OWNER){
				OwnersB.put(t.getOwner(), OwnersB.get(t.getOwner())+1);
			}
		}
		
		for (Player p : gs.getPlayerSet()){
			if (p.getPid() == me.getPid()){
				
			} else {
				
			}

		}
*/
		return value;
		
	}

}
