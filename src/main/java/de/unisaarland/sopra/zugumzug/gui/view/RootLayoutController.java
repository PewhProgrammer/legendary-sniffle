package de.unisaarland.sopra.zugumzug.gui.view;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.actions.server.Winner;
import de.unisaarland.sopra.zugumzug.gui.GUIGamer;
import de.unisaarland.sopra.zugumzug.model.City;
import de.unisaarland.sopra.zugumzug.model.GameState;
import de.unisaarland.sopra.zugumzug.model.Mission;
import de.unisaarland.sopra.zugumzug.model.Player;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class RootLayoutController {

	private final static HashMap<TrackKind, ImageView> imageMap = new HashMap<>();

	static {
		imageMap.put(TrackKind.ALL,
				new ImageView(new Image(RootLayoutController.class.getResourceAsStream(("../Images/openLoco.png")))));
		imageMap.put(TrackKind.BLACK,
				new ImageView(new Image(RootLayoutController.class.getResourceAsStream(("../Images/openBlack.png")))));
		imageMap.put(TrackKind.BLUE,
				new ImageView(new Image(RootLayoutController.class.getResourceAsStream(("../Images/openBlue.png")))));
		imageMap.put(TrackKind.GREEN,
				new ImageView(new Image(RootLayoutController.class.getResourceAsStream(("../Images/openGreen.png")))));
		imageMap.put(TrackKind.ORANGE,
				new ImageView(new Image(RootLayoutController.class.getResourceAsStream(("../Images/openOrange.png")))));
		imageMap.put(TrackKind.RED,
				new ImageView(new Image(RootLayoutController.class.getResourceAsStream(("../Images/openRed.png")))));
		imageMap.put(TrackKind.VIOLET,
				new ImageView(new Image(RootLayoutController.class.getResourceAsStream(("../Images/openViolet.png")))));
		imageMap.put(TrackKind.WHITE,
				new ImageView(new Image(RootLayoutController.class.getResourceAsStream(("../Images/openWhite.png")))));
		imageMap.put(TrackKind.YELLOW,
				new ImageView(new Image(RootLayoutController.class.getResourceAsStream(("../Images/openYellow.png")))));
	}

	private GUIGamer guiGamer;
	List<AnchorPane> missionCardAnchorList = new ArrayList<AnchorPane>();
	List<AnchorPane> drawnMissionCardAnchorList = new ArrayList<AnchorPane>();
	List<Mission> drawnMissionsList = new LinkedList<Mission>();
	boolean returnedMissions[] = new boolean[3];
	boolean drawnMissionsHidden = false;
	private RessourceCards ressourceCardMap = new RessourceCards();
	private static final Integer STARTIME = 180;
	private Timeline timeline;
	Text stext = new Text("s");
	private Integer timeSeconds = STARTIME;
	private int songCounter = 1;
	private boolean playing = true;
	
	@FXML
	private Label timerLabel;
	@FXML
	private Button acceptMissionsButton;
	@FXML
	private Button showHideDrawnMissions;
	@FXML
	private Label lablePlayerId;
	@FXML
	private Label lablePoints;
	@FXML
	private Label lableCards;
	@FXML
	private Label lableMissions;
	@FXML
	private Text drawMissionText;
	@FXML
	private Text winnerText;
	@FXML
	private Text playerLTText;
	@FXML
	private Text scoreText;
	@FXML
	private Text ressourceCounter;
	@FXML
	private Button menuButton;
	@FXML
	private Button pokalButton;
	@FXML
	private Button missionDeckButton;
	@FXML
	private Button closedDeckButton;
	@FXML
	private Button playerMissionsButton;
	@FXML
	private Button ressourceRed;
	@FXML
	private Button ressourceOrange;
	@FXML
	private Button ressourceYellow;
	@FXML
	private Button ressourceGreen;
	@FXML
	private Button ressourceBlue;
	@FXML
	private Button ressourceViolet;
	@FXML
	private Button ressourceBlack;
	@FXML
	private Button ressourceWhite;
	@FXML
	private Button ressourceAll;

	@FXML
	private Button openCard1;

	@FXML
	private Button openCard2;
	@FXML
	private Button openCard3;
	@FXML
	private Button openCard4;
	@FXML
	private Button openCard5;

	@FXML
	private Button missionSurveyCloseButton;

	@FXML
	private Button leaveGameButton;

	@FXML
	private Button backToGameButton;

	@FXML
	private Button loudSpeaker;
	@FXML
	private Button rulesButton;
	@FXML
	private Button previousButton;
	@FXML
	private Button nextButton;
	@FXML
	private Button jukebox;
	@FXML
	private ImageView damePicture;
	@FXML
	private AnchorPane endGameStatistics;
	@FXML
	private AnchorPane mainMenuAnchor;
	@FXML
	private AnchorPane playerMissionSurvey;
	@FXML
	private AnchorPane lowerBorder;
	@FXML
	private AnchorPane upperBorder;
	@FXML
	private AnchorPane endGameStatisticsAnchor;
	@FXML
	private AnchorPane pieChartAnchor;
	@FXML
	private AnchorPane statisticsAnchor;
	
	private List<File> songFiles = new ArrayList<File>();
	
	@FXML
	private void showMenu() {
		mainMenuAnchor.setVisible(true);
		mainMenuAnchor.toFront();
	}

	public Button[] getRessourceButtons() {
		Button [] buttonArray = { ressourceRed, ressourceOrange,
				ressourceYellow, ressourceGreen, ressourceBlue, ressourceViolet,
				ressourceBlack, ressourceWhite, ressourceAll};
		return buttonArray;
	}
	@FXML
	private void hideMenu() {
		mainMenuAnchor.setVisible(false);
	}

	@FXML
	private void close() {
		guiGamer.setPlayerOverview();
	}

	@FXML
	private void showMissionSurvey() {

		GameState gameState = guiGamer.getGameState();
		List<Mission> missionList = gameState.getPlayerFromId(guiGamer.getPlayerID()).getMissionList();
		HashMap<Short, City> cityMap = gameState.getGameBoard().getCityMap();

		double x = 125.0;
		double y = 50.0;
		boolean newRow = true; 
		for (Mission m : missionList) {
			MissionCard missionCard = new MissionCard(m, cityMap);
			AnchorPane missionPane = missionCard.getAnchorPane();
			missionCardAnchorList.add(missionPane);
			playerMissionSurvey.getChildren().add(missionPane);
			missionPane.setLayoutX(x);
			missionPane.setLayoutY(y);
			if (newRow) {
				x += 450.0;
				newRow = false;
			} else {
				x -= 450.0;
				y += 250.0;
				newRow = true;
			}
		}
		playerMissionSurvey.toFront();
		playerMissionSurvey.setVisible(true);
		lowerBorder.toFront();
		upperBorder.toFront();
		damePicture.toFront();
	}

	@FXML
	private void closeMissionSurvey() {
		playerMissionSurvey.setVisible(false);
		for (AnchorPane m : missionCardAnchorList) {
			playerMissionSurvey.getChildren().remove(m);
		}
	}

	@FXML
	private void scrollMissionSurvey() {
		playerMissionSurvey.setOnScroll((ScrollEvent event) -> {
			if (event.getDeltaY() < 0) {
				for (AnchorPane missionPane : missionCardAnchorList) {
					missionPane.setLayoutY(missionPane.getLayoutY() + 20.0);
				}
			} else {
				for (AnchorPane missionPane : missionCardAnchorList) {
					missionPane.setLayoutY(missionPane.getLayoutY() - 20.0);
				}
			}
		});
	}

	private void clickMissionCards() {

		for (int i = 0; i < 3; i++) {

			if (returnedMissions[i]) {

				drawnMissionCardAnchorList.get(i).setEffect(new DropShadow(25, Color.GOLD));
				acceptMissionsButton.setGraphic(
						new ImageView(new Image(getClass().getResourceAsStream(("../Images/greenAcceptGreen.png")))));
			} else {
				drawnMissionCardAnchorList.get(i).setEffect(new DropShadow(25, Color.BLACK));
			}
		}
		if (returnedMissions[0] == false && returnedMissions[1] == false && returnedMissions[2] == false)
			acceptMissionsButton.setGraphic(
					new ImageView(new Image(getClass().getResourceAsStream(("../Images/greenAcceptGrey.png")))));
	
	}

	public void updateStacks(int ressource) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				ressourceCounter.setText(String.valueOf(ressource));
			}});
	}
	
	public void showDrawnMissions(Set<Mission> missions) {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				for (int b = 0; b < 3; b++) {
					returnedMissions[b] = false;
				}

				drawnMissionsList.clear();

				MissionCard drawnMissions = new MissionCard(new Mission(0, 0, (short) 0, (short) 0),
						guiGamer.getGameState().getGameBoard().getCityMap());

				drawnMissionsList.addAll(missions);

				drawnMissions.setDrawnMissionCards(drawnMissionsList); // TODO
																		// add
																		// send
																		// receive
				drawnMissionCardAnchorList = drawnMissions.getDrawnMissionCardAnchors();
				List<Button> drawnMissionCardButtons = drawnMissions.getDrawnMissionCardButtons();
				double x = 25.0;
				AnchorPane rootLayout = guiGamer.getRootLayout();
				int i = 0;
				for (AnchorPane missionCardPane : drawnMissionCardAnchorList) {
					missionCardPane = drawnMissionCardAnchorList.get(i);
					rootLayout.getChildren().add(missionCardPane);
					missionCardPane.setLayoutX(x);
					missionCardPane.setLayoutY(200.0);

					Button missionCardButton = drawnMissionCardButtons.get(i);
					missionCardPane.getChildren().add(missionCardButton);

					x += 400.0;
					i++;
				}
				drawnMissionCardButtons.get(0).addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
					returnedMissions[0] = !(returnedMissions[0]);
					clickMissionCards();
				});
				drawnMissionCardButtons.get(1).addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
					returnedMissions[1] = !(returnedMissions[1]);
					clickMissionCards();
				});
				drawnMissionCardButtons.get(2).addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
					returnedMissions[2] = !(returnedMissions[2]);
					clickMissionCards();
				});
				
				drawMissionText.setEffect((new DropShadow(2, Color.WHITE)));
				drawMissionText.toFront();
				showHideDrawnMissions.toFront();
				acceptMissionsButton.toFront();
				drawMissionText.setVisible(true);
				showHideDrawnMissions.setVisible(true);
				acceptMissionsButton.setVisible(true);
			}
		});

	}

	public void showHideDrawnMissions() {
		if (!(drawnMissionsHidden)) {
			for (AnchorPane missionPane : drawnMissionCardAnchorList) {
				missionPane.setVisible(false);
			}
			acceptMissionsButton.setVisible(false);
			drawMissionText.setVisible(false);
			showHideDrawnMissions.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(("../Images/hideDrawnMissionCardsTrans.png")))));
			showHideDrawnMissions.setEffect(new DropShadow(15, Color.GOLD));
			drawnMissionsHidden = true;
		} else {
			for (AnchorPane missionPane : drawnMissionCardAnchorList) {
				missionPane.setVisible(true);
			}
			acceptMissionsButton.setVisible(true);
			drawMissionText.setVisible(true);
			showHideDrawnMissions.setEffect(new DropShadow(0, Color.BLACK));
			showHideDrawnMissions.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(("../Images/hideDrawnMissionCards.png")))));
			drawnMissionsHidden = false;
		}
	}

	public void showDrawnRessourceCard() {
		guiGamer.takeSecretCard();
	}

	public void showDrawnRessourceCard(TrackKind tk) {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				AnchorPane ressourcePane = new AnchorPane();
				ressourcePane.getChildren().add(imageMap.get(tk));
				guiGamer.getRootLayout().getChildren().add(ressourcePane);
				ressourcePane.setLayoutX(550.0);
				ressourcePane.setLayoutY(343.0);
				ressourcePane.setEffect(new DropShadow(20, Color.WHITE));
				Timeline timeline = new Timeline(new KeyFrame(Duration.millis(3000),
						ae -> guiGamer.getRootLayout().getChildren().remove(ressourcePane)));
				timeline.play();
			}
		});

	}

	@FXML
	public void acceptDrawnMissions() {

		if (returnedMissions[0] == false && returnedMissions[1] == false && returnedMissions[2] == false)
			return;

		AnchorPane rootLayout = guiGamer.getRootLayout();
		for (AnchorPane drawnCardAnchor : drawnMissionCardAnchorList) {
			rootLayout.getChildren().remove(drawnCardAnchor);
		}
		acceptMissionsButton.setVisible(false);
		showHideDrawnMissions.setVisible(false);
		drawMissionText.setVisible(false);
		
		int rmIndex = 0;
		for (boolean b : returnedMissions) {
			if (b) {
				drawnMissionsList.remove(rmIndex);
			} else {
				rmIndex++;
			}
		}
		drawMissionText.setVisible(false);
		guiGamer.returnMissions(new HashSet<Mission>(drawnMissionsList));
	}

	/**
	 * this method highlights all buttons in the game
	 */

	@FXML
	public void highlightButton() {

		menuButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> menuButton.setEffect(new DropShadow(3, Color.GOLD)));
		menuButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> menuButton.setEffect(new DropShadow(3, Color.BLACK)));

		pokalButton.addEventHandler(MouseEvent.MOUSE_ENTERED,
				e -> pokalButton.setEffect(new DropShadow(3, Color.GOLD)));
		pokalButton.addEventHandler(MouseEvent.MOUSE_EXITED,
				e -> pokalButton.setEffect(new DropShadow(3, Color.BLACK)));

		missionDeckButton.addEventHandler(MouseEvent.MOUSE_ENTERED,
				e -> missionDeckButton.setEffect(new DropShadow(3, Color.GOLD)));
		missionDeckButton.addEventHandler(MouseEvent.MOUSE_EXITED,
				e -> missionDeckButton.setEffect(new DropShadow(3, Color.BLACK)));
		missionDeckButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> guiGamer.getMissions());
		closedDeckButton.addEventHandler(MouseEvent.MOUSE_ENTERED,
				e -> closedDeckButton.setEffect(new DropShadow(3, Color.GOLD)));
		closedDeckButton.addEventHandler(MouseEvent.MOUSE_EXITED,
				e -> closedDeckButton.setEffect(new DropShadow(3, Color.BLACK)));

		playerMissionsButton.addEventHandler(MouseEvent.MOUSE_ENTERED,
				e -> playerMissionsButton.setEffect(new DropShadow(3, Color.GOLD)));
		playerMissionsButton.addEventHandler(MouseEvent.MOUSE_EXITED,
				e -> playerMissionsButton.setEffect(new DropShadow(3, Color.BLACK)));

		openCard1.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> openCard1.setEffect(new DropShadow(3, Color.GOLD)));
		openCard1.addEventHandler(MouseEvent.MOUSE_EXITED, e -> openCard1.setEffect(new DropShadow(3, Color.BLACK)));

		openCard2.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> openCard2.setEffect(new DropShadow(3, Color.GOLD)));
		openCard2.addEventHandler(MouseEvent.MOUSE_EXITED, e -> openCard2.setEffect(new DropShadow(3, Color.BLACK)));

		openCard3.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> openCard3.setEffect(new DropShadow(3, Color.GOLD)));
		openCard3.addEventHandler(MouseEvent.MOUSE_EXITED, e -> openCard3.setEffect(new DropShadow(3, Color.BLACK)));

		openCard4.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> openCard4.setEffect(new DropShadow(3, Color.GOLD)));
		openCard4.addEventHandler(MouseEvent.MOUSE_EXITED, e -> openCard4.setEffect(new DropShadow(3, Color.BLACK)));

		openCard5.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> openCard5.setEffect(new DropShadow(3, Color.GOLD)));
		openCard5.addEventHandler(MouseEvent.MOUSE_EXITED, e -> openCard5.setEffect(new DropShadow(3, Color.BLACK)));

		leaveGameButton.addEventHandler(MouseEvent.MOUSE_ENTERED,
				e -> leaveGameButton.setEffect(new DropShadow(3, Color.GOLD)));
		leaveGameButton.addEventHandler(MouseEvent.MOUSE_EXITED,
				e -> leaveGameButton.setEffect(new DropShadow(3, Color.BLACK)));

		rulesButton.addEventHandler(MouseEvent.MOUSE_ENTERED,
				e -> rulesButton.setEffect(new DropShadow(3, Color.GOLD)));
		rulesButton.addEventHandler(MouseEvent.MOUSE_EXITED,
				e -> rulesButton.setEffect(new DropShadow(3, Color.BLACK)));

		backToGameButton.addEventHandler(MouseEvent.MOUSE_ENTERED,
				e -> backToGameButton.setEffect(new DropShadow(3, Color.GOLD)));
		backToGameButton.addEventHandler(MouseEvent.MOUSE_EXITED,
				e -> backToGameButton.setEffect(new DropShadow(3, Color.BLACK)));
		
		loudSpeaker.addEventHandler(MouseEvent.MOUSE_ENTERED,
				e -> loudSpeaker.setEffect(new DropShadow(3, Color.GOLD)));
		loudSpeaker.addEventHandler(MouseEvent.MOUSE_EXITED,
				e -> loudSpeaker.setEffect(new DropShadow(0, Color.BLACK)));
		
		jukebox.addEventHandler(MouseEvent.MOUSE_ENTERED,
				e -> jukebox.setEffect(new DropShadow(3, Color.GOLD)));
		jukebox.addEventHandler(MouseEvent.MOUSE_EXITED,
				e -> jukebox.setEffect(new DropShadow(0, Color.BLACK)));
	}

	/**
	 * this is the first method called during RootLayoutController
	 * initialization
	 */

	public void initialize() {
		openCard1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				takeCardFromOpenDeck(0);
			}
		});
		openCard2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				takeCardFromOpenDeck(1);
			}
		});
		openCard3.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				takeCardFromOpenDeck(2);
			}
		});
		openCard4.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				takeCardFromOpenDeck(3);
			}
		});
		openCard5.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				takeCardFromOpenDeck(4);
			}
		});
	}

	/**
	 * this method updates OpenDeck, you need to call it each time you receive a
	 * suitable event
	 */
	public void updateOpenDeck() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
			TrackKind[] openCards = guiGamer.getOpenCards();
			openCard1.setGraphic(new ImageView(ressourceCardMap.getButtonImages().get(openCards[0])));
			openCard2.setGraphic(new ImageView(ressourceCardMap.getButtonImages().get(openCards[1])));
			openCard3.setGraphic(new ImageView(ressourceCardMap.getButtonImages().get(openCards[2])));
			openCard4.setGraphic(new ImageView(ressourceCardMap.getButtonImages().get(openCards[3])));
			openCard5.setGraphic(new ImageView(ressourceCardMap.getButtonImages().get(openCards[4])));
			}
		});
	}

	/**
	 * this updates the Table, you need to call it each time you load the Player
	 * Table.
	 * 
	 * @param pid
	 */
	public void updateTable(int pid) {
		Player p = guiGamer.getGameState().getPlayerFromId(pid);
		if (p != null) {
			lablePlayerId.setText("" + p.getName());
			lablePlayerId.setTextFill(guiGamer.getPlayerColor().getColorFromID(pid));
			lablePoints.setText("" + p.getScore());
			lableCards.setText("" + p.getSumRessourceCards());
			lableMissions.setText("" + p.getSumMissions());
		}
	}

	public void setGamer(GUIGamer gamer) {
		this.guiGamer = gamer;
	}

	private void takeCardFromOpenDeck(int i) {
		System.out.println("RootLayoutController card taken: " + i);
		guiGamer.takeCard(i);
	}

	@FXML
	private void TakeSecretCard() {
		guiGamer.takeSecretCard();
	}

	@FXML
	private void TakeMission() {
		missionDeckButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				guiGamer.getMissions();
			}
		});
	}

	@FXML
	private void leaveGame() {
		guiGamer.leave();
	}

	@FXML
	private void showRules() {
		guiGamer.setRules();

	}

	public void updateRessourceCards() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				HashMap<TrackKind, Integer> ressourceMap = guiGamer.getGameState()
						.getPlayerFromId(guiGamer.getPlayerID()).getRessourceMap();
				ressourceRed.setText("x" + ressourceMap.get(TrackKind.RED));
				ressourceOrange.setText("x" + ressourceMap.get(TrackKind.ORANGE));
				ressourceYellow.setText("x" + ressourceMap.get(TrackKind.YELLOW));
				ressourceGreen.setText("x" + ressourceMap.get(TrackKind.GREEN));
				ressourceBlue.setText("x" + ressourceMap.get(TrackKind.BLUE));
				ressourceViolet.setText("x" + ressourceMap.get(TrackKind.VIOLET));
				ressourceWhite.setText("x" + ressourceMap.get(TrackKind.WHITE));
				ressourceBlack.setText("x" + ressourceMap.get(TrackKind.BLACK));
				ressourceAll.setText("x" + ressourceMap.get(TrackKind.ALL));
			}
		});
	}

	/**
	 * Sets the timer to 180 seconds, call this at the rootLayout initialization
	 * stage
	 */
	public void setTimer() {
		timerLabel.setText(timeSeconds.toString());
		timerLabel.setTextFill(Color.BISQUE);
		timerLabel.setStyle("-fx-font-size: 3em;");
		stext.setLayoutX(1315);
		stext.setLayoutY(40);
		stext.setStyle("-fx-font-size: 3em;");
		stext.setFill(Color.BISQUE);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
		guiGamer.getRootLayout().getChildren().add(stext);
				}
			});
		timerLabel.setLayoutX(1250);
		timerLabel.setLayoutY(2);
	}

	/**
	 * enables the timer, this method should be called at the rootLayout
	 * initialization stage don't forget to call the 'startTimer' method, since
	 * this method only enables it, but doesn't start the countdown
	 */
	public void enableTimer() {
		timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		KeyFrame updateTimer = new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				timeSeconds--;
				timerLabel.setText(timeSeconds.toString());
				if (timeSeconds <= 0) {
					timeline.stop();
				}
			}
		});
		timeline.getKeyFrames().add(updateTimer);
	}

	/**
	 * use this method and the method stopTimer() together each time, since this
	 * method only resets the timer back to 180 but doesn't stop it
	 */
	public void resetTimer() {
		timeSeconds = STARTIME;
		timerLabel.setText(timeSeconds.toString());
	}

	/**
	 * this method stops the timer, if you need to start it after it has stopped,
	 * call the method 'startTimer'
	 */
	public void stopTimer() {
		timeline.stop();
	}

	/**
	 * this method starts the timer (do not call it before the enableTimer
	 * method)
	 */
	public void startTimer() {
		timeline.playFromStart();
	}

	public void setSongList(List<File> songFiles) {
		this.songFiles = songFiles;
	}
	
	private void setPieChart() {
		ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList();
		for(Player p : guiGamer.getGameState().getPlayerSet()) {
			PieChart.Data data = new PieChart.Data(p.getName(), p.getTrackCounter());
			//data.getNode().setStyle("-fx-pie-color: " + guiGamer.getPlayerColor().getColorFromID(p.getPid()).toString() + ";");
			pieChartData.add(data);
		}
		final PieChart chart = new PieChart(pieChartData);
		pieChartAnchor.getChildren().add(chart);
		chart.setLayoutX(10.0);
		chart.setLayoutY(10.0);
		
	}
	
	public void showEndGameStats(Winner a, Player longestTrackPlayer) {
		winnerText.setText(guiGamer.getGameState().getPlayerFromId(a.pid).getName());
		playerLTText.setText(longestTrackPlayer.getName());
		scoreText.setText(Integer.toString(guiGamer.getGameState().getPlayerFromId(guiGamer.getPlayerID()).getScore()));
		setPieChart();
		endGameStatisticsAnchor.toFront();
		endGameStatisticsAnchor.setVisible(true);
	}
	
	@FXML
	private void showPieChart() {
		statisticsAnchor.setVisible(false);
		nextButton.setVisible(false);
		pieChartAnchor.setVisible(true);
		previousButton.setVisible(true);
	}
	
	@FXML
	private void showStatistics() {
		pieChartAnchor.setVisible(false);
		previousButton.setVisible(false);
		statisticsAnchor.setVisible(true);
		nextButton.setVisible(true);
	}
	
	@FXML
	public void nextSong() {
		
		guiGamer.playSound(songFiles.get(songCounter));
		if(songCounter == 8) songCounter = 0;
		else songCounter++;
	}
	
	@FXML
	private void pauseContinueMusic() {
		
		if(playing) {
			guiGamer.getMediaPlayer().setVolume(0.0);
			loudSpeaker.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(("../Images/loudSpeakerOff.png")))));
			playing = false;
		}
		else {
			guiGamer.getMediaPlayer().setVolume(1.0);
			loudSpeaker.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(("../Images/loudSpeakerOn.png")))));
			playing = true;
		}
	}
	
	public void setMainApp(GUIGamer guiMain) {
		this.guiGamer = guiMain;
	}
}
