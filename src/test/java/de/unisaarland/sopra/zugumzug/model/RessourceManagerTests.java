package de.unisaarland.sopra.zugumzug.model;

import org.junit.*;

import de.unisaarland.sopra.MersenneTwister;
import de.unisaarland.sopra.TrackKind;
import static org.junit.Assert.*;

public class RessourceManagerTests {

	RessourceManager rm;
	
	@Before
	public void runBeforeMissionManagerTests() {
		rm = new RessourceManager(new MersenneTwister(0));
	}
	
	@Test
	public void testReturnCard() {
		
		int blue=3;
		int black=1;
		int green=2;
		int orange=3;
		int red=2;
		int violet=1;
		int white=2;
		int yellow=1;
		int all=2;
		
		rm.returnCard(TrackKind.BLUE, blue);
		rm.returnCard(TrackKind.BLACK, black);
		rm.returnCard(TrackKind.GREEN, green);
		rm.returnCard(TrackKind.ORANGE, orange);
		rm.returnCard(TrackKind.RED, red);
		rm.returnCard(TrackKind.VIOLET, violet);
		rm.returnCard(TrackKind.WHITE, white);
		rm.returnCard(TrackKind.YELLOW, yellow);
		rm.returnCard(TrackKind.ALL, all);
		
		int sum = blue + black + green + orange + red + violet + white
					+ yellow + all;
		
		for(int i = 0; i < sum; i++) {
			TrackKind drawnColor = rm.drawCard();
			assertTrue(drawnColor != null);
			
			if(drawnColor == TrackKind.BLUE) blue--;
			else if(drawnColor == TrackKind.BLACK) black--;
			else if(drawnColor == TrackKind.GREEN) green--;
			else if(drawnColor == TrackKind.ORANGE) orange--;
			else if(drawnColor == TrackKind.RED) red--;
			else if(drawnColor == TrackKind.VIOLET) violet--;
			else if(drawnColor == TrackKind.WHITE) white--;
			else if(drawnColor == TrackKind.YELLOW) yellow--;
			else if(drawnColor == TrackKind.ALL) all--;
			
			if(blue<0 || black<0 || green<0 || orange<0 || red<0 || violet<0 || white<0 || yellow<0 || all<0) {
				fail("Wrong drawn card");
			}
		}
		assertTrue(rm.getCurrentSizeClosedDeck() == 0);
	}
	
	@Test
	public void testDrawCardOpendeckValid() {
		
		TrackKind[] openRessourceDeck = rm.getOpenRessourceDeck();
		openRessourceDeck[0] = TrackKind.BLUE;
		openRessourceDeck[1] = TrackKind.YELLOW;
		openRessourceDeck[2] = TrackKind.BLUE;
		openRessourceDeck[3] = TrackKind.ALL;
		openRessourceDeck[4] = TrackKind.GREEN;
		
		assertTrue(rm.drawCard(TrackKind.YELLOW) == TrackKind.YELLOW);
		
		assertTrue(rm.drawCard(TrackKind.ALL) == TrackKind.ALL);
		
		assertTrue(rm.drawCard(TrackKind.GREEN) == TrackKind.GREEN);
		
		assertTrue(rm.drawCard(TrackKind.BLUE) == TrackKind.BLUE);
		
		assertTrue(rm.drawCard(TrackKind.BLUE) == TrackKind.BLUE);
	}

	@Test
	public void testDrawCardOpenDeckValidAfterRefill() {
		
		TrackKind[] openRessourceDeck = rm.getOpenRessourceDeck();
		openRessourceDeck[0] = TrackKind.BLUE;
		openRessourceDeck[1] = TrackKind.YELLOW;
		openRessourceDeck[2] = TrackKind.BLUE;
		openRessourceDeck[3] = TrackKind.ALL;
		openRessourceDeck[4] = TrackKind.GREEN;
		
		rm.returnCard(TrackKind.RED, 1);
		
		rm.drawCard(TrackKind.GREEN);
		assertTrue(openRessourceDeck[0] == TrackKind.BLUE
		&& openRessourceDeck[1] == TrackKind.YELLOW
		&& openRessourceDeck[2] == TrackKind.BLUE
		&& openRessourceDeck[3] == TrackKind.ALL
		&& openRessourceDeck[4] == null);
		
		assertTrue(rm.drawCard() == TrackKind.RED);
	}
	
	@Test
	public void testDrawCardOpendeckNotValid() {
		
		TrackKind[] openRessourceDeck = rm.getOpenRessourceDeck();
		openRessourceDeck[0] = TrackKind.BLUE;
		openRessourceDeck[1] = TrackKind.YELLOW;
		openRessourceDeck[2] = TrackKind.BLUE;
		openRessourceDeck[3] = TrackKind.ALL;
		openRessourceDeck[4] = TrackKind.GREEN;
		try {
			rm.drawCard(TrackKind.BLACK);
		} catch (IllegalArgumentException e) {
			return;
		}
	}
	
	@Test
	public void testDrawCardCloseddeckSingleColors() {
		
		rm.returnCard(TrackKind.BLUE, 1);
		assertTrue(rm.drawCard() == TrackKind.BLUE);
		
		rm.returnCard(TrackKind.BLACK, 1);
		assertTrue(rm.drawCard() == TrackKind.BLACK);
		
		rm.returnCard(TrackKind.GREEN, 1);
		assertTrue(rm.drawCard() == TrackKind.GREEN);
		
		rm.returnCard(TrackKind.ORANGE, 1);
		assertTrue(rm.drawCard() == TrackKind.ORANGE);
		
		rm.returnCard(TrackKind.RED, 1);
		assertTrue(rm.drawCard() == TrackKind.RED);
		
		rm.returnCard(TrackKind.VIOLET, 1);
		assertTrue(rm.drawCard() == TrackKind.VIOLET);
		
		rm.returnCard(TrackKind.WHITE, 1);
		assertTrue(rm.drawCard() == TrackKind.WHITE);
		
		rm.returnCard(TrackKind.YELLOW, 1);
		assertTrue(rm.drawCard() == TrackKind.YELLOW);
		
		rm.returnCard(TrackKind.ALL, 1);
		assertTrue(rm.drawCard() == TrackKind.ALL);
	}
	
	@Test
	public void testDrawCardCloseddeckRandom() {
		
		rm.returnCard(TrackKind.BLUE, 1);
		rm.returnCard(TrackKind.BLACK, 1);
		rm.returnCard(TrackKind.GREEN, 1);
		
		TrackKind drawn = rm.drawCard();
		assertTrue(drawn != null);
		
		if(drawn == TrackKind.BLUE) {
			if(rm.drawCard() == TrackKind.BLACK) assertTrue(rm.drawCard() == TrackKind.GREEN);
			else if(rm.drawCard() == TrackKind.GREEN) assertTrue(rm.drawCard() == TrackKind.BLACK);
		}
		else if(drawn == TrackKind.BLACK) {
			if(rm.drawCard() == TrackKind.BLUE) assertTrue(rm.drawCard() == TrackKind.GREEN);
			else if(rm.drawCard() == TrackKind.GREEN) assertTrue(rm.drawCard() == TrackKind.BLUE);
		}
		else {
			if(rm.drawCard() == TrackKind.BLUE) assertTrue(rm.drawCard() == TrackKind.BLACK);
			else if(rm.drawCard() == TrackKind.BLACK) assertTrue(rm.drawCard() == TrackKind.BLUE);
		}
	}
	
	@Test
	public void testRefillOpenDeck() {
		
		TrackKind[] openRessourceDeck = rm.getOpenRessourceDeck();
		openRessourceDeck[0] = TrackKind.RED;
		openRessourceDeck[1] = TrackKind.RED;
		openRessourceDeck[2] = TrackKind.RED;
		openRessourceDeck[3] = TrackKind.RED;
		openRessourceDeck[4] = TrackKind.GREEN;
		rm.returnCard(TrackKind.BLACK, 1);
		
		rm.drawCard(TrackKind.GREEN);
		assertTrue(openRessourceDeck[4] == null);
	}

	@Test
	public void testRefillOpenDeckFullOpendeck() {
		
		TrackKind[] openRessourceDeck = rm.getOpenRessourceDeck();
		openRessourceDeck[0] = TrackKind.RED;
		openRessourceDeck[1] = TrackKind.YELLOW;
		openRessourceDeck[2] = TrackKind.BLUE;
		openRessourceDeck[3] = TrackKind.VIOLET;
		openRessourceDeck[4] = TrackKind.GREEN;
		rm.returnCard(TrackKind.BLACK, 1);
		
		rm.refillOpenDeck();
		
		assertTrue(openRessourceDeck[0] == TrackKind.RED
		&& openRessourceDeck[1] == TrackKind.YELLOW
		&& openRessourceDeck[2] == TrackKind.BLUE
		&& openRessourceDeck[3] == TrackKind.VIOLET
		&& openRessourceDeck[4] == TrackKind.GREEN);
	}
	
	@Test																
	public void testRefreshOpenDeckGeneral() {		//Beinhaltet den "isOpenDeckValid-TestFalse".
		
		MersenneTwister random = new MersenneTwister(0);
		RessourceManager rm2 = new RessourceManager(random);
		//Reihenfolge Zufallszahlen: 0,1,6,6,2,2
		TrackKind[] openRessourceDeck = rm2.getOpenRessourceDeck();
		openRessourceDeck[0] = TrackKind.ALL;
		openRessourceDeck[1] = TrackKind.ALL;
		openRessourceDeck[2] = TrackKind.RED;
		openRessourceDeck[3] = TrackKind.ALL;
		openRessourceDeck[4] = TrackKind.ALL;
		rm2.returnCard(TrackKind.ALL, 1);
		rm2.returnCard(TrackKind.GREEN, 5);
		
		rm2.drawCard(TrackKind.RED);
		
		assertTrue(openRessourceDeck[0] == TrackKind.ALL
		&& openRessourceDeck[1] == TrackKind.ALL
		&& openRessourceDeck[2] == null
		&& openRessourceDeck[3] == TrackKind.ALL
		&& openRessourceDeck[4] == TrackKind.ALL);
	}
	
	@Test
	public void testRefreshOpenDeckThreeLoks() {	//Beinhaltet den "isOpenDeckValid-TestFalse".
	
		MersenneTwister random = new MersenneTwister(1);
		RessourceManager rm2 = new RessourceManager(random);
		//Reihenfolge Zufallszahlen: 1,2,1,0,0
		TrackKind[] openRessourceDeck = rm2.getOpenRessourceDeck();
		openRessourceDeck[0] = TrackKind.GREEN;
		openRessourceDeck[1] = TrackKind.ALL;
		openRessourceDeck[2] = TrackKind.RED;
		openRessourceDeck[3] = TrackKind.RED;
		openRessourceDeck[4] = TrackKind.ALL;
		rm2.returnCard(TrackKind.ALL, 1);
		
		rm2.drawCard(TrackKind.GREEN);
		
		assertTrue(openRessourceDeck[0] == null
		&& openRessourceDeck[1] == TrackKind.ALL
		&& openRessourceDeck[2] == TrackKind.RED
		&& openRessourceDeck[3] == TrackKind.RED
		&& openRessourceDeck[4] == TrackKind.ALL);
	}
	
	@Test
	public void testIsOpenDeckValidTrue() {
		MersenneTwister random = new MersenneTwister(1);
		RessourceManager rm2 = new RessourceManager(random);
		//Reihenfolge Zufallszahlen: 1,2,1,0,0
		TrackKind[] openRessourceDeck = rm2.getOpenRessourceDeck();
		openRessourceDeck[0] = TrackKind.RED;
		openRessourceDeck[1] = TrackKind.ALL;
		openRessourceDeck[2] = TrackKind.GREEN;
		openRessourceDeck[3] = TrackKind.RED;
		openRessourceDeck[4] = TrackKind.ALL;
		rm2.returnCard(TrackKind.BLACK, 1);
		
		rm2.drawCard(TrackKind.GREEN);
		
		assertTrue(openRessourceDeck[0] == TrackKind.RED
		&& openRessourceDeck[1] == TrackKind.ALL
		&& openRessourceDeck[2] == null
		&& openRessourceDeck[3] == TrackKind.RED
		&& openRessourceDeck[4] == TrackKind.ALL);
	}
	
	@Test
	public void testIsEmpty() {
		System.out.println(rm.isClosedDeckEmpty());
		assertTrue(rm.isClosedDeckEmpty() == true);
		
		rm.returnCard(TrackKind.BLACK, 1);
		assertTrue(rm.isClosedDeckEmpty() == false);
		
		rm.returnCard(TrackKind.BLUE, 3);
		rm.returnCard(TrackKind.GREEN, 2);
		rm.returnCard(TrackKind.ORANGE, 3);
		rm.returnCard(TrackKind.RED, 2);
		rm.returnCard(TrackKind.VIOLET, 1);
		rm.returnCard(TrackKind.WHITE, 2);
		rm.returnCard(TrackKind.YELLOW, 1);
		rm.returnCard(TrackKind.ALL, 2);
		
		assertTrue(rm.isClosedDeckEmpty() == false);
	}
	
	@Test
	public void testExistsCardEmptyAndFull() {
		//existsCard gibt Anzahl der "existierenden" Karten zurueck.
		assertTrue(rm.existsCard(TrackKind.ALL) == 0
				&& rm.existsCard(TrackKind.BLACK) == 0
				&& rm.existsCard(TrackKind.BLUE) == 0
				&& rm.existsCard(TrackKind.GREEN) == 0
				&& rm.existsCard(TrackKind.ORANGE) == 0
				&& rm.existsCard(TrackKind.RED) == 0
				&& rm.existsCard(TrackKind.VIOLET) == 0
				&& rm.existsCard(TrackKind.WHITE) == 0
				&& rm.existsCard(TrackKind.YELLOW) == 0);
		
		TrackKind[] openRessourceDeck = rm.getOpenRessourceDeck();
		openRessourceDeck[0] = TrackKind.BLACK;
		openRessourceDeck[1] = TrackKind.GREEN;
		openRessourceDeck[2] = TrackKind.GREEN;
		openRessourceDeck[3] = TrackKind.ORANGE;
		openRessourceDeck[4] = TrackKind.RED;
		
		assertTrue(rm.existsCard(TrackKind.ALL) == 0
				&& rm.existsCard(TrackKind.BLACK) == 1
				&& rm.existsCard(TrackKind.BLUE) == 0
				&& rm.existsCard(TrackKind.GREEN) == 2
				&& rm.existsCard(TrackKind.ORANGE) == 1
				&& rm.existsCard(TrackKind.RED) == 1
				&& rm.existsCard(TrackKind.VIOLET) == 0
				&& rm.existsCard(TrackKind.WHITE) == 0
				&& rm.existsCard(TrackKind.YELLOW) == 0);
	}
	
	@Test
	public void testExistsCardFullSingleColor() {
		
		TrackKind[] openRessourceDeck = rm.getOpenRessourceDeck();
		openRessourceDeck[0] = TrackKind.BLACK;
		openRessourceDeck[1] = TrackKind.BLACK;
		openRessourceDeck[2] = TrackKind.BLACK;
		openRessourceDeck[3] = TrackKind.BLACK;
		openRessourceDeck[4] = TrackKind.BLACK;
		
		assertTrue(rm.existsCard(TrackKind.ALL) == 0
				&& rm.existsCard(TrackKind.BLACK) == 5
				&& rm.existsCard(TrackKind.BLUE) == 0
				&& rm.existsCard(TrackKind.GREEN) == 0
				&& rm.existsCard(TrackKind.ORANGE) == 0
				&& rm.existsCard(TrackKind.RED) == 0
				&& rm.existsCard(TrackKind.VIOLET) == 0
				&& rm.existsCard(TrackKind.WHITE) == 0
				&& rm.existsCard(TrackKind.YELLOW) == 0);
	}
}
