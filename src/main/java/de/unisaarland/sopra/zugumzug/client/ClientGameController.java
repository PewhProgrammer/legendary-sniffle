package de.unisaarland.sopra.zugumzug.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.GetGamerInputThread;
import de.unisaarland.sopra.zugumzug.Flag;
import de.unisaarland.sopra.zugumzug.GameController;
import de.unisaarland.sopra.zugumzug.Gamer;
import de.unisaarland.sopra.zugumzug.MapFactory.DieInitializeException;
import de.unisaarland.sopra.zugumzug.Validator;
import de.unisaarland.sopra.zugumzug.AI.AIDummyLeave;
import de.unisaarland.sopra.zugumzug.AI.AIRandom;
import de.unisaarland.sopra.zugumzug.AI.AI_Aiden;
import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.client.*;
import de.unisaarland.sopra.zugumzug.actions.server.*;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.*;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.Mission;
import de.unisaarland.sopra.zugumzug.gui.GUIGamer;
import de.unisaarland.sopra.zugumzug.model.*;
import de.unisaarland.sopra.zugumzug.model.GameState;
import de.unisaarland.sopra.zugumzug.actions.*;

public class ClientGameController extends GameController implements ActionServerVisitor<Void> {
	
	public final ClientCommunicator communicator;
	private int myId;
	private int failureCount = 0;
	public Action myOwnLastAction = null;
	
	//threads:
	GetGamerInputThread runnable;			// this starts a second Thread to get user input and receive 
	Thread t;												// simultaneous
	
	
	public volatile boolean flag  = false;
	
	private Player mySelf;
	private String name;
	private Gamer gamer;
	 
	public ClientGameController(String host, int port, boolean gui, boolean ai) throws IOException {
		this.gameState = new GameState();
		this.communicator = new ClientCommunicator(host, port);
		this.myId = communicator.getOwnId();
		if (gui) {
			GUIGamer guiMain = new GUIGamer();
			guiMain.initialize(gameState, myId);
			this.gamer = guiMain;
			Thread t = new Thread() {
				@Override
				public void run() {
					GUIGamer.launch(GUIGamer.class);
				}
			};
			t.setName("GUI");
			t.setDaemon(true);
			t.start();
		}
		if (ai) {
//			this.gamer = new AIRandom(gameState, myId);
//			this.gamer = new AIDummyLeave(gameState, myId, validator);
			this.gamer = new AI_Aiden(this.gameState, myId, validator);
		}
		this.name = gamer.getName();
		connect();
	}
	
	/**
	 * This constructor is only for testing purposes. Therefore
	 * we pass a GameState and ClientCommunicator references to keep access.
	 * 
	 * @param host
	 * @param port
	 * @param gameState
	 * @param communicator
	 * @throws IOException
	 */
	public ClientGameController(GameState gameState, ClientCommunicator communicator, Gamer gamer) throws IOException {
		this.gameState = gameState;
		this.communicator =  communicator;
		this.myId = communicator.getOwnId();
		this.gamer = gamer;
		super.setup(new Validator(gameState), gameState);
	}

	/**
	 * This method is to send a Register and waiting for GameState
	 * Afterwards the visit method of the GameState will receive all 
	 * Players
	 */
	public void connect() {
		communicator.send(new Register(name, myId));
		ServerAction serverAction = receive();
		if (serverAction.accept(idHelper) ==
				de.unisaarland.sopra.zugumzug.actions.server.conceal.GameState.actionId){
			serverAction.accept(this);    //  the visit method receives until the numPlayer is reached
		}
		else{
			throw new IllegalArgumentException("Firt Event was not GameState");
		}
	}
	
	
	/**
	 * The runGame method handles the tree phases:
	 * 1. receive map  
	 * 2. receive the start hand of cards and missions
	 * At the arrival of StartGame the visitor of the event will initialize the GameStart
	 * 3. the running game unit Leave, Die or incoming Winner events
	 */ 
	@Override
	public synchronized void runGame() { // and setup
		
		ServerAction serverAction = receiveMap(); // returns last event, which is not part of the map
		
		// Notify gamer that gameState has been fully created
		gamer.initialize();
		super.setup(new Validator(gameState), gameState);
		
		// TODO more elegant was just written to get AI started
		if (!(serverAction instanceof NewMissions)) throw new IllegalArgumentException("Expected NewMissions, was: " + serverAction);
		serverAction.accept(this);
		lastValidAction = null;
		myOwnLastAction = null;
		serverAction = receive();
		while (serverAction instanceof TookMissions) {
			updateGamer(serverAction);
			serverAction = receive();
		}
		
		serverAction.accept(this);

		lastValidAction = null;
		myOwnLastAction = null;
		
		Flag isMyTurn = new Flag(false);
		
		runnable = new GetGamerInputThread(this, isMyTurn);			// this starts a second Thread to get user input and receive 
		t = new Thread(runnable);												// simultaneous
		t.start();
		
		giveGamerCurrentPlayer();
		
		while (true) {
			if (isMyTurn(serverAction)) {	// only returns true, if you really have to react
				isMyTurn.setFlag(true);
			}
			
			// Use this for debugging
//			printRessourceMap(mySelf);
			
			serverAction = receive();
			isMyTurn.setFlag(false);
			System.out.println("received event: " + serverAction.toString());
			serverAction.accept(this);	
		
			if (isTurnOver(serverAction)) {			//TODO  isTurnOver of Winner update ! 
				updateCurrentPlayerIndex();
				giveGamerCurrentPlayer();
				lastValidAction = null;
				myOwnLastAction = null;
			} else {
				if (serverAction.accept(idHelper) == TookCard.actionId ||
						serverAction.accept(idHelper) == TookSecretCard.actionId ||
						serverAction.accept(idHelper) == AdditionalCost.actionId){
					lastValidAction = serverAction;
				}
			}
		}
	
	}
	/**
	 * give the gamer the current Player, e.g. for use in the gui
	 */
	private void giveGamerCurrentPlayer(){
		int pid = activePlayers.get(currentPlayerIndex);
		Player p = gameState.getPlayerFromId(pid);
		gamer.updateActivePlayer(p);
	}
	

	/**
	 * This methods checks with help of a ServerAction if a player has to react 
	 * @param sa
	 * @return
	 */
	public boolean isMyTurn(ServerAction sa) {
		return myId == activePlayers.get(currentPlayerIndex);
	}	
	
	@Override
	protected void exit() {
		System.exit(0);
	}
	
	public int getFailureCount() {
		return failureCount;
	}

	public int getId() {
		return myId;
	}

	public void setID(final int id) {
		this.myId = id;
	}

	public void resetFailureCounter() {
		failureCount = 0;
	}

	public void incrementFailureCounter() {
		failureCount++;
	}

	
	/**
	 * returns last event, which is not part of the map
	 * special case : Map is broken --> Server sends DIE
	 */
	private ServerAction receiveMap() {
		try {
			return mapFactory.initializeGameState(communicator, gameState, activePlayers.size(), myId);
		} catch (DieInitializeException | IOException e) {
			e.printStackTrace();
			exit();
		}
		return null;
	}
	
	/**
	 * This is a wrap method for gamerInput
	 * @return
	 */
	public ClientAction getGamerInput() {
		ClientAction gamerAction = gamer.getInput();
		if (! (gamerAction instanceof Leave)) {
			while (!validator.test(this.myId, myOwnLastAction, gamerAction)) {
				incrementFailureCounter();
				gamer.update(new Failure(myId, "this was your %s internal Failure"));
				gamerAction = gamer.getInput();
			}
			resetFailureCounter();
		}
		return gamerAction;
	}

	/**
	 * To catch the exceptions of the communicator
	 * @return
	 */
	public ServerAction receive() {
		try {
			return communicator.receive();
		} catch (IOException e) {
			e.printStackTrace();;
		}
		throw new IllegalAccessError("connection lost");
	}
	
	/**
	 * This method deletes Player and update therefore
	 * the current player index
	 */
	@Override
	protected void deletePlayer(int pid) {
		if (activePlayers.indexOf(pid) < getCurrentPlayerIndex()){
			decrementCurrentPlayerIndex();
		} else if (activePlayers.get(currentPlayerIndex) == pid) {	// if active player is kicked
			if (currentPlayerIndex == (activePlayers.size() -1)){
				setCurrentPlayerIndex(0);
			}
			lastValidAction = null;										// update current player
		}
		activePlayers.remove(activePlayers.indexOf(pid));
	}
	
	
//	/**
//	 * to add own Client in the model, set currenPlayerIndex to 0
//	 */
//	private void initMySelf() {
//		activePlayers.add(myId);
//		this.mySelf = new Player(myId, this.name);
//		gameState.getPlayerMap().put(myId, this.mySelf);
//		currentPlayerIndex = 0;
//	}
	
	/**
	 * this is a wrap method for update the gamer
	 * @param a
	 */
	private void updateGamer(ServerAction a) {
		gamer.update(a);
	}
	
	
	//////////////////////////////////////////////////////
	///       VISITOR METHODS
	/////////////////////////////////////////////////////
	
	@Override
	public Void visit(FinalMissions a) {
		updateGamer(a);
		return null;
	}

	@Override
	public Void visit(FinalPoints a) {
		updateGamer(a);
		return null;
	}

	@Override
	public Void visit(GameStart a) {
		Collections.sort(activePlayers);
		setCurrentPlayerIndex(0);
		int pid = activePlayers.get(currentPlayerIndex);
		gameState.setCurrentPlayerId(pid);
		updateGamer(a);
		return null;
	}

	/**
	 * this method receives all Players
	 * @param a
	 * @return
	 */
	@Override
	public Void visit(de.unisaarland.sopra.zugumzug.actions.server.conceal.GameState a) {
		ServerAction serverAction;
		while(a.numPlayers > activePlayers.size()){
			serverAction = receive();
			if (serverAction.accept(idHelper) ==  NewPlayer.actionId){
				serverAction.accept(this);
			} else if(serverAction.accept(idHelper) == PlayerLeft.actionId){
				serverAction.accept(this);	
			} else{
				throw new IllegalArgumentException("In setup nothing but NewPlayer and PlayerLeft is legal");
			}
		}
		this.mySelf = gameState.getPlayerFromId(myId);
		return null;
	}

	@Override
	public Void visit(LongestTrack a) {
		updateGamer(a);
		return null;
	}

	@Override
	public Void visit(MaxTrackLength a) {
		gameState.setMaxTracks(a.maxTracks);
		gameState.setMaxHandSize((int)(a.maxTracks / (double) activePlayers.size() +0.5)); //rounds the number >
		return null;
	}

	@Override
	public Void visit(NewCity a) {
		City c = new City(a.cid, a.x, a.y, a.name);
		gameState.getGameBoard().addCity(c);
		return null;
	}

	@Override
	public Void visit(NewOpenCard a) {
		gameState.getRessourceManager().addOpenCard(a.kind); // current size set in method
		updateGamer(a);
		return null;
	}

	@Override
	public Void visit(NewPlayer a) {
		Player p = new Player(a.pid, a.name);
		activePlayers.add(a.pid);
		gameState.addPlayerInPlayerMap(p);
		return null;
	}

	@Override
	public Void visit(NewTrack a) {
		City cityA = gameState.getGameBoard().getCityFromId(a.cityA);
		City cityB = gameState.getGameBoard().getCityFromId(a.cityB);
		Track t = new Track(a.tid, a.kind, cityA, cityB, a.length, a.tunnel);
		gameState.getGameBoard().addTrack(t);
		return null;
	}

	@Override
	public Void visit(PlayerLeft a) {
		if (a.pid == myId) {
			System.exit(0);
		} else {
			deletePlayer(a.pid);
			updateGamer(a);
		}
		return null;
	}

	@Override
	public Void visit(TookCard a) {
		
		if (a.pid != myId){
			Player p = gameState.getPlayerFromId(a.pid);
			p.giveRessourceCards(a.kind, 1);
		} else {
			this.mySelf.giveRessourceCards(a.kind, 1);
		}
		RessourceManager rm = gameState.getRessourceManager();
		rm.removeOpenCard(a.kind);
		updateGamer(a);
		return null;
	}
	@Override
	public Void visit(TookMissions a) {
		if (a.pid != myId){
			gameState.getPlayerFromId(a.pid);
			gameState.getMissionManager().incrementNumberOfMissions(-a.count);
		}
		updateGamer(a);
		return null;
	}

	@Override
	public Void visit(TookSecretCard a) {
		if (a.pid != myId){
			gameState.getPlayerFromId(a.pid).incrementRessourceCards(1);
			gameState.getRessourceManager().incrementCurrentSizeClosedDeck(-1);
		} // else updated in NewCards
		updateGamer(a);
		return null;
	}

	@Override
	public Void visit(TrackBuilt a) {
		Track buildedTrack = gameState.getGameBoard().getTrackFromId(a.tid);
		gameState.getGameBoard().buildTrack(a.tid, gameState.getPlayerFromId(a.pid));
		Player p = gameState.getPlayerFromId(a.pid);
		
		if (a.pid == myId) {
			p.removeCard(TrackKind.ALL, a.locomotives);
			p.removeCard(a.kind, buildedTrack.getCosts() - a.locomotives);
			
		} else {
			p.incrementRessourceCards(- buildedTrack.getCosts());
		}
		
		gameState.getRessourceManager().incrementCurrentSizeClosedDeck(buildedTrack.getCosts());
		
		p.updateScore(getPoitsOfLenght(buildedTrack.getCosts()));
		updateGamer(a);
		return null;
	}

	@Override
	public Void visit(TunnelBuilt a) {
		Track builtTrack = gameState.getGameBoard().getTrackFromId(a.tid);
		gameState.getGameBoard().buildTrack(a.tid, gameState.getPlayerFromId(a.pid));
		Player p = gameState.getPlayerFromId(a.pid);

		if (a.pid == myId) {
			if (lastValidAction instanceof AdditionalCost) {
				int sumLocomotives = a.locomotives;
				int sumCosts = builtTrack.getCosts() + a.additionalCosts;
				p.removeCard(TrackKind.ALL, sumLocomotives);
				p.removeCard(a.kind, sumCosts - sumLocomotives);
			} else {
				throw new IllegalArgumentException();
			}
		} else {
			gameState.getPlayerFromId(a.pid).incrementRessourceCards(-(builtTrack.getCosts() + a.additionalCosts));
		}
		gameState.getRessourceManager()
				.incrementCurrentSizeClosedDeck(builtTrack.getCosts() + a.additionalCosts);
		p.updateScore(getPoitsOfLenght(builtTrack.getCosts()));
		updateGamer(a);
		return null;
	}
	
	@Override
	public Void visit(TunnelNotBuilt a) {
		updateGamer(a);
		return null;
	}

	/**
	 * updates currentPlayerIndex, that the client is able to leave at desire
	 * @param a
	 * @return
	 */
	@Override
	public Void visit(Winner a) { 
		currentPlayerIndex = activePlayers.indexOf(myId);
		updateGamer(a);
		return null;
	}

	@Override
	public Void visit(AdditionalCost a) {
		
		AdditionalCost ad;
		if (myOwnLastAction instanceof BuildTrack){
			BuildTrack bt = (BuildTrack) myOwnLastAction;
			ad = new AdditionalCost(a.pid, a.additionalCosts, bt);
			myOwnLastAction = ad;
		} else {
			throw new IllegalArgumentException();
		}
		
		updateGamer(ad);
		ClientAction ca = getGamerInput();
		communicator.send(ca);
		return null;
	}

	@Override
	public Void visit(Die a) {	
		updateGamer(a);
		exit();
		return null;
	}

	@Override
	public Void visit(Failure a) {
		updateGamer(a);
		return null;
	}


	@Override
	public Void visit(NewCards a) {
		this.mySelf.giveRessourceCards(a.kind, a.count);
		gameState.getRessourceManager().incrementCurrentSizeClosedDeck(-a.count);
		updateGamer(a);
		return null;
	}

	@Override
	public Void visit(NewMissions a) {
		updateGamer(a);
		Set<de.unisaarland.sopra.zugumzug.model.Mission> receivedMissions = a.missions;
		myOwnLastAction = new GetMissions(myId, receivedMissions);
		ClientAction ca = getGamerInput(); //receive a valid Input
		if (ca instanceof ReturnMissions) {
			communicator.send(ca);
			Set<de.unisaarland.sopra.zugumzug.model.Mission> returnedMissions = ((ReturnMissions) ca).missions;
			for (de.unisaarland.sopra.zugumzug.model.Mission m: returnedMissions ) {
				receivedMissions.remove(m);
			}
			this.mySelf.giveMissions(receivedMissions);
			gameState.getMissionManager().insertMissions(new ArrayList<>(receivedMissions));
		} else {
			throw new IllegalArgumentException("Internal Failure excected "
				+ "ReturnMission, but get: " + ca.toString());
		}
		return null;
	}
	
	
	////////////////////////////////////////////////////////////////
	//				UNUSED VISITORS
	////////////////////////////////////////////////////////////////
	
	
	@Override
	public Void visit(ServerAction a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(Broadcast a) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Void visit(Conceal a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(Mission a) {
		throw new IllegalArgumentException("Internal Event of the KommLib received");
	}

	
	@Override
	public Void visit(SendId a) {
		throw new IllegalArgumentException("Internal Event of the KommLib received");
	}

	@Override
	public Void visit(NewPlayersToObserver a) {
		throw new IllegalArgumentException("Oberserver Events are not supported");
	}

	@Override
	public Void visit(ObserverLeft a) {
		throw new IllegalArgumentException("Observer Events are not supported");
	}

}

