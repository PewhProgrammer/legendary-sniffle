package de.unisaarland.sopra.zugumzug.gui.view;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.AdditionalCost;
import de.unisaarland.sopra.zugumzug.gui.GUIGamer;
import de.unisaarland.sopra.zugumzug.model.City;
import de.unisaarland.sopra.zugumzug.model.Coordinate;
import de.unisaarland.sopra.zugumzug.model.GameState;
import de.unisaarland.sopra.zugumzug.model.Track;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Graph {

	Text greyTrack = new Text("Click on a card below to choose the color you want to pay with!");
	GUIGamer gg;
	Payment payment;
	AdditionalPayment addPayment;
	HashMap<Short, Button> cityButtons = new HashMap<Short, Button>();
	HashMap<Short, Button[]> tracks = new HashMap<Short, Button[]>();

	public Graph(GameState gs, Stage primaryStage, GUIGamer gg) {
		this.gg = gg;
		createCityButtons(gs, gs.getGameBoard().getNW(), gs.getGameBoard().getSE());
		for (City c : gs.getGameBoard().getCitySet()) {
			for (Track t : c.getTrackSet()) {
				createTracks(cityButtons.get(c.getID()), cityButtons.get(t.getOther(c.getID()).getID()), t);
			}
		}

		payment = new Payment(primaryStage);
		addPayment = new AdditionalPayment();
		HBox payBox = payment.getBox();
		payBox.setVisible(false);

		greyTrack.setLayoutX(10.0);
		greyTrack.setLayoutY(150.0);
		greyTrack.setFont(Font.font("Yu Mincho Regular", 30));
		greyTrack.setVisible(false);
	//	greyTrack.setStyle("-fx-background-color: BROWN;");

		gg.getRootLayout().getChildren().add(greyTrack);
	}

	public void createCityButtons(GameState gs, Coordinate nw, Coordinate se) {

		Image im = new Image(getClass().getResourceAsStream("../Images/CityDot.png"));

		Coordinate nwFest = new Coordinate(0, 110);
		Coordinate seFest = new Coordinate(1110, 623);

		int length = se.getx() - nw.getx();
		int width = se.gety() - nw.gety();
		double lenfac = length / (double) (seFest.getx() - nwFest.getx());
		double widfac = width / (double) (seFest.gety() - nwFest.gety());

		for (City c : gs.getGameBoard().getCitySet()) {

			Button b = new Button(c.getName());
			//Text name = new Text(c.getName());

			double x = (c.getX() - nw.getx()) / lenfac;
			double xlay = nwFest.getx() + x;
			b.setLayoutX(xlay);
			double y = (c.getY() - nw.gety()) / widfac;
			double ylay = nwFest.gety() + y;
			b.setLayoutY(ylay);
			b.setGraphic(new ImageView(im));
			b.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-font-weight: bold;");
			b.setDisable(true);

			cityButtons.put(c.getID(), b);
		}
	}

	public HashMap<Short, Button> getCityButtons() {
		return this.cityButtons;
	}

	private class TComp implements Comparator<Track> {

		@Override
		public int compare(Track t0, Track t1) {
			Short id = t1.getID();
			return id.compareTo(t0.getID());
		}

	}

	public void createTracks(Button cityA, Button cityB, Track t) {
		boolean parallel = false;
		if (!tracks.containsKey(t.getID())) {
			double length = (cityA.getLayoutX() - cityB.getLayoutX());
			double width = (cityA.getLayoutY() - cityB.getLayoutY());
			double angle;
			if (length == 0.0) {
				angle = 90; // horizontale Tracks
			} else
				angle = (Math.sin(width / length) * 180) / Math.PI;
			double dis = Math.sqrt((length * length) + (width * width));

			// Behandlung paralleler Tracks
			int add = 0;
			List<Track> parallelTracks = t.getParallelTracks();
			if (parallelTracks.size() >= 1) {
				parallelTracks.sort(new TComp());
				for (int i = 0; i < parallelTracks.size(); i++) {
					if (t.getID() == (parallelTracks.get(i).getID()))
						add = i * 20;
				}
				parallel = true;
			}

			// Korrektes Bild fuer Track
			Image im = getImageTrack(t);

			// CityButtons positionieren
			Button[] trackArray = new Button[t.getCosts()];
			for (int i = 1; i < t.getCosts() + 1; i++) {
				Button b = new Button();
				b.setLayoutX((cityA.getLayoutX() - length / (t.getCosts() + 1) * i) + add);
				b.setLayoutY((cityA.getLayoutY() - width / (t.getCosts() + 1) * i) + add);

				// Skalierung
				ImageView imV = new ImageView(im);
				if (parallel) {
					imV.setScaleY(0.7);
				}
				if (dis < (im.getWidth() * (t.getCosts() + 1))) {
					imV.setScaleX(dis / (im.getWidth() * (t.getCosts() + 2)));
				}
				b.setGraphic(imV);

				// Funktionalitaet des Buttons
				b.setStyle("-fx-background-color: transparent");
				b.setRotate(angle);
				b.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
					b.setEffect(new DropShadow(20, Color.BLACK));
					cityA.setEffect(new DropShadow(20, Color.DARKRED));
					cityB.setEffect(new DropShadow(20, Color.DARKRED));
				});
				b.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
					b.setEffect(null);
					cityA.setEffect(null);
					cityB.setEffect(null);
				});

				// Kaufoption hinzufuegen
				if (t.getColor() == TrackKind.ALL) {
					payGreyTrack(b, t);
				} else {
					payTrack(b, t);
				}

				trackArray[i - 1] = b;
			}
			tracks.put(t.getID(), trackArray);
		}
	}

	public void payTrack(Button b, Track t) {
		// System.out.println("payTrack wurde aufgerufen");
		b.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			showPayment(t, t.getColor());
		});

	}

	public void showPayment(Track t, TrackKind tk) {
		// System.out.println(t.getID());
		// System.out.println(t.getCityA().getName() + " " +
		// t.getCityB().getName());
		payment.getBox().setVisible(true);
		payment.getBox().toFront();
		payment.getPayButton().addEventHandler(MouseEvent.MOUSE_CLICKED, f -> {
			gg.buildTrack(t.getID(), tk, payment.getPayment());
		});
	}

	public void payGreyTrack(Button b, Track t) {

		b.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			greyTrack.setVisible(true);
			greyTrack.toFront();
		});
		b.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {

			Button[] handcards = gg.getRLC().getRessourceButtons();
			handcards[0].addEventFilter(MouseEvent.MOUSE_CLICKED, f -> {
				showPayment(t, TrackKind.RED);
				greyTrack.setVisible(false);
			});
			handcards[1].addEventFilter(MouseEvent.MOUSE_CLICKED, f -> {
				showPayment(t, TrackKind.ORANGE);
				greyTrack.setVisible(false);
			});
			handcards[2].addEventFilter(MouseEvent.MOUSE_CLICKED, f -> {
				showPayment(t, TrackKind.YELLOW);
				greyTrack.setVisible(false);
			});
			handcards[3].addEventFilter(MouseEvent.MOUSE_CLICKED, f -> {
				showPayment(t, TrackKind.GREEN);
				greyTrack.setVisible(false);
			});
			handcards[4].addEventFilter(MouseEvent.MOUSE_CLICKED, f -> {
				showPayment(t, TrackKind.BLUE);
				greyTrack.setVisible(false);
			});
			handcards[5].addEventFilter(MouseEvent.MOUSE_CLICKED, f -> {
				showPayment(t, TrackKind.VIOLET);
				greyTrack.setVisible(false);
			});
			handcards[6].addEventFilter(MouseEvent.MOUSE_CLICKED, f -> {
				showPayment(t, TrackKind.BLACK);
				greyTrack.setVisible(false);
			});
			handcards[7].addEventFilter(MouseEvent.MOUSE_CLICKED, f -> {
				showPayment(t, TrackKind.WHITE);
				greyTrack.setVisible(false);
			});
			handcards[8].addEventFilter(MouseEvent.MOUSE_CLICKED, f -> {
				showPayment(t, TrackKind.ALL);
				greyTrack.setVisible(false);
			});
		});

	}

	public HashMap<Short, Button[]> getTracks() {
		return this.tracks;
	}

	public Image getImageTrack(Track t) {
		boolean b = t.isTunnel();
		switch (t.getColor()) {

		case ALL:
			if (!b)
				return new Image(getClass().getResourceAsStream("../Images/greyTrack.jpg"));
			return new Image(getClass().getResourceAsStream("../Images/greyTrackTunnel.jpg"));
		case RED:
			if (!b)
				return new Image(getClass().getResourceAsStream("../Images/redTrack.jpg"));
			return new Image(getClass().getResourceAsStream("../Images/redTrackTunnel.jpg"));
		case BLUE:
			if (!b)
				return new Image(getClass().getResourceAsStream("../Images/blueTrack.jpg"));
			return new Image(getClass().getResourceAsStream("../Images/blueTrackTunnel.jpg"));
		case BLACK:
			if (b)
				return new Image(getClass().getResourceAsStream("../Images/blackTrackTunnel.jpg"));
			return new Image(getClass().getResourceAsStream("../Images/blackTrack.jpg"));
		case VIOLET:
			if (b)
				return new Image(getClass().getResourceAsStream("../Images/violetTrackTunnel.jpg"));
			return new Image(getClass().getResourceAsStream("../Images/violetTrack.jpg"));
		case YELLOW:
			if (!b)
				return new Image(getClass().getResourceAsStream("../Images/yellowTrack.jpg"));
			return new Image(getClass().getResourceAsStream("../Images/yellowTrackTunnel.jpg"));
		case WHITE:
			if (!b)
				return new Image(getClass().getResourceAsStream("../Images/whiteTrack.jpg"));
			return new Image(getClass().getResourceAsStream("../Images/whiteTrackTunnel.jpg"));
		case ORANGE:
			if (!b)
				return new Image(getClass().getResourceAsStream("../Images/orangeTrack.jpg"));
			return new Image(getClass().getResourceAsStream("../Images/orangeTrackTunnel.jpg"));
		case GREEN:
			if (!b)
				return new Image(getClass().getResourceAsStream("../Images/greenTrack.jpg"));
			return new Image(getClass().getResourceAsStream("../Images/greenTrackTunnel.jpg"));
		default:
			throw new IllegalArgumentException("wrong color");
		}
	}

	public void buildTrack(short tid, Color c) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Button[] buttons = tracks.get(tid);
				for (int i = 0; i < buttons.length; i++) {
					Image image = new Image(getClass().getResourceAsStream("../Images/SchieneBlack.png"));

					ImageView imV = new ImageView(image);
					buttons[i].setGraphic(imV);
					buttons[i].setScaleX(0.65);
					buttons[i].setScaleY(0.55);
					buttons[i].setStyle(null);
					buttons[i].setBackground(new Background(new BackgroundFill(c, null, null)));
				}
			}
		});
	}

	public HBox getPaymentBox() {
		return this.payment.getBox();
	}

	public Payment getPayment() {
		return this.payment;
	}

	public HBox getAdditionalPaymentBox() {
		return this.addPayment.getBox();
	}

	public AdditionalPayment getAdditionalPayment() {
		return this.addPayment;
	}

	public void buildTunnel(AdditionalCost a) {
		// zusaetzliche Kosten anzeigen
		// choicebox
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				AnchorPane rootLayout = gg.getRootLayout();
				Text addCost = new Text("You need" + " " + Integer.toString(a.additionalCosts)
						+ " additional Resourececards to buy the tunnel");

				rootLayout.getChildren().add(addCost);
				addCost.setLayoutX(20.0);
				addCost.setLayoutY(120.0);
				addCost.setFont(Font.font("Yu Mincho Regular", 30));

				HBox hbox = addPayment.getBox();
				hbox.setPrefSize(1366.0, 50.0);
				rootLayout.getChildren().add(hbox);
				hbox.setLayoutX(110.0);
				addPayment.getPayButton().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
					gg.payTunnel(addPayment.getPayment());
					addCost.setVisible(false);
				});
			}
		});
	}
}
