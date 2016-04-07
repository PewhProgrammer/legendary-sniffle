package de.unisaarland.sopra.zugumzug.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.unisaarland.sopra.MersenneTwister;
import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.GameController;
import de.unisaarland.sopra.zugumzug.Validator;
import de.unisaarland.sopra.zugumzug.actions.ActionClientVisitor;
import de.unisaarland.sopra.zugumzug.actions.client.BuildTrack;
import de.unisaarland.sopra.zugumzug.actions.client.ClientAction;
import de.unisaarland.sopra.zugumzug.actions.client.GetMissions;
import de.unisaarland.sopra.zugumzug.actions.client.Leave;
import de.unisaarland.sopra.zugumzug.actions.client.NewObserver;
import de.unisaarland.sopra.zugumzug.actions.client.PayTunnel;
import de.unisaarland.sopra.zugumzug.actions.client.Register;
import de.unisaarland.sopra.zugumzug.actions.client.ReturnMissions;
import de.unisaarland.sopra.zugumzug.actions.client.TakeCard;
import de.unisaarland.sopra.zugumzug.actions.client.TakeSecretCard;
import de.unisaarland.sopra.zugumzug.actions.client.Timeout;
import de.unisaarland.sopra.zugumzug.actions.server.Broadcast;
import de.unisaarland.sopra.zugumzug.actions.server.FinalMissions;
import de.unisaarland.sopra.zugumzug.actions.server.FinalPoints;
import de.unisaarland.sopra.zugumzug.actions.server.GameStart;
import de.unisaarland.sopra.zugumzug.actions.server.LongestTrack;
import de.unisaarland.sopra.zugumzug.actions.server.MaxTrackLength;
import de.unisaarland.sopra.zugumzug.actions.server.NewCity;
import de.unisaarland.sopra.zugumzug.actions.server.NewOpenCard;
import de.unisaarland.sopra.zugumzug.actions.server.NewPlayer;
import de.unisaarland.sopra.zugumzug.actions.server.NewTrack;
import de.unisaarland.sopra.zugumzug.actions.server.PlayerLeft;
import de.unisaarland.sopra.zugumzug.actions.server.ServerAction;
import de.unisaarland.sopra.zugumzug.actions.server.TookCard;
import de.unisaarland.sopra.zugumzug.actions.server.TookMissions;
import de.unisaarland.sopra.zugumzug.actions.server.TookSecretCard;
import de.unisaarland.sopra.zugumzug.actions.server.TrackBuilt;
import de.unisaarland.sopra.zugumzug.actions.server.TunnelBuilt;
import de.unisaarland.sopra.zugumzug.actions.server.TunnelNotBuilt;
import de.unisaarland.sopra.zugumzug.actions.server.Winner;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.AdditionalCost;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.Conceal;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.Die;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.Failure;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.NewCards;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.NewMissions;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.NewPlayersToObserver;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.ObserverLeft;
import de.unisaarland.sopra.zugumzug.model.City;
import de.unisaarland.sopra.zugumzug.model.GameBoard;
import de.unisaarland.sopra.zugumzug.model.GameState;
import de.unisaarland.sopra.zugumzug.model.Mission;
import de.unisaarland.sopra.zugumzug.model.MissionManager;
import de.unisaarland.sopra.zugumzug.model.Player;
import de.unisaarland.sopra.zugumzug.model.RessourceManager;
import de.unisaarland.sopra.zugumzug.model.Track;

public class ServerGameController extends GameController implements ActionClientVisitor<Void> {

	private final int numPlayers;
	private final String fileName;
	private final MersenneTwister rng;
	private final ServerCommunicator communicator;
	private final HashMap<Integer, Integer> failureCounter = new HashMap<Integer, Integer>();
	
	private RessourceManager rm;
	
	private List<Broadcast> nextBroadcasts = new ArrayList<Broadcast>();
	private List<Conceal> nextConceals = new ArrayList<Conceal>();

	private List<Integer> clientList = new ArrayList<Integer>();
	
	protected ClientAction currentAction = null;
	
	public ServerGameController(String fileName, int playerNum, int port, int seed) throws IOException {
		this.communicator = new ServerCommunicator(port);
		this.rng = new MersenneTwister(seed);
		this.fileName = fileName;
		this.numPlayers = playerNum;
		this.gameState = new GameState();
		this.rm = gameState.getRessourceManager();
	}
	
	/**
	 * This constructor is only for testing purposes. Therefore
	 * we pass a GameState and ServerCommunicator references to keep access.
	 * 
	 * @param gameState
	 * @param fileName
	 * @param playerNum
	 * @param port
	 * @param seed
	 * @throws IOException
	 */
	public ServerGameController(GameState gameState, String fileName, ServerCommunicator communicator, int playerNum, int seed) throws IOException {
		this.communicator =  communicator;
		this.rng = new MersenneTwister(seed);
		this.numPlayers = playerNum;
		this.fileName = fileName;
		this.gameState = gameState;
		mapFactory.initializeGameState(fileName, playerNum, rng, gameState);
		this.rm = gameState.getRessourceManager();
	}

	/**
	 *  this method is started after the setup and 
	 *  manages the game
	 */
	@Override
	public void runGame() {
	
		send(new GameStart());
		
		while (!isGameOver()) {
			
			// Use this for debugging
//			printRessourceMap(gameState.getPlayerFromId(getCurrentPlayerId()));
			
			this.currentAction = communicator.receive();
			int currentActionId = currentAction.accept(idHelper);
			
			if (!clientList.contains(currentAction.pid) && 
					!(currentActionId == NewObserver.actionId)){
				continue;
			} else if (currentActionId == NewObserver.actionId) {
					currentAction.accept(this);
			} else if (currentActionId == Timeout.actionId) {
				currentAction.accept(this);
			} else if (currentActionId == Leave.actionId) {
				currentAction.accept(this);
			} else if (validator.test(getCurrentPlayerId(), lastValidAction, currentAction)) {
				
				int pid = getCurrentPlayerId();
				resetFailureCounter(pid);
				
				this.currentAction.accept(this); // executes the current Command

				if (isTurnOver(currentAction)) {
					updateCurrentPlayerIndex();
					lastValidAction = null;
				} else {						// this is a special case, we in TunnelBuild lastValidAcvtion
												// is used to save the additional costs and have to be not overwritten
												// by the currentAction
					if (lastValidAction != null){
						if (!(lastValidAction.accept(idHelper) == AdditionalCost.actionId)){
							lastValidAction = currentAction;
						} else{ /** do nothing **/ }
					} else {lastValidAction = currentAction;}
				}
				for (int i = 0; i < nextBroadcasts.size(); i++){
					send(nextBroadcasts.get(i));
				}
				for (int i = 0; i < nextConceals.size(); i++){
					send(nextConceals.get(i));
				}
				nextBroadcasts.clear();
				nextConceals.clear();
				
			} else {
					updateFailureCounter(this.currentAction.pid);
			}
		}
		// when game is over :
		if (activePlayers.size() > 0){
			this.sendGameStatistics();
		}
		this.exit();
	}
	
	/**
	 * this setup method makes everything until the game start
	 * 1. receive new player and observer
	 * 2. if map is valid send it
	 * 3. send open Cards
	 * 4. send cards and missions to every player
	 */
	public void setup() {
		
		receivePlayers();	
		try {
			mapFactory.initializeGameState(fileName, numPlayers, rng, gameState);
			validator = new Validator(gameState);
			rm = gameState.getRessourceManager();
		} catch(IOException | IllegalArgumentException e) {
			String failureMsg = e.getMessage();
			for (int pid: clientList){
				send(new Die(pid, failureMsg));
				if (activePlayers.contains(pid) && clientList.size() >1){
					send(new PlayerLeft(pid)); 	
				} 
			}
			communicator.close();
			System.exit(-1);
		}
		
		Collections.sort(this.activePlayers);
		currentPlayerIndex = 0;
		
		sendMap();
		refillAndSendOpenCards();
		
		// this map is used to save which missions were sended to the players
		HashMap<Integer, GetMissions> missionsGivenToPid = new HashMap<Integer, GetMissions>();
		for (int pid : activePlayers) {
			giveStartCards(pid);
			giveMissionsAtGameStart(pid, missionsGivenToPid);
		}
		
		takeMissionsBackGameStart(missionsGivenToPid);
		this.currentAction = null;
		this.lastValidAction = null;
		this.nextBroadcasts.clear();
	}

	private void receivePlayers() {
		while (this.activePlayers.size() != this.numPlayers) {
			
			this.currentAction = this.communicator.receive();
			int currentActionId = currentAction.accept(idHelper);
			
			if (!clientList.contains(currentAction.pid) && 
					!(currentActionId == NewObserver.actionId)){
				continue;
				
			} else if (currentActionId == NewObserver.actionId) {
				currentAction.accept(this);
				
			} else if(currentActionId == Leave.actionId){
				currentAction.accept(this);
				gameState.getPlayerMap().remove(currentAction.pid);
				
			} else if (currentActionId ==  Register.actionId) {
				currentAction.accept(this);
				
			} else {
				updateFailureCounter(currentAction.pid);
				
			}
		}
		
	}

	/**
	 * this method validates and and collects the missions, that the player return
	 * @param missionsGivenToPid
	 */
	private void takeMissionsBackGameStart(HashMap<Integer, GetMissions> missionsGivenToPid) {

		List<Integer> notGivenBack = new ArrayList<Integer>(activePlayers.size());
		for(int i: activePlayers) {notGivenBack.add(i);};
		
		while (!notGivenBack.isEmpty()) {
			
			currentAction = communicator.receive();
			int pid = this.currentAction.pid;
			
			int currentActionId = currentAction.accept(idHelper);
									
			if (!clientList.contains(currentAction.pid) && 
					!(currentActionId == NewObserver.actionId)){
				continue;
			} else if (currentActionId == NewObserver.actionId) {
				currentAction.accept(this);	
			
			} else if (currentActionId == ReturnMissions.actionId 
					&& notGivenBack.contains(pid)
					&& validator.test(pid, missionsGivenToPid.get(pid), currentAction)) {
				
				lastValidAction = missionsGivenToPid.get(pid);
				returnMissionsFromPlayer(pid, ((ReturnMissions) currentAction).missions);
				notGivenBack.remove(notGivenBack.indexOf(pid));
				send(nextBroadcasts.get(0)); // <- set in returnMissionsFromPlayer
				this.nextBroadcasts.clear();
				
			} else if (currentActionId == Timeout.actionId){
				if (notGivenBack.contains(pid)){
					notGivenBack.remove(notGivenBack.indexOf(pid));
				}
				currentAction.accept(this);
				continue;
				
			} else if (currentActionId == Leave.actionId) {
				if (notGivenBack.contains(pid)){
					notGivenBack.remove(notGivenBack.indexOf(pid));
				}
				currentAction.accept(this);
				continue;

			} else { 
				updateFailureCounter(pid);
				if (!clientList.contains(pid)){			// means the player was kicked
					notGivenBack.remove(notGivenBack.indexOf(pid));
				}
			}
			
		}
	}

	/**
	 * this method sends a player 3 missions to chose and fills up the
	 * missionsGivenToPid Map
	 * @param pid
	 * @param missionsGivenToPid
	 */
	private void giveMissionsAtGameStart(int pid, HashMap<Integer, GetMissions> missionsGivenToPid) {
		
		Mission mission1 = gameState.getMissionManager().drawMission();
		Mission mission2 = gameState.getMissionManager().drawMission();
		Mission mission3 = gameState.getMissionManager().drawMission();
		Set<Mission> missionsList = new HashSet<Mission>(Arrays.asList(mission1, mission2, mission3));
		NewMissions nm = new NewMissions(pid, missionsList);
		GetMissions gm = new GetMissions(pid, missionsList);
		missionsGivenToPid.put(pid, gm);
		send(nm);
	}

	/**
	 * this method generates and collects cards for a
	 * players start hand of cards
	 * @param pid
	 */
	private void giveStartCards(int pid) {
		TrackKind drawedCard;
		int oldCounter = 0;
		HashMap<TrackKind, Integer> sendCardsCollection = new HashMap<TrackKind, Integer>();
		for (int i = 0; i < GAMESTART_CARDS_TO_PLAYER; i++) {
			drawedCard = rm.drawCard();
			if (sendCardsCollection.containsKey(drawedCard)){
				 oldCounter = sendCardsCollection.get(drawedCard);
			}
			else{
				oldCounter = 0;
			}
			sendCardsCollection.put(drawedCard, oldCounter +1 );
			gameState.getPlayerFromId(pid).giveRessourceCards(drawedCard, 1);
		}
		for(TrackKind tk: sendCardsCollection.keySet()){
			NewCards nc = new NewCards(pid, tk,sendCardsCollection.get(tk));
			send(nc);
		}
		
	}

	/**
	 * this method refill the open deck and broadcasts NewOpenCard events to every Client
	 */
	private void refillAndSendOpenCards() {
		List<TrackKind> refreshedCards = rm.refillOpenDeck();
		for(TrackKind tk: refreshedCards){
			NewOpenCard noc = new NewOpenCard(tk);
			send(noc);
		}
	}

	/**
	 * this method sends to a new observer:
	 * 1. GameState to give informations about the number of players
	 * 2. NewPlayer events for all player joined the game so far
	 * @param pid
	 */
	private void sendObserverInformations(int clientId) {
		de.unisaarland.sopra.zugumzug.actions.server.conceal.GameState gs = 
				new de.unisaarland.sopra.zugumzug.actions.server.conceal.GameState(clientId, numPlayers);
		send(gs);
		for (int pid: gameState.getPlayerMap().keySet()){
			Player p = gameState.getPlayerFromId(pid);
			NewPlayersToObserver np = new NewPlayersToObserver(clientId, new NewPlayer(p.getPid(), p.getName()) );
			send(np);
		}
	}

	/**
	 * this method waits for all players send their Leave command
	 * then it closes the connection and exits the program 
	 */
	@Override
	protected void exit() { 
		while (clientList.size() != 0) {
			currentAction = this.communicator.receive();
			int currentActionId = currentAction.accept(idHelper);
			
			if (!clientList.contains(currentAction.pid) && 
					!(currentActionId == NewObserver.actionId)){
				continue;
			} else if (currentActionId == NewObserver.actionId) {
				currentAction.accept(this);	
			} else if (currentActionId == Leave.actionId) {
				currentAction.accept(this);
			} else if (currentActionId == Timeout.actionId){
				currentAction.accept(this); 
			} else{
				updateFailureCounter(currentAction.pid);
			}
		}
		communicator.close();
		System.exit(0);
	}

	void resetFailureCounter(final int pid) {
		failureCounter.replace((Integer) pid, 0);
	}

	void incrementFailureCounter(final int pid) {
		int newCounter = (failureCounter.get(pid)) + 1;
		failureCounter.replace(pid, newCounter);
	}

	protected void drawSecretCard(final int pid) {
		
		TrackKind drawedCard = rm.drawCard();
		gameState.getPlayerFromId(getCurrentPlayerId()).giveRessourceCards(drawedCard, 1);

		nextBroadcasts.add(new TookSecretCard(pid));
		nextConceals.add(new NewCards(pid, drawedCard, 1));
	}

	protected void drawCard(int pid, TrackKind color) {
		rm.drawCard(color);
		gameState.getPlayerFromId(getCurrentPlayerId()).giveRessourceCards(color, 1);

		nextBroadcasts.add(new TookCard(pid, color));
	}

	protected void giveMissionsToPlayer(int pid) {
		MissionManager mm = gameState.getMissionManager();
		Set<Mission> missionsList = new HashSet<Mission>();
		for (int i = 0; i < 3; i++){
			if (!mm.isEmpty()){
				missionsList.add(mm.drawMission());
			}
		}
		currentAction = new GetMissions(pid, missionsList);
		nextConceals.add(new NewMissions(pid, missionsList));
	}

	/**
	 * 
	 * @param pid
	 * @param returnedMissionList
	 */
	protected void returnMissionsFromPlayer(int pid, Set<Mission> returnedMissionList) {
		GetMissions givenAction = (GetMissions) lastValidAction;
		HashSet<Mission> givenMissions = (HashSet<Mission>) givenAction.missions;

		// after his loop the mission the player choose are in the givenMissions list
		for (Mission m : returnedMissionList) {
			givenMissions.remove(m);
		}
		gameState.getMissionManager().returnMissions(returnedMissionList);
		gameState.getPlayerFromId(pid).giveMissions(givenMissions);
		
		int missionsTook = givenMissions.size();
		if (missionsTook == 0 || missionsTook > 3)
			throw new IllegalArgumentException("returnMissionsFromPlayer: returnedMissionList.size" + returnedMissionList.size()
												+ " \ngivenMissions.size: " + givenMissions.size()
												+ " \ngivenAction.pid: " + givenAction.pid
												+ " \ncurrentAction.pid: " + currentAction.pid
												+ " \nvalidator call(curPid, last, cur): "
														+ validator.test(getCurrentPlayerId(), lastValidAction, currentAction)
												+ " \ngetCurrentPlayerId(): " + getCurrentPlayerId()
												);
		nextBroadcasts.add(new TookMissions(pid, missionsTook));
	}

	/**
	 *  This methods builds a track (non Tunnel), adds the answer events to 
	 *  the broadcast/conceal list and refreshes if necessary   
	 * @param pid
	 * @param tid
	 * @param color
	 * @param locomotives
	 */
	protected void buildTrack(int pid, short tid, TrackKind color, int locomotives) {
		gameState.getGameBoard().buildTrack(tid, gameState.getPlayerFromId(pid));
		int costs = gameState.getGameBoard().getTrackFromId(tid).getCosts();
		
		gameState.getPlayerFromId(pid).updateScore(getPoitsOfLenght(costs));
		gameState.getPlayerFromId(pid).removeCard(TrackKind.ALL, locomotives);
		gameState.getPlayerFromId(pid).removeCard(color, costs - locomotives);
		
		nextBroadcasts.add(new TrackBuilt(pid, tid, color, locomotives));
		
		rm.returnCard(TrackKind.ALL, locomotives);
		rm.returnCard(color, costs -locomotives);
		
		List<TrackKind> refreshedCards = rm.refillOpenDeck();
		for(TrackKind tk: refreshedCards){
			nextBroadcasts.add(new NewOpenCard(tk));
		}
	}

	/**
	 * this method send the game statistics:
	 * 1. Final missions 
	 * 2. Longest track
	 * 3.Final points
	 * 4. Winner
	 */
	private void sendGameStatistics() {
		GameBoard gamebord = gameState.getGameBoard();
		HashMap<Integer, Integer> missionsAccomplishedFromPlayer = new HashMap<Integer, Integer>();
		// 1. FinalMissions
		for (int pid : activePlayers) {

			Set<Mission> missionFail = new HashSet<Mission>();
			Set<Mission> missionSuccess = new HashSet<Mission>();
			Player p = gameState.getPlayerFromId(pid);
			for (Mission m : p.getMissionList()) {
				int points = m.getPoints();
				if (gameState.getGameBoard().isMissionAccomplished(pid, m.getCityA(), m.getCityB())) {
					p.updateScore(points);
					missionSuccess.add(m);
				} else {
					p.updateScore(-points);
					missionFail.add(m);
				}
			}
			missionsAccomplishedFromPlayer.put(pid, missionSuccess.size());
			FinalMissions broadcastFinalMissions = new FinalMissions(pid, missionSuccess, missionFail);
			send(broadcastFinalMissions);
		}

		// 2.Longest Track
		int longestTrack = 0;
		int playersLongestTrack;
		for (int pid : activePlayers) { 					// this loop is used
															// to find the
															// longest Track
			playersLongestTrack = gamebord.getLongestTrackFromPlayer(pid);
			if (playersLongestTrack > longestTrack) {
				longestTrack = playersLongestTrack;
			}
		}
		for (int pid : gameState.getPlayerMap().keySet()) { // this loop is used
															// to see which
															// player(s)
															// has/have the
															// longest track
			playersLongestTrack = gameState.getGameBoard().getLongestTrackFromPlayer(pid);
			if (playersLongestTrack == longestTrack) {
				gameState.getPlayerFromId(pid).updateScore(LONGEST_TRACK_POINTS);
				LongestTrack broadcastLongestTrack = new LongestTrack(pid, longestTrack);
				send(broadcastLongestTrack);
			}
		}
		// 3. FinalPoints
		for (int pid : activePlayers) {
			Player p = gameState.getPlayerFromId(pid);
			FinalPoints broadcastFinalPoints = new FinalPoints(pid, p.getScore());
			send(broadcastFinalPoints);
		}

		// 4. Winner
		
		List<Player> winnerList = new ArrayList<Player>();
		Player winner = gameState.getPlayerFromId(activePlayers.get(0)); // if there is no active Player, this method will not get called
		winnerList.add(winner);
		
		int pid;
		for (int i = 1; i < activePlayers.size(); i++){
			pid = activePlayers.get(i);
			Player playerToCheck = gameState.getPlayerFromId(pid);
			if (playerToCheck.getScore() > winner.getScore()) {
				winner = playerToCheck;
				winnerList.clear();
				winnerList.add(winner);
			} else if (playerToCheck.getScore() == winner.getScore()) {
				if (missionsAccomplishedFromPlayer.get(playerToCheck.getPid()) > missionsAccomplishedFromPlayer.get(winner.getPid())) {
					winner = playerToCheck;
					winnerList.clear();
					winnerList.add(winner);
				} else if (missionsAccomplishedFromPlayer.get(playerToCheck.getPid()) == missionsAccomplishedFromPlayer
						.get(winner.getPid())) {
					if (gamebord.getLongestTrackFromPlayer(playerToCheck.getPid()) > gamebord
							.getLongestTrackFromPlayer(winner.getPid())) {
						winner = playerToCheck;
						winnerList.clear();
						winnerList.add(winner);
					} else if ((gamebord.getLongestTrackFromPlayer(playerToCheck.getPid()) == gamebord
							.getLongestTrackFromPlayer(winner.getPid()))) {
						winnerList.add(playerToCheck);
					}
				}

			}
		}
		for (Player p : winnerList) {
			Winner broadcastWinner = new Winner(p.getPid());
			send(broadcastWinner);
		}

	}

	/**
	 * this method update the failure counter of a player and kick him if 
	 * the FAILURES_TO_KICK is reached
	 * Sends : Failure, Die, Player, ObserverLeft
	 * @param pid
	 * @return true if the player was kicked
	 */
	private void updateFailureCounter(int pid) {
		incrementFailureCounter(pid);
		if (failureCounter.get(pid) == FAILURES_TO_KICK) {
			if (activePlayers.contains(pid)){
				deletePlayer(pid);
				send(new Die(pid, "too much failures"));
				if (clientList.size() > 0){					
					send(new PlayerLeft(pid));					// this is a broadcast
				}
			} else{							
				deleteObserver(pid);
				send(new Die(pid, "too much failures")); // No ObserverLeft only by Leave cmd
			}
		} else{
			send(new Failure(pid, "this is not a valid command"));
		}
	}


	/**
	 * this method sends the map of the file to each observer
	 * 1. all resources in the game
	 * 2. the maxTrackLength
	 * 3. Cities
	 * 4. Tracks
	 */
	private void sendMap() {
		for (TrackKind k : rm.getClosedRessourceDeck().keySet()) {
			for (int id : clientList) {
				NewCards nc = new NewCards(id, k, rm.getClosedRessourceDeck().get(k));
				send(nc);
			}
		}
		send(new MaxTrackLength(this.gameState.getMaxTracks()));
		
		for (City c : gameState.getGameBoard().getCitySet()) {
			NewCity nc = new NewCity(c.getID(), c.getName(), c.getX(), c.getY());
			send(nc);
		}
		for (Track t : gameState.getGameBoard().getTrackSet()) {
			NewTrack nt = new NewTrack(t.getID(), t.getCityA().getID(), t.getCityB().getID(), t.getCosts(),
					t.getColor(), t.isTunnel());
			send(nt);
		}
	}
	
	/**
	 * this method is used to have a single interface to the communicator
	 * @param serverAction
	 */
	void send(ServerAction serverAction){
		try {
			communicator.send(serverAction);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * this method deletes a player from client list, failure counter and activeplayerlist
	 * and also updates if necessary the currentplayerindex
	 * 
	 * Sends: Die, PlayerLeft
	 */
	protected void deletePlayer(final int pid) {
		clientList.remove(clientList.indexOf(pid));
		failureCounter.remove(pid);
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
	

	/**
	 * this method deletes an observer from client list and failure counter
	 * Sends: Die
	 * @param pid
	 */
	private void deleteObserver(final int pid) {
		clientList.remove(clientList.indexOf(pid));
		failureCounter.remove(pid);
	}
		
	//////////////////////////////////////////////////////////
	// 						VISITORS						//
	/////////////////////////////////////////////////////////
	
	@Override
	public Void visit(BuildTrack a) {
		
		Track trackToBuild = gameState.getGameBoard().getTrackFromId(a.tid);
		
		if (trackToBuild.isTunnel()) {
			buildTunnel(a);
		} else {
			buildTrack(a.pid, a.tid, a.kind, a.locomotives);
		}
		return null;
	}
	/**
	 * this method generate the additional costs and validate if the player
	 * can pay for it. If he can he get an additional costs event otherwise
	 * a TunnelNotBuild event will be send 
	 * @param a
	 */
	private void buildTunnel(BuildTrack a) {
		int addCosts = 0;
		List<TrackKind> drawedCards = new ArrayList<TrackKind>();
		for (int i = 0; i < 3 && !rm.isClosedDeckEmpty(); i++) {
			TrackKind showedCard = rm.drawCard();
			drawedCards.add(showedCard);
			if (showedCard == a.kind || showedCard == TrackKind.ALL) {
				addCosts++;
			}
		}
		
		for (TrackKind tk: drawedCards) {
			rm.returnCard(tk, 1);
		}

		AdditionalCost ac = new AdditionalCost(a.pid, addCosts, a);
		
		if (validator.test(a.pid, a, ac)){
			nextConceals.add(ac);
			// special case
			lastValidAction = ac;		
		} else {
			TunnelNotBuilt tnb = new TunnelNotBuilt(a.pid, a.tid);
			nextBroadcasts.add(tnb);
			lastValidAction = tnb;									//special case in isTurnOver
		}
		
	}

	@Override
	public Void visit(ClientAction a) {
		throw new UnsupportedOperationException("Invalid current Action");
	}

	/**
	 * Gives missions to a player 
	 */
	@Override
	public Void visit(GetMissions a) {
		giveMissionsToPlayer(a.pid);
		return null;
	}

	/**
	 * this method is used to handle a delete a player from the active-lists 
	 * after a Leave command
	 */
	@Override
	public Void visit(Leave a) {
		if (this.activePlayers.contains(a.pid)){
			deletePlayer(a.pid);
			send(new PlayerLeft(a.pid));
		} else {
			deleteObserver(a.pid);
			send(new ObserverLeft(a.pid));
		}
		return null; 
	}

	/**
	 * this method adds a new Observer in the client list
	 */
	@Override
	public Void visit(NewObserver a) {
		if (!this.clientList.contains(a.pid)) {
			this.clientList.add(a.pid);
			this.failureCounter.put(a.pid, 0);
			sendObserverInformations(a.pid);
		} else{
			updateFailureCounter(a.pid);
		}
		return null;
	}

	/**
	 *  To built the Tunnel after a valid PlayTunnel command
	 */
	@Override
	public Void visit(PayTunnel a) {
		Player p = gameState.getPlayerFromId(a.pid);
		
		AdditionalCost ac = (AdditionalCost) lastValidAction;
		BuildTrack bt = ac.track;
		
		gameState.getGameBoard().buildTrack(bt.tid, p);
		int trackCosts = gameState.getGameBoard().getTrackFromId(bt.tid).getCosts();
		
		int sumLocomotives = a.locomotives + bt.locomotives;
		int sumNormalCosts = trackCosts + ac.additionalCosts;
		
		p.updateScore(getPoitsOfLenght(trackCosts));
		p.removeCard(TrackKind.ALL, sumLocomotives);
		p.removeCard(bt.kind, sumNormalCosts - sumLocomotives);
		
		nextBroadcasts.add(new TunnelBuilt(a.pid, bt.tid, ac.additionalCosts, bt.kind, sumLocomotives));
		
		rm.returnCard(TrackKind.ALL, sumLocomotives);
		rm.returnCard(bt.kind, sumNormalCosts - sumLocomotives);
		
		List<TrackKind> refreshedCards = rm.refillOpenDeck();
		for(TrackKind tk: refreshedCards){
			nextBroadcasts.add(new NewOpenCard(tk));
		}
		
		return null;
	}
	
	/**
	 * this method is used to register a new Player
	 */
	@Override
	public Void visit(Register a) {
		Player p = null;
		
		if (!this.activePlayers.contains(a.pid)) {
			p = new Player(a.pid, a.myName);
			this.gameState.getPlayerMap().put(a.pid, p);
			this.activePlayers.add(a.pid);
		} else {
			p = gameState.getPlayerFromId(a.pid);
			p.setName(a.myName);
		}
		send(new NewPlayer(p.getPid(), p.getName()));
		
		// if he is NOT in clientList or activePlayers, we can not send a Failure
		return null;
	}

	@Override
	public Void visit(ReturnMissions a) {
		returnMissionsFromPlayer(a.pid, a.missions);
		return null;
	}

	/**
	 * this method draws and refresh cards
	 */
	@Override
	public Void visit(TakeCard a) {
		this.drawCard(a.pid, a.kind);
		List<TrackKind> refreshedCards = rm.refillOpenDeck();
		for(TrackKind tk: refreshedCards){
			nextBroadcasts.add(new NewOpenCard(tk));
		}
		return null;
	}

	@Override
	public Void visit(TakeSecretCard a) {
		drawSecretCard(a.pid);
		return null;
	}

	@Override
	public Void visit(Timeout a) {
		int pid = getCurrentPlayerId();
		send(new Die(pid, "Timeout"));
		deletePlayer(pid);			// Timeout does not know which player was kicked
		if (clientList.size() > 0){
			send(new PlayerLeft(pid));
		}
		return null;
	}

	//////////////////////////////////////////////////////////
	// in the following are unimportant getters and setters	//
	//////////////////////////////////////////////////////////
	
	public HashMap<Integer, Integer> getFailureCounter() {
		return this.failureCounter;
	}

}
