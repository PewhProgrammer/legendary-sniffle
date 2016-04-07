package de.unisaarland.sopra.zugumzug;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.sopra.MersenneTwister;
import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.actions.Action;
import de.unisaarland.sopra.zugumzug.actions.client.BuildTrack;
import de.unisaarland.sopra.zugumzug.actions.client.GetMissions;
import de.unisaarland.sopra.zugumzug.actions.client.PayTunnel;
import de.unisaarland.sopra.zugumzug.actions.client.ReturnMissions;
import de.unisaarland.sopra.zugumzug.actions.client.TakeCard;
import de.unisaarland.sopra.zugumzug.actions.client.TakeSecretCard;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.AdditionalCost;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.NewMissions;
import de.unisaarland.sopra.zugumzug.model.GameState;
import de.unisaarland.sopra.zugumzug.model.Mission;
import de.unisaarland.sopra.zugumzug.model.MissionManager;
import de.unisaarland.sopra.zugumzug.model.Player;
import de.unisaarland.sopra.zugumzug.model.RessourceManager;
import de.unisaarland.sopra.zugumzug.model.Track;
import de.unisaarland.sopra.zugumzug.server.ServerCommunicator;
import de.unisaarland.sopra.zugumzug.server.ServerGameController;

public class ValidatorTests {
	
	final static String STANDARD_MAP = "src/Mapfiles/SmallBoard";
	final static String PARALLEL_MAP = "src/Mapfiles/SmallBoard_ParallelTracks";
	
	// STANDARD_MAP values	
	final static short TRACK0_ID = 0; // 0,1,2,ALL,N
	final static short TRACK1_ID = 1; // 1,2,2,ALL,N
	final static short TRACK2_ID = 2; // 2,4,3,BLACK,T
	final static short TRACK3_ID = 3; // 3,4,3,RED,T
	
	// PARALLEL_MAP
	final static short TRACKP0_ID = 0;	// 0,1,2,ALL,N
	final static short TRACKP1_ID = 1;	// 0,1,2,BLUE,T
	final static short TRACKP4_ID = 4;	// 0,1,2,VIOLET,N
	final static short TRACKP6_ID = 6;	// 0,1,2,BLACK,N
	final static short TRACKP9_ID = 9;	// 0,1,2,YELLOW,T

	final static int Player1_ID = 1;
	final static int Player2_ID = 2;
	
	final static int PORT = 7778;
	final static int SEED = 42;
	
	final static TakeSecretCard RANDOM_CMD1 = new TakeSecretCard(Player1_ID);
	final static TakeSecretCard RANDOM_CMD2 =  new TakeSecretCard(Player2_ID);
	
	GameState gameState = new GameState();
	Validator validator;
	MersenneTwister rng = new MersenneTwister(89);
	MapFactory mapFactory = new MapFactory();
	
	@BeforeClass
	public static void setup() throws IOException {
	}
	
	/**
	 * Initializes a SinglePlayerGame
	 * Player ID = 1 
	 * @throws IOException 
	 */
	public void initializeGameControllerSinglePlayer() throws IOException {
		
		mapFactory.initializeGameState(STANDARD_MAP, 1, rng, gameState);
		validator = new Validator(gameState);
		
		gameState.setMaxHandSize(55);	
		
		Player onlyPlayer = new Player(Player1_ID, "onlyPlayer");
		gameState.addPlayerInPlayerMap(onlyPlayer);
	}
	
	/**
	 * Initializes a TwoPlayerGame
	 * Player IDs = 1,2
	 * @throws IOException 
	 */
	public void initializeGameControllerTwoPlayer() throws IOException{
		
		mapFactory.initializeGameState(STANDARD_MAP, 2, rng, gameState);
		validator = new Validator(gameState);
		
		gameState.setMaxHandSize(28);
		
		Player firstPlayer = new Player(Player1_ID, "FirstPlayer");
		gameState.addPlayerInPlayerMap(firstPlayer);
		
		Player secondPlayer = new Player(Player2_ID, "SecondPlayer");
		gameState.addPlayerInPlayerMap(secondPlayer);
		
	}
	
	@Test
	public void testBuildTrackAndLastActionNull() throws IOException {
		
		GameState gameState = new GameState();
		mapFactory.initializeGameState(STANDARD_MAP, 1, rng, gameState);
		Validator validator = new Validator(gameState);

		Player p = new Player(0, "Ufuk");
		p.giveRessourceCards(TrackKind.GREEN, 2);
		p.giveRessourceCards(TrackKind.BLACK, 2);
		gameState.addPlayerInPlayerMap(p);
		
		BuildTrack action = new BuildTrack(0, TRACK1_ID, TrackKind.GREEN, 0);
		BuildTrack action2 = new BuildTrack(0, TRACK3_ID, TrackKind.BLACK, 0);

		assertFalse(validator.test(2, null, action));
		assertTrue(validator.test(0, null, action));
		assertFalse(validator.test(0, null, action2));
		
	}
	
	@Test
	public void testBuildTrackWithMoreCardsThanCosts() throws IOException {
		
		GameState gameState = new GameState();
		mapFactory.initializeGameState(STANDARD_MAP, 1, rng, gameState);
		Validator validator = new Validator(gameState);

		Player p = new Player(0, "Ufuk");
		p.giveRessourceCards(TrackKind.GREEN, 3);
		p.giveRessourceCards(TrackKind.ALL, 3);
		gameState.addPlayerInPlayerMap(p);
		
		BuildTrack action = new BuildTrack(0, TRACK1_ID, TrackKind.GREEN, 3);

		assertFalse(validator.test(0, null, action));
	}
	
	@Test
	public void testBuildValidParallelTrack() throws IOException {
		
		GameState gameState = new GameState();
		mapFactory.initializeGameState(PARALLEL_MAP, 2, rng, gameState);
		Validator validator = new Validator(gameState);
		
		Player p1 = new Player(0, "Ufuk");
		p1.giveRessourceCards(TrackKind.GREEN, 2);
		gameState.addPlayerInPlayerMap(p1);
		
		Player p2 = new Player(1, "Django");
		p2.giveRessourceCards(TrackKind.BLACK, 2);
		gameState.addPlayerInPlayerMap(p2);
		
		BuildTrack actionP1 = new BuildTrack(0, TRACKP0_ID, TrackKind.GREEN, 0);
		BuildTrack actionP2 = new BuildTrack(1, TRACKP6_ID, TrackKind.BLACK, 0);
		
		assertTrue(validator.test(0, null, actionP1));
		
		gameState.getGameBoard().buildTrack(TRACKP0_ID, p1);
		
		assertTrue(validator.test(1, null, actionP2));
	}
	
	@Test
	public void testBuildInvalidParallelTrack() throws IOException {
		
		GameState gameState = new GameState();
		mapFactory.initializeGameState(PARALLEL_MAP, 2, rng, gameState);
		Validator validator = new Validator(gameState);
		
		Player p0 = new Player(0, "Ufuk");
		p0.giveRessourceCards(TrackKind.GREEN, 2);
		p0.giveRessourceCards(TrackKind.BLACK, 2);
		p0.giveRessourceCards(TrackKind.YELLOW, 2);
		gameState.addPlayerInPlayerMap(p0);
		
		Player p1 = new Player(1, "Django");
		p1.giveRessourceCards(TrackKind.BLACK, 2);
		p1.giveRessourceCards(TrackKind.YELLOW, 2);
		gameState.addPlayerInPlayerMap(p1);
		
		BuildTrack actionP0 = new BuildTrack(0, TRACKP0_ID, TrackKind.GREEN, 0);
		BuildTrack actionP0_2 = new BuildTrack(0, TRACKP6_ID, TrackKind.BLACK, 0);
		BuildTrack actionP0Wrong1 = new BuildTrack(0, TRACKP4_ID, TrackKind.VIOLET, 0);
		BuildTrack actionP0Wrong2 = new BuildTrack(0, TRACKP1_ID, TrackKind.GREEN, 0);
		BuildTrack actionP0Wrong3 = new BuildTrack(0, TRACKP6_ID, TrackKind.BLACK, 0);
		BuildTrack actionP0Wrong4 = new BuildTrack(0, TRACKP9_ID, TrackKind.YELLOW, 0);
		
		BuildTrack actionP1 = new BuildTrack(1, TRACKP6_ID, TrackKind.BLACK, 0);
		BuildTrack actionP1Wrong1 = new BuildTrack(1, TRACKP0_ID, TrackKind.BLACK, 0);
		BuildTrack actionP1Wrong2 = new BuildTrack(1, TRACKP9_ID, TrackKind.YELLOW, 0);
		
		assertFalse(validator.test(0, null, actionP0Wrong1));	// has no VIOLET
		assertFalse(validator.test(0, null, actionP0Wrong2));	// has no BLUE
		assertTrue(validator.test(0, null, actionP0_2));		// has BLACK
		assertTrue(validator.test(0, null, actionP0));			// has GREEN
		
		gameState.getGameBoard().buildTrack(TRACKP0_ID, p0);
		
		assertTrue(validator.test(1, null, actionP1Wrong2));	// has enough for tunnel
		assertTrue(validator.test(1, null, actionP1));			// has BLACK
		assertFalse(validator.test(1, null, actionP1Wrong1));	// already built
		assertFalse(validator.test(0, null, actionP0Wrong3));	// no more parallel track allowed
		assertFalse(validator.test(0, null, actionP0Wrong4));	// no more parallel track allowed
		
	}
	
	@Test
	public void testTakeSecretCardAndLastActionNull() throws IOException {
		TakeSecretCard action = new TakeSecretCard(0);
		GameState gameState = new GameState();
		mapFactory.initializeGameState(STANDARD_MAP, 1, rng, gameState);
		gameState.addPlayerInPlayerMap(new Player(0, "Ufuk"));
		Validator validator = new Validator(gameState);
		
		assertFalse(validator.test(2, null, action));
		assertTrue(validator.test(0, null, action));
	}
	
	@Test
	public void testTakeCardTwiceValid() throws IOException {
		
		TakeCard takeCard1 = new TakeCard(1, TrackKind.BLACK);
		TakeCard takeCard2 = new TakeCard(1, TrackKind.RED);
		
		GameState gameState = new GameState();
		mapFactory.initializeGameState(STANDARD_MAP, 1, rng, gameState);
		TrackKind[] deck = gameState.getRessourceManager().getOpenRessourceDeck();
		deck[0] = TrackKind.BLACK;
		deck[1] = TrackKind.RED;
		
		gameState.addPlayerInPlayerMap(new Player(1, "Ufuk"));
		
		Validator validator = new Validator(gameState);
		
		assertTrue(validator.test(1, null, takeCard1));
		assertTrue(validator.test(1, takeCard1, takeCard2));
	}

	/**
	 * Tests whether the validator can handle the null command
	 * @throws IOException 
	 */
	@Test
	public void gameEntryValidTrackBuild() throws IOException {
		
		initializeGameControllerSinglePlayer();
		
		Player p = gameState.getPlayerFromId(Player1_ID);
		p.giveRessourceCards(TrackKind.BLACK, 3);	
		
		BuildTrack buildTrack = new BuildTrack(Player1_ID, TRACK0_ID, TrackKind.BLACK, 0);
		
		assertTrue(validator.test(Player1_ID, null, buildTrack));

	}

	/**
	 * A invalid player turn
	 * @throws IOException 
	 */
	@Test
	public void invalidPlayersCommmand() throws IOException{
		
		initializeGameControllerTwoPlayer();
		
		TakeSecretCard tsc2 = new TakeSecretCard(Player2_ID);
		
		assertFalse(validator.test(Player1_ID, RANDOM_CMD1, tsc2));
		
	}
	
	/**
	 * Last action is a invalid command
	 * Provoked once a StackOverflow Exception
	 * 
	 * @throws IOException 
	 */
	@Test(timeout = 2000)
	public void lastValidCommandInvalid() throws IOException{
		initializeGameControllerTwoPlayer();
		
		NewMissions nw1 = new NewMissions(Player1_ID, null);
		ReturnMissions rm1 = new ReturnMissions(Player1_ID, null);
		
		assertFalse(validator.test(Player1_ID, nw1, rm1));
	}
	
	/**
	 * Tests a singleplayer game
	 * @throws IOException 
	 */
	@Test
	public void singePlayerDrawTwoLocomotives() throws IOException {
		
		initializeGameControllerSinglePlayer();
		
		gameState.getRessourceManager().getOpenRessourceDeck()[2] = TrackKind.ALL;
		
		TakeCard takeLoco1 = new TakeCard(Player1_ID, TrackKind.ALL);		//Hand is not full
		TakeCard takeLoco2 = new TakeCard(Player1_ID, TrackKind.ALL);
		
		assertTrue(!validator.test(Player1_ID, takeLoco1, takeLoco2));
	}
	
	/**
	 *	MissionManager is Empty 
	 * @throws IOException 
	 */
	@Test(timeout = 1000)
	public void missionManagerEmpty() throws IOException{
		
		initializeGameControllerTwoPlayer();

		MissionManager mm = gameState.getMissionManager();
		while (!mm.isEmpty()) {
			mm.drawMission();
		}
		
		// TODO FIX
//		GetMissions gm2 = new GetMissions(Player2_ID);
		
//		assertFalse(validator.test(Player2_ID, RANDOM_CMD1, gm2));	
	}
	
	/**
	 *  closed Deck of RM is empty
	 * @throws IOException 
	 */
	@Test(timeout = 1000000)
	public void ressouceManagerClosedEmpty() throws IOException{
		
		initializeGameControllerTwoPlayer();
		
		RessourceManager rm = this.gameState.getRessourceManager();
		if (rm.isClosedDeckEmpty()) rm.refillOpenDeck();
		
		TakeSecretCard tsc2 = new TakeSecretCard(Player2_ID);
		
		assertTrue(validator.test(Player2_ID, RANDOM_CMD2, tsc2));
	}
	
	/**
	 *  open Deck of RM is empty
	 * @throws IOException 
	 */
	@Test(timeout = 1000)
	public void ressourceManagerOpenEmpty() throws IOException{
		
		initializeGameControllerTwoPlayer();
		
		RessourceManager rm = this.gameState.getRessourceManager();
		
		if (rm.isClosedDeckEmpty()) rm.refillOpenDeck();
		
		for (int i = 0; i < 5; i++) {
			rm.getOpenRessourceDeck()[i] = null;
		}	
		
		TakeCard tc2 = new TakeCard(Player2_ID,TrackKind.ALL);
		
		assertFalse(validator.test(Player2_ID, RANDOM_CMD1, tc2));	
	}
	
	/**
	 *  Open deck card, which is tried to draw, does not exist 
	 * @throws IOException 
	 */
	@Test
	public void reManagerOpenColorNotExist() throws IOException{
		
		initializeGameControllerSinglePlayer();
		
		RessourceManager rm = this.gameState.getRessourceManager();
		for (int i = 0; i < 5; i++){
			rm.getOpenRessourceDeck()[i] = TrackKind.BLACK;
		}
		
		TakeCard tc2 = new TakeCard(Player1_ID, TrackKind.BLUE);
		
		assertFalse(validator.test(Player1_ID, RANDOM_CMD1, tc2));	
	}
	
	/**
	 * players maxhandsize is reached
	 * @throws IOException 
	 */
	@Test
	public void playerMaxHandSize() throws IOException{
		
		initializeGameControllerTwoPlayer();
		
		gameState.getPlayerFromId(Player2_ID).giveRessourceCards(TrackKind.BLACK,6);
		gameState.getPlayerFromId(Player2_ID).giveRessourceCards(TrackKind.RED,6);
		gameState.getPlayerFromId(Player2_ID).giveRessourceCards(TrackKind.BLUE,6);
		gameState.getPlayerFromId(Player2_ID).giveRessourceCards(TrackKind.GREEN,6);
		gameState.getPlayerFromId(Player2_ID).giveRessourceCards(TrackKind.ALL,4);		
		
		TakeSecretCard tsc2 = new TakeSecretCard(Player2_ID);
		
		// TODO check Jannic richtig?
		assertFalse(validator.test(Player2_ID, null, tsc2));
		assertTrue(validator.test(Player2_ID, RANDOM_CMD1, tsc2));
	}
	
	/**
	 * Test what happen if a track has already an owner
	 * When a player tries to build again on his track
	 * Valid build a free Track
	 * @throws IOException 
	 */
	@Test
	public void trackOwnerTests() throws IOException{
		
		initializeGameControllerTwoPlayer();
		
		gameState.getGameBoard().buildTrack(TRACK0_ID, gameState.getPlayerFromId(Player1_ID));
		gameState.getGameBoard().buildTrack(TRACK1_ID, gameState.getPlayerFromId(Player2_ID));
		
		gameState.getPlayerFromId(Player2_ID).giveRessourceCards(TrackKind.BLACK,6);	
		
		BuildTrack btInvalid1 = new BuildTrack(Player2_ID, TRACK0_ID, TrackKind.BLACK, 0);
		BuildTrack btInvalid2 = new BuildTrack(Player2_ID, TRACK1_ID, TrackKind.BLACK, 0);
		BuildTrack btValid = new BuildTrack(Player2_ID, TRACK2_ID, TrackKind.BLACK, 0);
		
		assertFalse(validator.test(Player2_ID, null, btInvalid1));
		assertFalse(validator.test(Player2_ID, null, btInvalid2));
		assertTrue(validator.test(Player2_ID, null, btValid));
	}
	
	
	/**
	 * Wrong Track ID
	 * @throws IOException 
	 */
	@Test
	public void buildInvalidID() throws IOException{
		initializeGameControllerTwoPlayer();
	
		BuildTrack btInvalid1 = new BuildTrack(Player2_ID, Short.MAX_VALUE, TrackKind.BLACK,0);
		
		assertFalse(validator.test(Player2_ID, RANDOM_CMD1, btInvalid1));
	}
	
	
	/**
	 * a player tries to build a track without enough resources
	 * @throws IOException 
	 */
	@Test
	public void notEnoughRessourcesToBuild() throws IOException{
		
		initializeGameControllerTwoPlayer();
		
		gameState.getGameBoard().buildTrack(TRACK0_ID, gameState.getPlayerFromId(Player1_ID));
		
		BuildTrack buildTrack2 = new BuildTrack(Player2_ID,(short) 0, TrackKind.BLACK,0);

		assertFalse(validator.test(Player2_ID, RANDOM_CMD1, buildTrack2));
	}
	
	/**
	 *	A player returns invalid missions 
	 * @throws IOException 
	 */
	@Test 
	public void missionsReturned() throws IOException{
		
		initializeGameControllerTwoPlayer();
		
		Mission mission1 = gameState.getMissionManager().drawMission();
		Mission mission2 = gameState.getMissionManager().drawMission();
		Mission mission3 = gameState.getMissionManager().drawMission();
		
		Set<Mission> missionsGiven = new HashSet<Mission>(Arrays.asList(mission1, mission2, mission3));
		Set<Mission> missionsReturnedInvalid1 = new HashSet<Mission>(Arrays.asList(mission1, mission2, mission3));
		Set<Mission> missionsReturnedValid1 = new HashSet<Mission>(Arrays.asList(mission1, mission3));
		Set<Mission> missionsReturnedValid2 = new HashSet<Mission>(Arrays.asList(mission1));
		Set<Mission> missionsReturnedValid3 = new HashSet<Mission>(Arrays.asList());
		Set<Mission> missionsReturnedValid4 = new HashSet<Mission>(Arrays.asList(mission1, mission1));
		
		GetMissions gm1 = new GetMissions(Player1_ID, missionsGiven);
		ReturnMissions rm1Invalid1 = new ReturnMissions(Player1_ID, missionsReturnedInvalid1);
		ReturnMissions rm1Valid1 = new ReturnMissions(Player1_ID, missionsReturnedValid1);
		ReturnMissions rm1Valid2 = new ReturnMissions(Player1_ID, missionsReturnedValid2);
		ReturnMissions rm1Valid3 = new ReturnMissions(Player1_ID, missionsReturnedValid3);
		ReturnMissions rm1Valid4 = new ReturnMissions(Player1_ID, missionsReturnedValid4);
		
		assertFalse(validator.test(Player1_ID, gm1, rm1Invalid1));
		assertTrue(validator.test(Player1_ID, gm1, rm1Valid1));
		assertTrue(validator.test(Player1_ID, gm1, rm1Valid2));
		assertTrue(validator.test(Player1_ID, gm1, rm1Valid3));
		// We changed Lists to Set so we have no trouble with dupliactes
		assertTrue(validator.test(Player1_ID, gm1, rm1Valid4));
	}
	
	/**
	 * Player tries to pay with cards, he does not have
	 * Player tries to pay with to much cards
	 * Player pays with correct resources
	 * @throws IOException 
	 */
	@Test
	public void payTunnelAddCosts() throws IOException{
		
		initializeGameControllerTwoPlayer();
		
		gameState.getPlayerFromId(Player1_ID).giveRessourceCards(TrackKind.BLACK, 2);	
		gameState.getPlayerFromId(Player1_ID).giveRessourceCards(TrackKind.ALL, 1);
		
		BuildTrack tunnel = new BuildTrack(Player1_ID, TRACK2_ID, TrackKind.BLACK, 0);
		
		AdditionalCost ad1 = new AdditionalCost(Player1_ID, 3, tunnel);
		AdditionalCost ad2 = new AdditionalCost(Player1_ID, 0, tunnel);
		
		PayTunnel ptInvalid1 = new PayTunnel(Player1_ID, 0);
		PayTunnel ptInvalid2  = new PayTunnel(Player1_ID, 2);
		PayTunnel ptInvalid3  = new PayTunnel(Player1_ID, 4);
		PayTunnel ptValid = new PayTunnel(Player1_ID, 1);	
		
		assertFalse(validator.test(Player1_ID, ad1, ptInvalid1));
		assertFalse(validator.test(Player1_ID, ad1, ptInvalid2));
		assertFalse(validator.test(Player1_ID, ad1, ptInvalid3));
		assertTrue(validator.test(Player1_ID, ad2, ptValid));
	}
	
	
	//CommandSequencTests
	
	
	/**
	 * Tests every command sequence, which can happen on a open game situation
	 * @throws IOException 
	 */
	@Test
	public void commandSequenceTests() throws IOException{
		
		initializeGameControllerTwoPlayer();
		
		gameState.getPlayerFromId(Player1_ID).giveRessourceCards(TrackKind.BLACK,3);	
		gameState.getPlayerFromId(Player2_ID).giveRessourceCards(TrackKind.BLACK,3);	
		BuildTrack bt1 = new BuildTrack(Player1_ID,TRACK0_ID, TrackKind.BLACK,0);
		BuildTrack bt2 = new BuildTrack(Player2_ID,TRACK2_ID, TrackKind.BLACK,0);
		
		RessourceManager rm = this.gameState.getRessourceManager();
		rm.getOpenRessourceDeck()[0] = TrackKind.ALL;
		rm.getOpenRessourceDeck()[1] = TrackKind.ALL;
		rm.getOpenRessourceDeck()[2] = TrackKind.BLACK;
		rm.getOpenRessourceDeck()[3] = TrackKind.BLACK;
		rm.getOpenRessourceDeck()[4] = TrackKind.BLACK;
		
		TakeCard tc1 = new TakeCard(Player1_ID, TrackKind.BLACK);
		TakeCard tc2 = new TakeCard(Player2_ID, TrackKind.BLACK);
		TakeCard tcloco1 = new TakeCard(Player1_ID, TrackKind.ALL);
		TakeCard tcloco2 = new TakeCard(Player2_ID, TrackKind.ALL);
		
		TakeSecretCard tsc1 = new TakeSecretCard(Player1_ID);
		TakeSecretCard tsc2 = new TakeSecretCard(Player2_ID);
		
		Mission mission1 = gameState.getMissionManager().drawMission();
		Mission mission2 = gameState.getMissionManager().drawMission();
		Mission mission3 = gameState.getMissionManager().drawMission();
		
		Set<Mission> missions = new HashSet<Mission>(Arrays.asList(mission1, mission2, mission3));
		NewMissions nm1 = new NewMissions(Player1_ID,missions);
		NewMissions nm2 = new NewMissions(Player2_ID,missions);
		
		ReturnMissions rm1 = new ReturnMissions(Player1_ID, missions);
		ReturnMissions rm2 = new ReturnMissions(Player2_ID, missions);
		
		// TODO
//		BuildTrack tunnel1 = new BuildTrack(Player1_ID, tid, kind, 0);
//		BuildTrack tunnel2 = new BuildTrack(Player1_ID, tid, kind, 0);
//
//		AdditionalCost ad1 = new AdditionalCost(Player1_ID, 0);
//		AdditionalCost ad2 = new AdditionalCost(Player2_ID, 0);
//		
//		PayTunnel pt1 = new PayTunnel(Player1_ID, 0);
//		PayTunnel pt2 = new PayTunnel(Player2_ID, 0);
//		
//		Action[] newTurn1 = {tc1, tsc1, tcloco1, bt1, nm1};
//		Action[] inverseNewTurn1 = {pt1, ad1,rm1};
		
			
//		//null
//		Action[] validActions_null = newTurn1;
//		for (Action act: validActions_null){
//			assertTrue(validator.test(Player1_ID, null , act));
//		}
//		Action[] invalidActions_null = inverseNewTurn1;
//		for (Action act: invalidActions_null ){
//			assertFalse(validator.test(Player1_ID, null, act));
//		}
		
		////////////////////////////////////////////////////////////////////
		
		//tc2 - Single Turn
		// TODO
//		Action[] validActions_tc2_2 = {tc2, tsc2,};
//		for (Action act: validActions_tc2_2){
//			assertTrue(validator.test(Player2_ID, tc2 , act));
//		}
//		Action[] invalidActions_tc2_2 = {tcloco2, bt2, pt2, ad2, nm2, rm2};
//		for (Action act: invalidActions_tc2_2 ){
//			assertFalse(validator.test(Player2_ID, tc2, act));
//		}
		
		
		
		
		
		
//		//tc2 - Two Turns
//		
//		Action[] validActions_tc2_1 = newTurn1;
//		for (Action act: validActions_tc2_1){
//			assertTrue(validator.test(Player1_ID, tc2 , act));
//		}
//		Action[] invalidActions_tc2_1 = inverseNewTurn1;
//		for (Action act: invalidActions_tc2_1 ){
//			assertFalse(validator.test(Player1_ID, tc2, act));
//		}
		
		/////////////////////////////////////////////////////////////
		
//		//tcloco2 - Two Turns
//		Action[] validActions_tcloco2_1 = newTurn1;
//		for (Action act: validActions_tcloco2_1){
//			assertTrue(validator.test(Player1_ID, tcloco2 , act));
//		}
//		Action[] invalidActions_tcloco2_1 = inverseNewTurn1;
//		for (Action act: invalidActions_tcloco2_1 ){
//			assertFalse(validator.test(Player1_ID, tcloco2, act));
//		}
//		
		///////////////////////////////////////////////////////////////////
		
		//tsc2 - Single Turn
		// TODO
//		Action[] validActions_tsc2_2 = {tsc2, tc2};
//		for (Action act: validActions_tsc2_2 ){
//			assertTrue(validator.test(Player2_ID, tsc2 , act));
//		}
//		Action[] invalidActions_tsc2_2 = {tcloco2, bt2, pt2, ad2, nm2, rm2};
//		for (Action act: invalidActions_tsc2_2 ){
//			assertFalse(validator.test(Player2_ID, tsc2, act));
//		}
		
//		//tsc2 - Two Turns
//		Action[] validActions_tsc2_1 = newTurn1;
//		for (Action act: validActions_tsc2_1 ){
//			assertTrue(validator.test(Player1_ID, tsc2 , act));
//		}
//		Action[] invalidActions_tsc2_1 = inverseNewTurn1;
//		for (Action act: invalidActions_tsc2_1 ){
//			assertFalse(validator.test(Player1_ID, tsc2, act));
//		}
		
		
		/////////////////////////////////////////////////////////////////
		
		//bt2 -Single Turn
		// TODO
//		Action[] validActions_bt2_2 = {ad2};
//		for (Action act: validActions_bt2_2){
//			assertTrue(validator.test(Player2_ID, bt2 , act));
//		}
//		Action[] invalidActions_bt2_2 = {tc2, tcloco2, tsc2, bt2,pt2, nm2, rm2};
//		for (Action act: invalidActions_bt2_2 ){
//			assertFalse(validator.test(Player2_ID, bt2, act));
//		}
		
		
//		// bt2 - Two Turn
//		Action[] validActions_bt2_1 = newTurn1;
//		for (Action act: validActions_bt2_1){
//			assertTrue(validator.test(Player1_ID, bt2 , act));
//		}
//		Action[] invalidActions_bt2_1 = inverseNewTurn1;
//		for (Action act: invalidActions_bt2_1 ){
//			assertFalse(validator.test(Player1_ID, bt2, act));
//		}
//		
		//////////////////////////////////////////////////////////////////////
		
		//ad2 - Single Turn
		// TODO
//		Action[] validActions_ad2_2 = {pt2};
//		for (Action act: validActions_ad2_2){
//			assertTrue(validator.test(Player2_ID, ad2 , act));
//		}
//		Action[] invalidActions_ad2_2 = {tc2, tsc2, tcloco2, bt2, nm2, ad2, rm2};
//		for (Action act: invalidActions_ad2_2 ){
//			assertFalse(validator.test(Player2_ID, ad2, act));
//		}
		
			
			
		//////////////////////////////////////////////////////////////////////	
		
//		//pt2 - Two Turns
//		Action[] validActions_pt2_1 = newTurn1;
//		for (Action act: validActions_pt2_1){
//			assertTrue(validator.test(Player1_ID, pt2 , act));
//		}
//		Action[] invalidActions_pt2_1 = inverseNewTurn1;
//		for (Action act: invalidActions_pt2_1){
//			assertFalse(validator.test(Player1_ID, pt2, act));
//		}
		
		//////////////////////////////////////////////////////////////////////////
		
		// nm2 - Single Turn
		// TODO
//		Action[] validActions_nm2_2 = {rm2};
//		for (Action act: validActions_nm2_2){
//			assertTrue(validator.test(Player2_ID, nm2 , act));
//		}
//		Action[] invalidActions_nm2_2 = {tc2, tsc2, tcloco2, bt2, nm2, pt2, ad2};
//		for (Action act: invalidActions_nm2_2 ){
//			assertFalse(validator.test(Player2_ID, nm2, act));
//		}
		
		//////////////////////////////////////////////////////////////////////////
		
//		//rm2 - Two turns
//		Action[] validActions_rm2_1 = newTurn1;
//		for (Action act: validActions_rm2_1){
//			assertTrue(validator.test(Player1_ID, rm2 , act));
//		}
//		Action[] invalidActions_rm2_1 = inverseNewTurn1;
//		for (Action act: invalidActions_rm2_1 ){
//			assertFalse(validator.test(Player1_ID, rm2, act));
//		}
	}	
}
