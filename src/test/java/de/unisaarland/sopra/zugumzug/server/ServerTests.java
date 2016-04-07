package de.unisaarland.sopra.zugumzug.server;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.actions.client.BuildTrack;
import de.unisaarland.sopra.zugumzug.actions.client.ClientAction;
import de.unisaarland.sopra.zugumzug.actions.client.Leave;
import de.unisaarland.sopra.zugumzug.actions.client.NewObserver;
import de.unisaarland.sopra.zugumzug.actions.client.PayTunnel;
import de.unisaarland.sopra.zugumzug.actions.client.Register;
import de.unisaarland.sopra.zugumzug.actions.client.ReturnMissions;
import de.unisaarland.sopra.zugumzug.actions.client.TakeCard;
import de.unisaarland.sopra.zugumzug.actions.client.TakeSecretCard;
import de.unisaarland.sopra.zugumzug.model.GameState;
import de.unisaarland.sopra.zugumzug.model.Mission;
import de.unisaarland.sopra.zugumzug.model.Player;
import de.unisaarland.sopra.zugumzug.model.RessourceManager;

public class ServerTests {
	
	final static String FILENAME = "src/Mapfiles/SmallBoard";
	final static String MAP_TAKE_ALL_RESOURCE_CARDS = "src/Mapfiles/SmallBoard_TakeAllResourceCards";
	final static String FANTASY = "NightlyTests/GetMissionsTest/fantasytowns.map";
	
	final static int NUM_PLAYERS = 2;
	final static int SEED = Integer.MAX_VALUE; 
	final static int PORT = 7778;
	
	final static short TRACKID1 = 0;
	final static int COSTS1 = 2;
	final static int TRACKCOUNTER_MAX = Integer.MAX_VALUE;
	
	final static String NAME1 = "Frank1";
	final static String NAME2 = "Frank2";
	final static String NAME3 = "Muchacho";
	final static String NAME4 = "Carbonara";
	
	final static int ID1 = 1;
	final static int ID2 = 2;
	final static int ID3 = 3;
	final static int ID4 = 4;
	
	NewObserver ob1 = new NewObserver(ID1);
	NewObserver ob2 = new NewObserver(ID2);
	NewObserver ob3 = new NewObserver(ID3);
	NewObserver ob4 = new NewObserver(ID4);
	
	Register reg1 = new Register(NAME1, ID1);
	Register reg2 = new Register(NAME2, ID2);
	Register reg3 = new Register(NAME3, ID3);
	Register reg4 = new Register(NAME4, ID4);
	
	Set<Mission> emptyMissionSet = new HashSet<>();
	
	ReturnMissions rm1 = new ReturnMissions(ID1, emptyMissionSet);
	ReturnMissions rm2 = new ReturnMissions(ID2, emptyMissionSet);
	ReturnMissions rm3 = new ReturnMissions(ID3, emptyMissionSet);
	ReturnMissions rm4 = new ReturnMissions(ID4, emptyMissionSet);
	
	Leave leave1 = new Leave(ID1);
	Leave leave2 = new Leave(ID2);
	Leave leave3 = new Leave(ID3);
	Leave leave4 = new Leave(ID4);
	
	ArrayList<ClientAction> SETUP_RECEIVES = new ArrayList<>(Arrays.asList(ob1, ob2, reg1, reg2, rm1, rm2));
	
	
	ServerCommunicatorDummy sc;
	ServerGameController gc;
	GameState gs = new GameState();
	
	private static SecurityManager securityManager = new SecurityManager() {
		@Override
	    public void checkPermission(Permission permission) {
	    	if ("exitVM".equals(permission.getName())) {
	    		throw new SecurityException("System.exit attempted and blocked.");
	    	}
	    }
		@Override
        public void checkExit(int status) {
            super.checkExit(status);
        	throw new IllegalArgumentException("" + status);
        }
	};
	
	@BeforeClass
	public static void setup() {
		System.setSecurityManager(securityManager);
	}

	public void initialize(String map, int numPlayers, ArrayList<ClientAction> receives) throws IOException{
		sc = new ServerCommunicatorDummy(receives);
		gc = new ServerGameController(gs, map, sc, numPlayers, SEED);
		gc.setup();
	}
	
	@After
	public void finish() {
		sc.close();
	}
	
	/**
	 * @throws IOException 
	 *  
	 */
	@Test(timeout = 1000)
	public void testValidInitalize() throws IOException {
		
		initialize(FILENAME, NUM_PLAYERS, SETUP_RECEIVES);
		
		int expectedSends = 121231231; 		//TODO 
		
		for (int id: gc.getActivePlayers()){
			
			Player p = gs.getPlayerFromId(id);
			assertTrue(p.getMissionList().size() >= 1 && p.getMissionList().size() <=3);
			assertTrue(p.getSumRessourceCards() == 4);
			assertTrue(p.getScore() == 0);
			assertTrue(p.getTrackCounter() == 0);
		}	
		//sc.printSendedActions();
		//assertEquals(expectedSends, sc.getSendCounter());
	}
	
	@Test(timeout = 1000)
	public void playerInSetupKicked() throws IOException{
		
		int expectedSends = 123123123;
		
		NewObserver ob1 = new NewObserver(ID1);
		NewObserver ob2 = new NewObserver(ID2);
		Register reg1 = new Register(NAME1,ID1);
		Register reg2 = new Register(NAME2,ID2);
		ReturnMissions rm1 = new ReturnMissions(ID1, emptyMissionSet);
		TakeSecretCard invalidTsc2 = new TakeSecretCard(ID2);
		
		ArrayList<ClientAction> invalidReceives = new ArrayList<>(Arrays.asList(ob1, ob2, reg1, reg2, rm1,
							invalidTsc2, invalidTsc2, invalidTsc2, invalidTsc2));
		
		initialize(FILENAME, NUM_PLAYERS, invalidReceives);
		assertTrue(gc.getActivePlayers().size() == NUM_PLAYERS -1);
		
		//assertEquals(expectedSends, sc.getSendCounter());
	}
	
//	@Test
	@Test(timeout = 1000)
	public void buildTrackEndsGame() throws IOException{
		
		try {
			int expectedSends = 123123;
			BuildTrack bt1 = new BuildTrack(ID1, TRACKID1, TrackKind.ALL, COSTS1);
			ArrayList<ClientAction> RECEIVES
				= new ArrayList<>(Arrays.asList(ob1, ob2, reg1, reg2, rm1, rm2,
												bt1, leave1, leave2));
			
			initialize(FILENAME, NUM_PLAYERS, RECEIVES);
				
			gs.getPlayerFromId(ID1).giveRessourceCards(TrackKind.ALL, 5);
			gs.getPlayerFromId(ID1).incrementTrackCounter(TRACKCOUNTER_MAX);
			
			gc.runGame();
			
		} catch (IllegalArgumentException e) {
			assertTrue(Integer.parseInt(e.getMessage()) == 0);
			assertTrue(gs.getPlayerFromId(ID1).getScore() == -9);
			assertTrue(gs.getPlayerFromId(ID2).getScore() == -7);
			
			//assertEquals(expectedSends, sc.getSendCounter());
		}
		
	}
	
	// Test LateObserver and PlayerLeave
	
	@Test
	public void testTakeAllResouceCards() throws IOException {
		
		try {
			TakeSecretCard tsc1 = new TakeSecretCard(ID1);
			TakeSecretCard tsc2 = new TakeSecretCard(ID2);
			
			int expectedSends = 123123;
			ArrayList<ClientAction> RECEIVES
				= new ArrayList<>(Arrays.asList(
												ob1, ob2, ob3, ob4,
												reg1, reg2, reg3, reg4,
												rm1, rm2, rm2, rm3,
												leave3, leave4,
												tsc1, tsc1, tsc2, tsc2,		// 4
												tsc1, tsc1, tsc2, tsc2,		// 8
												tsc1, tsc1, tsc2, tsc2,		// 12
												tsc1, tsc1, tsc2, tsc2,		// 16
												tsc1, tsc1,					// 18
												tsc2, tsc2, // 2nd inval	// 20
												leave1, leave2
												));
			
			initialize(MAP_TAKE_ALL_RESOURCE_CARDS, 4, RECEIVES);
			
			assertTrue(gs.getRessourceManager().getCurrentSizeClosedDeck() == 19);
			assertTrue(gs.getRessourceManager().getCurrentOverallNumberOfCards() == 24);
//			gs.getPlayerFromId(ID1).incrementTrackCounter(TRACKCOUNTER_MAX);

			gc.runGame();
			
		} catch (IllegalArgumentException e) {
			sc.printSentActions();
			assertTrue(Integer.parseInt(e.getMessage()) == 0);
			assertTrue("Score was: " + gs.getPlayerFromId(ID1).getScore(), gs.getPlayerFromId(ID1).getScore() == 0);	// Leave
			assertTrue("Score was: " + gs.getPlayerFromId(ID2).getScore(), gs.getPlayerFromId(ID2).getScore() == 0);	// Leave
			assertTrue("Score was: " + gs.getPlayerFromId(ID3).getScore(), gs.getPlayerFromId(ID3).getScore() == 0);
			assertTrue("Score was: " + gs.getPlayerFromId(ID4).getScore(), gs.getPlayerFromId(ID4).getScore() == 0);
			//assertEquals(expectedSends, sc.getSendCounter());
		}
		
	}
	
	@Test
	public void testDrawLocomotiveSecondLegalTest() throws IOException {
		
		try {
			TakeCard tc = new TakeCard(ID1, TrackKind.ALL);
			
			int expectedSends = 123123;
			ArrayList<ClientAction> RECEIVES
			= new ArrayList<>(Arrays.asList(ob1, ob2, reg1, reg2, rm1, rm2,
					tc, tc, leave1, leave2));
			
			initialize(FILENAME, NUM_PLAYERS, RECEIVES);
			
			RessourceManager rm = gs.getRessourceManager();
			HashMap<TrackKind, Integer> closedDeck = rm.getClosedRessourceDeck();
			for (TrackKind k : TrackKind.values())
				closedDeck.put(k, 0);
			
			TrackKind[] openDeck = rm.getOpenRessourceDeck();
			for (int i = 0; i < openDeck.length; i++)
				openDeck[i] = TrackKind.ALL;
			
			rm.incrementCurrentSizeClosedDeck(-42);
			assertTrue(rm.areTheOnlyRemainingOpenCardsLocomotives());
			
			gc.runGame();
			
		} catch (IllegalArgumentException e) {
			sc.printSentActions();
		}
	}
	
	@Test
	public void testTakeAllMissions() throws IOException {
		
		try {
			
			Set<Mission> rmSet1 = new HashSet<>();
			rmSet1.add(new Mission(3, 8, (short) 0, (short) 5));
			rmSet1.add(new Mission(6, 6, (short) 1, (short) 4));
			ReturnMissions rmValid = new ReturnMissions(ID1, rmSet1);
			
			Set<Mission> rmSet2 = new HashSet<>();
			rmSet2.add(new Mission(1, 6, (short) 0, (short) 4));
			rmSet2.add(new Mission(2, 5, (short) 0, (short) 3));
			rmSet2.add(new Mission(5, 6, (short) 5, (short) 1));
			ReturnMissions rmInvalid = new ReturnMissions(ID2, rmSet2);
			
			int expectedSends = 123123;
			ArrayList<ClientAction> RECEIVES
			= new ArrayList<>(Arrays.asList(ob1, ob2,
											reg2, reg1,
											rmValid, rmInvalid,
											leave1, leave2));
			
			initialize(FILENAME, NUM_PLAYERS, RECEIVES);
			
			gs.getPlayerFromId(ID1).incrementTrackCounter(TRACKCOUNTER_MAX);
			
			gc.runGame();
			
		} catch (IllegalArgumentException e) {
			sc.printSentActions();
//			List<ServerAction> actions = sc.sentActions;
//			System.out.println(actions.size());
		}
	}
	
	@Test
	public void illegalBuildPlayer() throws IOException {
		try{
			
			//Turn Player1
			BuildTrack illbt1_1 = new BuildTrack(ID1, TRACKID1, TrackKind.BLACK , 0); // wrong TrackKind 
			BuildTrack illbt1_2 = new BuildTrack(ID1, TRACKID1, TrackKind.BLUE , 3); // to much locomotives
			
			BuildTrack legal1_1 = new BuildTrack(ID1, (short) 3, TrackKind.WHITE, 1);
			//Turn Player2
			BuildTrack illegal1_3 = new BuildTrack(ID1, (short) 0, TrackKind.ALL, 1);		// it is not his turn
			
			BuildTrack illegal2_1 = new BuildTrack(ID2, (short) 3, TrackKind.WHITE, 0);			// Allready build
			
			BuildTrack legal2_1 = new BuildTrack(ID2, (short) 4, TrackKind.BLUE, 0);
			// Turn Player 1
			BuildTrack illbt1_3 = new BuildTrack(ID1, (short) 5, TrackKind.RED , 3); 			// has allready parrallel
			
			BuildTrack legal1_2 = new BuildTrack(ID1, (short) 12, TrackKind.WHITE, 1);
			PayTunnel pt1_1 = new PayTunnel(ID1, 0);
			
			// GameOver
			
			
			ArrayList<ClientAction> RECEIVES
			= new ArrayList<>(Arrays.asList(ob1, ob2, reg1, reg2, rm1, rm2, illbt1_1, illbt1_2, legal1_1, illegal1_3, illegal2_1,
						legal2_1, illbt1_3, legal1_2, pt1_1, leave1, leave2));
		
			initialize(FANTASY,2,RECEIVES);
			
			gs.getPlayerFromId(ID1).giveRessourceCards(TrackKind.ALL, 5);
			gs.getPlayerFromId(ID1).giveRessourceCards(TrackKind.WHITE, 10);
			gs.getPlayerFromId(ID1).giveRessourceCards(TrackKind.BLUE, 1);
			gs.getPlayerFromId(ID1).incrementTrackCounter(23);					// 32 is max he is going to build 6
			
			gs.getPlayerFromId(ID2).giveRessourceCards(TrackKind.ALL, 5);
			gs.getPlayerFromId(ID2).giveRessourceCards(TrackKind.WHITE, 5);
			gs.getPlayerFromId(ID2).giveRessourceCards(TrackKind.BLUE, 5);
			
		} catch (IllegalArgumentException e) {
			assertTrue(Integer.parseInt(e.getMessage()) == 0);
		

			//assertEquals(expectedSends, sc.getSendCounter());
		}
	
	}
	
}
