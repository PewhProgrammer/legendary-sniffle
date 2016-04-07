package de.unisaarland.sopra.zugumzug.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.Gamer;
import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.client.*;
import de.unisaarland.sopra.zugumzug.actions.server.*;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.*;
import de.unisaarland.sopra.zugumzug.gui.model.PlayerGui;
import de.unisaarland.sopra.zugumzug.gui.view.*;
import de.unisaarland.sopra.zugumzug.model.GameState;
import de.unisaarland.sopra.zugumzug.model.Mission;
import de.unisaarland.sopra.zugumzug.model.Player;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GUIGamer extends Application implements Gamer, ActionServerVisitor<Void> {

	private static Stage primaryStage;

	private static AnchorPane rootLayout;
	private static BorderPane mainScene;
	private static AnchorPane playerOverview;
	private static AnchorPane rulesLayout;

	private static RootLayoutController rootController;
	private static PlayerOverviewController playerOverviewController;
	// private static LoginScreenController loginScreenController;
	private static RulesLayoutController rulesController;

	private ObservableList<PlayerGui> playerData = FXCollections.observableArrayList();

	private static GameState gameState;
	private static Graph graph;

	private static int playerID;
	private int currentPlayer;
	private static PlayerColor playerColor;
	private Player longestTrackPlayer;

	// TODO check whether it is supposed to be static
	private static ClientAction currentAction;
	private static TrackKind[] openCards;

	private static boolean wasStarted = false;
	private static boolean wasInitialized = false;
	private static List<ServerAction> uncompletedActions = new ArrayList<>();

	private MediaPlayer mediaPlayer;
	private boolean firstSongBool = true;
	
	public static void main(String[] args) {
		launch();
	}

	//////////////////////////////////
	// JavaFX methods
	//////////////////////////////////

	/**
	 * Main method, starts the primary Stage
	 */
	@Override
	public void start(Stage newPrimaryStage) throws IOException {

		System.out.println("GUIGamer start");

		primaryStage = newPrimaryStage;
		primaryStage.setTitle("Zugumzug");

		try {
			FXMLLoader loaderMain = new FXMLLoader();
			loaderMain.setLocation(GUIGamer.class.getResource("view/MainScene.fxml"));
			mainScene = (BorderPane) loaderMain.load();
			setMainScene(mainScene);
			Scene sc = new Scene(mainScene);
			primaryStage.setScene(sc);

			FXMLLoader loaderRoot = new FXMLLoader();
			loaderRoot.setLocation(GUIGamer.class.getResource("view/RootLayout.fxml"));
			rootLayout = (AnchorPane) loaderRoot.load();
			rootController = loaderRoot.getController();
			rootController.setGamer(this);

			FXMLLoader loaderPlayerOverview = new FXMLLoader();
			loaderPlayerOverview.setLocation(GUIGamer.class.getResource("view/PlayerOverview.fxml"));
			playerOverview = (AnchorPane) loaderPlayerOverview.load();
			playerOverviewController = loaderPlayerOverview.getController();
			playerOverviewController.setMainApp(this);
			
			FXMLLoader loaderRules = new FXMLLoader();
			loaderRules.setLocation(GUIGamer.class.getResource("view/RulesLayout.fxml"));
			rulesLayout = (AnchorPane) loaderRules.load();
			rulesController = loaderRules.getController();
			rulesController.setGuiGamer(this);

			wasStarted = true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (wasInitialized)
			start();
	}

	private void start() {
		showRootLayout();
		if (!uncompletedActions.isEmpty()) {
			for (ServerAction a : uncompletedActions)
				a.accept(this);
		}
	}

	/**
	 * First method called by 'start', initializes the login screen
	 */
	// private void initLoginScreen() {
	// Scene scene = new Scene(loginScreen);
	// primaryStage.setScene(scene);
	// primaryStage.show();
	// loginScreenController.setMainApp(this);
	// }

	/**
	 * this method loads rootLayout into main scene
	 */
	public void showRootLayout() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				playerColor = new PlayerColor(gameState);
				System.out.println(playerColor.getColorFromID(playerID).toString());
				initMap();
				mainScene.setCenter(rootLayout);
				rootController.setTimer();
				rootController.enableTimer();
				rootController.highlightButton();
				rootController.updateTable(currentPlayer);
				rootController.updateOpenDeck();
				List<File> songFiles = new ArrayList<File>();
				songFiles.add(new File("../sounds/Normal_BackGroundMusic.mp3"));
				songFiles.add(new File("../sounds/Normal_NewYork.mp3"));
				songFiles.add(new File("../sounds/Normal_Tetris.mp3"));
				songFiles.add(new File("../sounds/Normal_chucu.mp3"));
				songFiles.add(new File("../sounds/Normal_GameOfThrones.mp3"));
				songFiles.add(new File("../sounds/Normal_AveMaria.mp3"));
				songFiles.add(new File("../sounds/Normal_Vangelis.mp3"));
				songFiles.add(new File("../sounds/Normal_Chamillionaire.mp3"));
				songFiles.add(new File("../sounds/Normal_ItGoesAroundTheWorld-ATC.mp3"));
				rootController.setSongList(songFiles);
				
				playSound(new File("../sounds/Normal_BackGroundMusic.mp3"));

				primaryStage.show();
				fillPlayerOverview();
			}
		});
	}

	/**
	 * this method is called, once you press 'pokalButton'
	 */
	public void showPlayerOverview() {
		mainScene.setCenter(playerOverview);
		playerOverviewController.setMainApp(this);
	}

	public void setRootLayout() {
		try {
			mainScene.setCenter(rootLayout);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setPlayerOverview() {
		try {
			mainScene.setCenter(playerOverview);
			updatePlayerOverview();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setRules() {
		try {
			mainScene.setCenter(rulesLayout);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initMap() {
		graph = new Graph(gameState, primaryStage, this);
		HashMap<Short, Button> cities = graph.getCityButtons();
		HashMap<Short, Button[]> buttons = graph.getTracks();

		rootLayout.getChildren().add(graph.getPaymentBox());

		for (Button[] bu : buttons.values()) {
			rootLayout.getChildren().addAll(bu);
		}
		for (Button b : cities.values()) {
			rootLayout.getChildren().add(b);
		}

		openCards = gameState.getRessourceManager().getOpenRessourceDeck();
	}

	public void playSound(File file) {
		
		if(!(firstSongBool)) mediaPlayer.stop();
		String path = file.toURI().toString();
		String[] pathPieces = path.split("/");
		path = pathPieces[0] + "//";
		for (int i = 1; i < pathPieces.length; i++) {
			if (pathPieces[i].equals("..")) {
				path = path + "/src/main/java/de/unisaarland/sopra/zugumzug/gui";
			} else
				path = path + "/" + pathPieces[i];
		}
		Media musicFile = new Media(path);
		mediaPlayer = new MediaPlayer(musicFile);
		mediaPlayer.play();
		firstSongBool = false;
	}
	
	public void playSoundWithoutStop(File file) {
		String path = file.toURI().toString();
		String[] pathPieces = path.split("/");
		path = pathPieces[0] + "//";
		for (int i = 1; i < pathPieces.length; i++) {
			if (pathPieces[i].equals("..")) {
				path = path + "/src/main/java/de/unisaarland/sopra/zugumzug/gui";
			} else
				path = path + "/" + pathPieces[i];
		}
		Media musicFile = new Media(path);
		mediaPlayer = new MediaPlayer(musicFile);
		mediaPlayer.play();
	}

	public MediaPlayer getMediaPlayer() {
		return this.mediaPlayer;
	}
	
	public void setMainScene(BorderPane main) {
		mainScene = main;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public AnchorPane getRootLayout() {
		return rootLayout;
	}

	public RootLayoutController getRLC() {
		return rootController;
	}
	//////////////////////////////////
	// Gamer methods
	//////////////////////////////////

	@Override
	public ClientAction getInput() {
		System.out.println("GUIGamer getInput #1");

		if (wasStarted)
			rootController.startTimer();

		try {
			while (currentAction == null) {
				Thread.sleep(50);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (wasStarted)
			updateTimer();
			//rootController.stopTimer();

		ClientAction ca = currentAction;
		currentAction = null;
		return ca;
	}

	@Override
	public void update(ServerAction a) {
		System.out.println("GUIGamer update: " + a);
		if (!wasStarted) {
			uncompletedActions.add(a);
		} else {

			rootController.updateStacks(gameState.getRessourceManager().getCurrentSizeClosedDeck());
			rootController.updateRessourceCards();
			a.accept(this);
		}
	}

	public void updateTimer() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				rootController.stopTimer();
				rootController.resetTimer();
				}
			});
	}

	@Override
	public String getName() {
		return "Frank";
	}

	@Override
	public void initialize() {
		System.out.println("GUIGamer initialize GameState initialized");
		wasInitialized = true;
		if (wasStarted)
			start();
	}

	public void initialize(GameState newGameState, final int myId) {
		gameState = newGameState;
		playerID = myId;
	}

	//////////////////////////////////
	// Getter
	//////////////////////////////////

	/**
	 * This method returns the observable list of players, this method is needed
	 * for the player view class
	 * 
	 * @CM: Mit lieben meine ich nicht lieben sondern lieben.
	 *      "Sag mir warum?! ... Ich weiss warum!!!"
	 * 
	 * @return
	 */
	public ObservableList<PlayerGui> getPlayerData() {
		return playerData;
	}

	/**
	 * This function fills the player overview table, this will be called once
	 * during game initialization
	 */
	private void fillPlayerOverview() {

		for (Player p : gameState.getPlayerMap().values()) {
			Color color = playerColor.getColorFromID(p.getPid());
			playerData.add(new PlayerGui(p.getName(), p.getPid(), color, p.getScore(), p.getSumRessourceCards(),
					p.getSumMissions()));
		}
	}

	/**
	 * this function is used to update the player overview, will be called each
	 * time the player presses the 'pokal' button
	 */
	private void updatePlayerOverview() {
		for (Player p : gameState.getPlayerMap().values()) {
			PlayerGui pGui = playerData.get(p.getPid());
			pGui.setSumMission(p.getSumMissions());
			pGui.setName(p.getName());
			pGui.setPoints(p.getScore());
			pGui.setSumCards(p.getSumRessourceCards());
		}
	}

	public PlayerColor getPlayerColor() {
		return playerColor;
	}

	public GameState getGameState() {
		return gameState;
	}

	public int getPlayerID() {
		return playerID;
	}

	public TrackKind[] getOpenCards() {
		return openCards;
	}

	//////////////////////////////////
	// Set currentAction
	//////////////////////////////////

	public void takeCard(int i) {

		currentAction = new TakeCard(playerID, openCards[i]);
	}

	public void takeSecretCard() {
		currentAction = new TakeSecretCard(playerID);
	}

	public void returnMissions(Set<Mission> retMissions) {
		currentAction = new ReturnMissions(playerID, retMissions);
	}

	public void buildTrack(short tid, TrackKind c, int locomotives) {
		currentAction = new BuildTrack(playerID, tid, c, locomotives);
	}

	public void getMissions() {
		currentAction = new GetMissions(playerID, null);
	}

	public void payTunnel(int locomotives) {
		currentAction = new PayTunnel(playerID, locomotives);
	}

	public void leave() {
		currentAction = new Leave(playerID);
		System.out.println("send leave");
	}

	//////////////////////////////////
	// visitor methods to update own
	// GameState when receiving new
	// Server events
	//////////////////////////////////

	@Override
	public Void visit(NewOpenCard a) {
		// TODO Auto-generated method stub
		System.out.println("newOpenCard");
		rootController.updateOpenDeck();
		return null;
	}

	@Override
	public Void visit(PlayerLeft a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(TookCard a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(TookMissions a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(TookSecretCard a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(TrackBuilt a) {
		graph.buildTrack(a.tid, playerColor.getColorFromID(a.pid));
		playSoundWithoutStop(new File("../sounds/TrackBuiltEvent.mp3"));
		return null;
	}

	@Override
	public Void visit(TunnelBuilt a) {
		graph.buildTrack(a.tid, playerColor.getColorFromID(a.pid));
		return null;
	}

	@Override
	public Void visit(TunnelNotBuilt a) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Text tunnelNotBuildText = new Text("Not enough resource Cards to pay");
				tunnelNotBuildText.setFont(Font.font("Yu Mincho Regular", 50));
				rootLayout.getChildren().add(tunnelNotBuildText);
				tunnelNotBuildText.setLayoutX(400.0);
				tunnelNotBuildText.setLayoutY(350.0);
				tunnelNotBuildText.toFront();
				Timeline timeline = new Timeline(
						new KeyFrame(Duration.millis(3000), ae -> rootLayout.getChildren().remove(tunnelNotBuildText)));
				timeline.play();
			}
		});
		return null;
	}

	@Override
	public Void visit(AdditionalCost a) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				graph.buildTunnel(a);
			}
		});
		return null;
	}

	@Override
	public Void visit(Die a) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Text dieText = new Text("You died!");
				dieText.setFont(Font.font("Yu Mincho Regular", 50));
				rootLayout.getChildren().add(dieText);
				dieText.setLayoutX(400.0);
				dieText.setLayoutY(350.0);
				dieText.toFront();
				Timeline timeline = new Timeline(
						new KeyFrame(Duration.millis(5000), ae -> rootLayout.getChildren().remove(dieText)));
				timeline.play();
			}
		});
		return null;
	}

	@Override
	public Void visit(Failure a) {
		System.out.println("Failure:" + a.message);
		Text failureText = new Text("This move is not possible!");
		failureText.setEffect((new DropShadow(3, Color.RED)));
		failureText.setFont(Font.font("Yu Mincho Regular", 50));
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				rootLayout.getChildren().add(failureText);
				failureText.setLayoutX(350.0);
				failureText.setLayoutY(350.0);
				failureText.toFront();
				playSound(new File("../sounds/Failure_ComputerSaysNo.mp3"));
				Timeline timeline = new Timeline(
						new KeyFrame(Duration.millis(2000), ae -> rootLayout.getChildren().remove(failureText)));
				timeline.play();
			}
		});
		return null;
	}

	@Override
	public Void visit(NewCards a) {
		rootController.showDrawnRessourceCard(a.kind);
		return null;
	}

	@Override
	public Void visit(NewMissions a) {
		System.out.println("GUIGamer NewMissions received");
		rootController.showDrawnMissions(a.missions);
		return null;
	}

	// Finish visitor methods

	@Override
	public Void visit(Winner a) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				rootController.showEndGameStats(a, longestTrackPlayer);
				playSound(new File("../sounds/Winner_Queen-WeAreTheChampions.mp3"));
			}
		});
		return null;
	}

	@Override
	public Void visit(FinalMissions a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(FinalPoints a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(LongestTrack a) {
		longestTrackPlayer = gameState.getPlayerFromId(a.pid);
		return null;
	}

	// won't needed visitor methods
	// TODO some has to be checked

	@Override
	public Void visit(ServerAction a) {
		throw new IllegalArgumentException();
	}

	@Override
	public Void visit(Broadcast a) {
		throw new IllegalArgumentException();
	}

	@Override
	public Void visit(GameStart a) {
		System.out.println("GuiGamer GameState");
		return null;
	}

	@Override
	public Void visit(de.unisaarland.sopra.zugumzug.actions.server.conceal.GameState a) {
		throw new IllegalArgumentException();
	}

	@Override
	public Void visit(MaxTrackLength a) {
		throw new IllegalArgumentException();
	}

	@Override
	public Void visit(NewCity a) {
		throw new IllegalArgumentException();
	}

	@Override
	public Void visit(NewPlayer a) {
		throw new IllegalArgumentException();
	}

	@Override
	public Void visit(NewTrack a) {
		throw new IllegalArgumentException();
	}

	@Override
	public Void visit(Conceal a) {
		throw new IllegalArgumentException();
	}

	@Override
	public Void visit(de.unisaarland.sopra.zugumzug.actions.server.conceal.Mission a) {
		throw new IllegalArgumentException();
	}

	@Override
	public Void visit(SendId a) {
		throw new IllegalArgumentException();
	}

	@Override
	public Void visit(NewPlayersToObserver a) {
		throw new IllegalArgumentException();
	}

	@Override
	public Void visit(ObserverLeft a) {
		throw new IllegalArgumentException();
	}

	@Override
	public void updateActivePlayer(Player p) {
		// TODO Auto-generated method stub
		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				currentPlayer = p.getPid();
				rootController.updateTable(currentPlayer);

				if (p.getPid() == playerID) {
					Text failureText = new Text("It's your turn!");
					failureText.setEffect((new DropShadow(3, Color.RED)));
					failureText.setFont(Font.font("Yu Mincho Regular", 100));

					rootLayout.getChildren().add(failureText);
					failureText.setLayoutX(350.0);
					failureText.setLayoutY(350.0);
					failureText.toFront();
					Timeline timeline = new Timeline(
							new KeyFrame(Duration.millis(1000), ae -> rootLayout.getChildren().remove(failureText)));
					timeline.play();
				}

			}
		});
	}

}
