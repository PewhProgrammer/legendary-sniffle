package de.unisaarland.sopra.zugumzug.gui.view;

import de.unisaarland.sopra.zugumzug.gui.GUIGamer;
import javafx.fxml.FXML;

public class RulesLayoutController {

	@FXML
	private GUIGamer guiGamer;
	

	public void setGuiGamer(GUIGamer guiGamer) {
		this.guiGamer = guiGamer;
	}
	
	@FXML
	private void switchView(){
		guiGamer.setRootLayout();
	}
}
