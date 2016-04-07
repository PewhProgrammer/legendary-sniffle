package de.unisaarland.sopra.zugumzug.gui.view;

import de.unisaarland.sopra.zugumzug.gui.GUIGamer;
import de.unisaarland.sopra.zugumzug.gui.model.PlayerGui;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class PlayerOverviewController {

	private GUIGamer guiMain;

	@FXML
	private TableView<PlayerGui> playerTable;

	@FXML
	private TableColumn<PlayerGui, Integer> nameColumn;

	@FXML
	private TableColumn<PlayerGui, Integer> pointsColumn;

	@FXML
	private TableColumn<PlayerGui, Integer> sumCards;

	@FXML
	private TableColumn<PlayerGui, Integer> sumMissions;

	public PlayerOverviewController() {
	}

	@FXML
	private void close() {
		guiMain.setRootLayout();
	}

	@FXML
	private void initialize() {

		nameColumn.setCellValueFactory(cellData -> cellData.getValue().getPlayerIDProperty().asObject());
		pointsColumn.setCellValueFactory(cellData -> cellData.getValue().getPointsProperty().asObject());
		sumCards.setCellValueFactory(cellData -> cellData.getValue().getSumCardsProperty().asObject());
		sumMissions.setCellValueFactory(cellData -> cellData.getValue().getSumMissionsProperty().asObject());

		nameColumn.setCellFactory(column -> {
			return new TableCell<PlayerGui, Integer>() {
				@Override
				protected void updateItem(Integer pid, boolean empty) {
					super.updateItem(pid, empty);

					if (pid == null || empty) {
						setText(null);
						setStyle("");
					} else {
						setText(guiMain.getGameState().getPlayerFromId(pid).getName());
						setTextFill(guiMain.getPlayerColor().getColorFromID(pid));
					}
				}
			};
		});

	}

	public void setMainApp(GUIGamer guiMain) {
		this.guiMain = guiMain;
		playerTable.setItems(guiMain.getPlayerData());
	}
}
