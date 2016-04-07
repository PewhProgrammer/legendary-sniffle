package de.unisaarland.sopra.zugumzug.gui.view;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Payment {

	HBox vbox = new HBox(100);
	Label text = new Label("You're now buying a track. How many locomotivecards do you want to use for payment? ");
	
	ChoiceBox<String> cb = new ChoiceBox<>();
	Button b1, b2;

	public Payment(Stage stage) {

		text.setFont(Font.font("Yu Mincho Regular", 18));
		b1 = new Button("Don't buy");
		b2 = new Button("Buy");
		
		vbox.setStyle("-fx-background-color: BROWN;");
		vbox.setLayoutY(110);
		
		cb.getItems().addAll("0", "1", "2", "3", "4", "5", "6");
		cb.setValue("0");
		vbox.setCenterShape(true);
		
		b1.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> vbox.setVisible(false));
		b2.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			vbox.setVisible(false);
		});
		
		vbox.getChildren().addAll(text, cb, b2, b1);
	
	}

	public HBox getBox() {
		return vbox;
	}
	
	public Button getPayButton() {
		return this.b2;
	}

	public int getPayment() {
		//System.out.println(cb.getSelectionModel().getSelectedItem());
		return Integer.parseInt(cb.getSelectionModel().getSelectedItem());
	}

}
