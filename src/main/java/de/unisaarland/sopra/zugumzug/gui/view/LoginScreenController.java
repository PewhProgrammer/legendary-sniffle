package de.unisaarland.sopra.zugumzug.gui.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class LoginScreenController {

	@FXML
	private Button registerButton;
	
	@FXML
	private TextField name;
	
	private String input;
	
	public void initialize() {
	}
	
	public void changeName(){
		input = name.getText();
		System.out.println(input);
	}

	public String returnInput(){
		return input;
	}
}

