package de.unisaarland.sopra.zugumzug.gui.view;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class AdditionalPayment {
	
	HBox hbox = new HBox(100);
	Label text = new Label("Mit wie vielen Lokomotiven möchtest du die zus\u00e4tzlichen Kosten zahlen?");
	ChoiceBox<String> cb = new ChoiceBox<>();
	Button b1 = new Button("Bezahlen");
	
	public AdditionalPayment() {
		
		hbox.setStyle("-fx-background-color: BROWN;");
		
		cb.getItems().addAll("0", "1", "2", "3");
		cb.setValue("0");
		
		b1.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> hbox.setVisible(false));
		
		hbox.setCenterShape(true);
		
		hbox.getChildren().addAll(text, cb, b1);
		}
	
	public HBox getBox() {
		return hbox;
	}
	
	public Button getPayButton() {
		return this.b1;
	}

	public int getPayment() {
		return Integer.parseInt(cb.getSelectionModel().getSelectedItem());
	}
}