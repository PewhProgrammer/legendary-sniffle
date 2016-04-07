package de.unisaarland.sopra.zugumzug;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import de.unisaarland.sopra.MersenneTwister;
import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.actions.ActionIdHelper;
import de.unisaarland.sopra.zugumzug.actions.server.MaxTrackLength;
import de.unisaarland.sopra.zugumzug.actions.server.NewCity;
import de.unisaarland.sopra.zugumzug.actions.server.NewOpenCard;
import de.unisaarland.sopra.zugumzug.actions.server.NewPlayer;
import de.unisaarland.sopra.zugumzug.actions.server.NewTrack;
import de.unisaarland.sopra.zugumzug.actions.server.ServerAction;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.Die;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.NewCards;
import de.unisaarland.sopra.zugumzug.client.ClientCommunicator;
import de.unisaarland.sopra.zugumzug.model.City;
import de.unisaarland.sopra.zugumzug.model.GameBoard;
import de.unisaarland.sopra.zugumzug.model.GameState;
import de.unisaarland.sopra.zugumzug.model.Mission;
import de.unisaarland.sopra.zugumzug.model.MissionManager;
import de.unisaarland.sopra.zugumzug.model.Player;
import de.unisaarland.sopra.zugumzug.model.RessourceManager;
import de.unisaarland.sopra.zugumzug.model.Track;

public class MapFactory {
	
	private static final int COUNT_GIVE_CARDS = 4;
	private static HashMap<String, TrackKind> colorMap = new HashMap<String, TrackKind>();
	private static ActionIdHelper actionHelper = new ActionIdHelper();
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
	
	/**
	 * This exception indicates that a Die Event was sent
	 * while the initialization phase. 
	 *
	 */
	@SuppressWarnings("serial")
	public class DieInitializeException extends Exception {
		public DieInitializeException() {
		}

		public DieInitializeException(String s) {
			super(s);
		}
	}
	
	private HashMap<Short, City> cityMap = new HashMap<>();
	private GameState gameState;
	private int numPlayers;
	
	private RessourceManager rm;
	private MissionManager mm;
	private GameBoard gameBoard;
	
	public void initializeGameState(final String path, final int numPlayers,
									MersenneTwister rng, GameState gameState ) throws IOException {
		
		initializeGameState(gameState, rng, numPlayers);
		
		FileInputStream file = null;
		InputStreamReader ir = null;
		BufferedReader br = null;
		
		file = new FileInputStream(path);
		ir = new InputStreamReader(file);
		br = new BufferedReader(ir);
		
		String line = getNextLine(br);
		
		validateLine(line, "Limits:");
		line = readRessourceCards(br);
		validateLine(line, "Staedte:");
		line = readCities(br);
		validateLine(line, "Strecken:");
		line = readTracks(br);
		validateLine(line, "Auftraege:");
		readMissions(br);
		
		file.close();
		ir.close();
		br.close();
		
		gameState.setMaxHandSize(calcMaxHandSize());
	}
	
	public ServerAction initializeGameState(final ClientCommunicator communicator,
						GameState gameState, final int numPlayers, final int myId) throws DieInitializeException, IOException {
	
		initializeGameState(gameState, null, numPlayers);
		
		ServerAction serverAction = getNextAction(communicator);
		
		while (serverAction instanceof NewPlayer) {
			NewPlayer p = (NewPlayer) serverAction;
			gameState.addPlayerInPlayerMap(new Player(p.pid, p.name));
			
			serverAction = getNextAction(communicator);
		}
		while (serverAction instanceof NewCards) {
			NewCards nc = (NewCards) serverAction;
			rm.returnCard(nc.kind, nc.count);
			
			serverAction = getNextAction(communicator);
		}
		if (serverAction instanceof MaxTrackLength) {
			MaxTrackLength mtl = (MaxTrackLength) serverAction;
			gameState.setMaxTracks(mtl.maxTracks);
			
			serverAction = getNextAction(communicator);
		} else {
			throw new IllegalArgumentException();
		}
			
		while (serverAction instanceof NewCity) {
			NewCity nc = (NewCity) serverAction;
			City c = new City(nc.cid, nc.x, nc.y, nc.name);
			gameState.getGameBoard().addCity(c);
			
			serverAction = getNextAction(communicator);
		}
		
		while (serverAction instanceof NewTrack) {
			NewTrack nt = (NewTrack) serverAction;
			City cityA = gameBoard.getCityFromId(nt.cityA);
			City cityB = gameBoard.getCityFromId(nt.cityB);
			Track t = new Track(nt.tid, nt.kind, cityA, cityB, nt.length, nt.tunnel);
			gameBoard.addTrack(t);
			
			serverAction = getNextAction(communicator);
		}
		
		int i = 1;
		for (; serverAction instanceof NewOpenCard; i++) {
			NewOpenCard nc = (NewOpenCard) serverAction;
			rm.getOpenRessourceDeck()[i % 5] = nc.kind;
			
			serverAction = getNextAction(communicator);
		}
		i--;
		if (i >= 5 && i % 5 != 0) throw new IllegalStateException("MapFactory: NewOpenCards was not % 5");
		final int decrem = i % 5 == 0 ? -5 : -i;
		rm.incrementCurrentSizeClosedDeck(decrem);
		
		Player p = gameState.getPlayerFromId(myId);
		while (serverAction instanceof NewCards) {
			NewCards nc = (NewCards) serverAction;
			p.giveRessourceCards(nc.kind, nc.count);
			
			serverAction = getNextAction(communicator);
		}
		
		for (Player pN : gameState.getPlayerMap().values()) {
			if (pN.getPid() != myId) {
				pN.incrementRessourceCards(GameController.GAMESTART_CARDS_TO_PLAYER);
			}
		}
		
		gameState.setMaxHandSize(calcMaxHandSize());
		rm.incrementCurrentSizeClosedDeck(-(numPlayers * GameController.GAMESTART_CARDS_TO_PLAYER));
		
		return serverAction;
	}
	
	private ServerAction getNextAction(ClientCommunicator communicator) throws IOException, DieInitializeException {
		ServerAction serverAction = communicator.receive();
		
		switch (serverAction.accept(actionHelper)) {
			case 6:		// Die
			case 34:	// Timeout
				throw new DieInitializeException();
			case 11:	// PlayerLeft
				return getNextAction(communicator);
			case 2:		// NewCards
			case 4:		// NewMissions
			case 12:	// NewPlayer
			case 20:	// MaTrackLength
			case 21:	// NewTrack
			case 22:	// NewCity
			case 17:	// NewOpenCard
				break;
			default:
				throw new IllegalArgumentException("MapFactory: wrong event while initialization: " + serverAction.accept(actionHelper));
		}
		
		return serverAction;
	}
	
	private void initializeGameState(GameState gameState, MersenneTwister rng, final int numPlayers) {
		
		this.gameState = gameState;
		this.numPlayers = numPlayers;
		
		RessourceManager rm = new RessourceManager(rng);
		MissionManager mm = new MissionManager(rng);
		GameBoard gameBoard = new GameBoard();
		
		this.rm = rm;
		this.mm = mm;
		this.gameBoard = gameBoard;
		
		gameState.setRessourceManager(rm);
		gameState.setMissionManager(mm);
		gameState.setGameBoard(gameBoard);
	}
	
	/**
	 * Note: All colors must be in the file. That's
	 * the reason why we check in end the size of the
	 * color set.
	 * 
	 * @return
	 * @throws IOException
	 */
	private String readRessourceCards(BufferedReader br) throws IOException {

		String line = getNextLine(br);
		EnumSet<TrackKind> colSet = EnumSet.noneOf(TrackKind.class);
		
		while (true) {
			
			if (line.startsWith("MaxTracks")) {
				addMaxTracks(line);
			} else {
				if (!addRessourceCard(line, colSet))
					break;
			}
				
			line = getNextLine(br);
		}
		
		if (colSet.size() != 9)
			throw new IllegalArgumentException("MapFactory: Not all colors are in map");
		if (rm.getCurrentOverallNumberOfCards() < COUNT_GIVE_CARDS * numPlayers)
			throw new IllegalArgumentException("MapFactory: Not enough cards");
		
		return line;
	}
	
	private boolean addRessourceCard(String line, EnumSet<TrackKind> colSet) {
		
		String[] elements = line.split(",");
		
		if (elements.length != 2) return false;
		
		String colorStr = elements[0];
		
		TrackKind color = colorMap.get(colorStr);
		if (color == null) return false;
		colSet.add(color);
		
		final int count = Integer.parseInt(elements[1]);
	
		if (rm.getClosedRessourceDeck().get(color) != 0)
			throw new IllegalArgumentException("MapFactory: Duplicate color");
		
		rm.returnCard(color, count);
		
		return true;
	}
	
	private void addMaxTracks(String line) throws IOException {
		
		String[] elements = line.split(",");
		
		if (elements.length != 2
				|| !elements[0].equals("MaxTracks"))
			throw new IllegalArgumentException("MapFactory: expected \"MaxTracks\"");
		
		final int maxTracks = Integer.parseInt(elements[1]);
		
		gameState.setMaxTracks(maxTracks);
	}
	
	private String readCities(BufferedReader br) throws IOException {
		short id = 0;
		String line = getNextLine(br);
		for (; addCity(line, id); line = getNextLine(br), id++);
		if (id < calcFaculty(numPlayers)) throw new IllegalArgumentException("MapFactory: Not enough cities");
		return line;
	}
	
	private boolean addCity(String line, final short id) {
		
		String[] elements = line.split(",");
		
		if (elements.length != 3) return false;

		final int x = Integer.parseInt(elements[1]);
		final int y = Integer.parseInt(elements[2]);
		
		City city = new City(id, x, y, elements[0]);
		cityMap.put(id, city);
		gameBoard.addCity(city);
		
		return true;
	}
	
	private String readTracks(BufferedReader br) throws IOException {
		short id = 0;
		String line = getNextLine(br);
		for (; addTrack(line, id); line = getNextLine(br), id++);
		return line;
	}
	
	private boolean addTrack(String line, final short id) {

		String[] elements = line.split(",");
		
		if (elements.length != 5) return false;

		final short cityIdA = Short.parseShort(elements[0]);
		City cityA = cityMap.get(cityIdA);
		final short cityIdB = Short.parseShort(elements[1]);
		City cityB = cityMap.get(cityIdB);
		final int costs = Integer.parseInt(elements[2]);
		
		if (costs > 6 || costs < 1)
			throw new IllegalArgumentException("MapFactory: costs are not between 1 and 6");
		
		String colorStr = elements[3];
		TrackKind color = colorMap.get(colorStr);
		if (color == null) return false;
		
		boolean isTunnel;
		if (elements[4].equals("N")) isTunnel = false;
		else if (elements[4].equals("T")) isTunnel = true;
		else return false;
		
		if (cityA == null
				|| cityB == null
				|| cityIdA == cityIdB)
			throw new IllegalArgumentException("MapFactory: illegal city id " + cityIdA + " or " + cityIdB);
		
		Track track = new Track(id,
								color,
								cityA,
								cityB,
								costs,
								isTunnel);
		
		gameBoard.addTrack(track);
		
		return true;
	}
	
	private void readMissions(BufferedReader br) throws IOException {

		List<Mission> missionList = new ArrayList<Mission>();
		int id = 0;
		for (String line = getNextLine(br); line != null; line = getNextLine(br)) {
			Mission m = getMissionFromLine(id, line);
			if (!missionList.contains(m))
				missionList.add(m);
			else
				throw new IllegalArgumentException("MapFactory: Duplicate missions: "
													+ m.getCityA() + "," + m.getCityB() + "," + m.getPoints());
			
			id++;
		}
		
		if (missionList.size() < 3 * numPlayers) throw new IllegalArgumentException("MapFactory: too less missions");
		
		mm.insertMissions(missionList);
	}
	
	private Mission getMissionFromLine(int id, String line) throws IOException {
		
		String[] elements = line.split(",");
		
		if (elements.length != 3) throw new IllegalArgumentException("MapFactory: wrong mission");
		
		final short cityIdA = Short.parseShort(elements[0]);
		final short cityIdB = Short.parseShort(elements[1]);
		final int costs = Integer.parseInt(elements[2]);
		
		if (cityMap.get(cityIdA) == null
				|| cityMap.get(cityIdB) == null)
			throw new IllegalArgumentException("MapFactory: illegal city id " + cityIdA + " or " + cityIdB);
		
		return new Mission(id, costs, cityIdA, cityIdB);
	}
	
	//////////////////////////////////
	//	Helper methods mostly for validating
	//////////////////////////////////
	
	private int calcMaxHandSize() {
		int maxHandSize = (int) Math.ceil(rm.getCurrentOverallNumberOfCards() / (double) numPlayers);
		return maxHandSize - 1;
	}
	
	/**
	 * Calculates a pseudo faculty for number of cities.
	 * It is for n < 1 not the faculty and not recursive
	 * to provoke no StackOverflow
	 * 
	 * @param n
	 * @return
	 */
	private static int calcFaculty(int n) {
		
		if (n < 1) return 0;
		
		int sum = 1;
		while (n > 1) {
			sum *= n;
			n--;
		}
		
		return sum;
	}
	
	private static boolean isComment(String line) {
		return line.startsWith("#");
	}
	
	private static String getNextLine(BufferedReader br) throws IOException {
		
		String line = br.readLine();
		
		while (line != null && isComment(line))
			line = br.readLine();
		
		return line;
		
	}
	
	private static void validateLine(String givenLine, final String expectedLine) throws IOException {
		
		if (!expectedLine.equals(givenLine)) {
			throw new IllegalArgumentException("MapFactory: expected " + expectedLine);
		}
	}
	
}
