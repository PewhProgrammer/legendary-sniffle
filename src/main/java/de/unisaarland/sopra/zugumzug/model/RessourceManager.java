
package de.unisaarland.sopra.zugumzug.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.unisaarland.sopra.MersenneTwister;
import de.unisaarland.sopra.TrackKind;

public class RessourceManager {

	private static final int OPEN_SIZE = 5;
	
	private final TrackKind[] openRessourceDeck = new TrackKind[OPEN_SIZE];
	private final HashMap<TrackKind, Integer> closedRessourceDeck = new HashMap<TrackKind, Integer>();

	private final MersenneTwister random;

	private int currentSizeClosedDeck = 0;

	public RessourceManager(MersenneTwister r) {
		random = r;
		for (TrackKind color : TrackKind.values()) {
			closedRessourceDeck.put(color, 0);
		}
	}

	public HashMap<TrackKind, Integer> getClosedRessourceDeck() {
		return this.closedRessourceDeck;
	}

	public boolean isOpenDeckValid() {
		return existsCard(TrackKind.ALL) < 3;
	}

	public void refreshOpenDeck() {
		for (int i = 0; i < OPEN_SIZE; i++) {
			if (openRessourceDeck[i] != null){
				returnCard(openRessourceDeck[i], 1);
				openRessourceDeck[i] = null;
			}
		}
		for (int i = 0; i < OPEN_SIZE && !isClosedDeckEmpty(); i++) {
			openRessourceDeck[i] = drawCard();
		}
	}

	public TrackKind drawCard() {
		
		if((closedRessourceDeck.isEmpty())) {
			throw new IllegalArgumentException("closedRessourceDeck is empty");
		}
		
		int r = random.nextInt(currentSizeClosedDeck-1);
		int current = 0;
		
		for(TrackKind tk: TrackKind.values()) {
			int countColCards = closedRessourceDeck.get(tk);
			current = countColCards + current;
			if(current > r) {
				closedRessourceDeck.put(tk, countColCards - 1);
				currentSizeClosedDeck--;
				return tk;
			}
			
		}
		
		throw new IllegalArgumentException();
	}

	public TrackKind drawCard(TrackKind color) {
		if (existsCard(color) <= 0)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < OPEN_SIZE; i++) {
			if (openRessourceDeck[i] == color) {
				openRessourceDeck[i] = null;
				break;
			}
		}
		return color;
	}

	public int getCurrentSizeClosedDeck() {
		return currentSizeClosedDeck;
	}
	
	public int getCurrentOverallNumberOfCards() {
		int sum = 0;
		for (int i = 0; i < openRessourceDeck.length; i++) {
			if (openRessourceDeck[i] != null) sum++;
		}
		return currentSizeClosedDeck + sum;
	}

	public void returnCard(TrackKind color, int count) {
		int oldcount = closedRessourceDeck.get(color);
		closedRessourceDeck.put(color, oldcount + count);
		currentSizeClosedDeck += count;
	}

	public boolean isClosedDeckEmpty() {
		int sum = 0;
		for (int i : this.closedRessourceDeck.values()){
			sum += i;
		}
		return sum == 0;
	}

	/**
	 * 
	 * @param color
	 * @return the amount of existing cards in the open deck of this specific
	 *         color
	 */
	public int existsCard(TrackKind color) {
		int sum = 0;
		for (int i = 0; i < OPEN_SIZE; i++) {
			if (openRessourceDeck[i] == color)
				sum++;
		}
		return sum;
	}

	public List<TrackKind> refillOpenDeck() {		// The server has to know, which Cards were refreshed
		List<TrackKind> refeshedCards = new ArrayList<TrackKind>();
		for (int i = 0; i < OPEN_SIZE && !isClosedDeckEmpty(); i++) {
			if (openRessourceDeck[i] == null){
				openRessourceDeck[i] = drawCard();
				refeshedCards.add(openRessourceDeck[i]);
			}
		}
		if (!isOpenDeckValid() && !isClosedDeckEmpty() && refeshedCards.size() != 0) {
			refreshOpenDeck();
			for (int i = 0; i < OPEN_SIZE; i++) {
				if (openRessourceDeck[i] != null){	
					refeshedCards.add(openRessourceDeck[i]);
				}
			}
		}
		
		return refeshedCards;
	}

	public TrackKind[] getOpenRessourceDeck() {
		return openRessourceDeck;
	}
	
	public void incrementCurrentSizeClosedDeck(int increm){
		this.currentSizeClosedDeck += increm;
	}
	
	/**
	 * This method checks whether the closed deck is empty
	 * if TRUE return FALSE
	 * if FALSE check if all open cards are locomotives.
	 * 
	 * @return
	 */
	public boolean areTheOnlyRemainingOpenCardsLocomotives() {
		if (!isClosedDeckEmpty()) {
			return false;
		} else {
			for (int i = 0; i < openRessourceDeck.length; i++) {
				if (openRessourceDeck[i] != TrackKind.ALL
						&& openRessourceDeck[i] != null)
					return false;
			}
			return true;
		}
	}

	
	/**
	 * This method removes a specific TrackKind from the open deck
	 * @param kind
	 */
	public void removeOpenCard(TrackKind kind) {
		for (int i = 0; i < OPEN_SIZE; i++){
			if (openRessourceDeck[i] == kind){
				openRessourceDeck[i] = null;
				break;
			}
		}
	}
	
	/**
	 * This method add a card to the open deck, and checks weather a 
	 * refresh will receive;
	 * @param tk
	 */
	public void addOpenCard(TrackKind tk){
		boolean freeField = false;
		for (int i = 0; i < OPEN_SIZE; i++) {
			if (openRessourceDeck[i] == null) {
				openRessourceDeck[i] = tk;
				currentSizeClosedDeck--;
				freeField = true;
				break;
			}
		}
		if (!freeField) {		// means a refresh will received next
			for (int i = 0; i < OPEN_SIZE; i++) {
				if (openRessourceDeck[i] != null){
					openRessourceDeck[i] = null; 
					currentSizeClosedDeck += 1;
				}
			}
			addOpenCard(tk);
		}	
		
	}
	
	public boolean openDeckContains(TrackKind kind) {
		for (TrackKind tK : openRessourceDeck) {
			if (tK == kind){
				return true;
			}
		}
		return false;
	}
	
	

}
