package de.unisaarland.sopra.zugumzug;

import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.unisaarland.sopra.MersenneTwister;
import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.actions.client.TakeCard;
import de.unisaarland.sopra.zugumzug.model.GameState;
import de.unisaarland.sopra.zugumzug.model.Player;

/**
 * This is an Utility class everybody can use
 * 
 * @author timotheus
 *
 */
public class Utility {
	
	public final static HashMap<String, TrackKind> colorMap = new HashMap<String, TrackKind>();
	private final static MapFactory mapFactory = new MapFactory();
	
	static {
		colorMap.put("ALL", TrackKind.ALL);
		colorMap.put("BLACK", TrackKind.BLACK);
		colorMap.put("BLUE", TrackKind.BLUE);
		colorMap.put("GREEN", TrackKind.GREEN);
		colorMap.put("ORANGE", TrackKind.ORANGE);
		colorMap.put("RED", TrackKind.RED);
		colorMap.put("VIOLET", TrackKind.VIOLET);
		colorMap.put("WHITE", TrackKind.WHITE);
		colorMap.put("YELLOW", TrackKind.YELLOW);
	}
	
	public static <E> boolean containTwoListsSameValues(List<E> listA, List<E> listB) {
		
		final int size = listA.size();
		
		if (size != listB.size()) return false;
		
		for (int i = 0; i < size; i++) {
			if (!listA.get(i).equals(listB.get(i)))
				return false;
		}
		
		return true;
	}
	
	public static List<Integer> asList(final int[] is) {
		
		List<Integer> retList = new ArrayList<>();
		
		for (int i : is) {
			retList.add(i);
		}
		
		return retList;
	}
	
	public static GameState getGameOverGameState(final int[] listIDs) throws IOException {
		MersenneTwister rng = new MersenneTwister(42);
		
		GameState gameState = new GameState();
		mapFactory.initializeGameState("./src/MapFiles/SmallBoard", 1, rng, gameState);
		
		for (int i : listIDs) {
			Player p = new Player(i, "Balu");
			gameState.addPlayerInPlayerMap(p);
			p.incrementTrackCounter(gameState.getMaxTracks());
		}
		
		return gameState;
	}
	
	public static TakeCard getDrawCard(final int pid, TrackKind t) {
		return new TakeCard(pid, t);
	}

}
