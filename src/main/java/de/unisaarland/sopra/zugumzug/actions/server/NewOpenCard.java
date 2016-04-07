package de.unisaarland.sopra.zugumzug.actions.server;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;

public class NewOpenCard extends Broadcast {

	public static final int actionId = 17;
	
	public final TrackKind kind;

	public NewOpenCard(TrackKind color) {
		kind = color;
	}
	
	@Override
	public <T> T accept(ActionVisitor<T> v) {
		return v.visit(this);
	}
	
	@Override
	public <T> T accept(ActionServerVisitor<T> v) {
		return v.visit(this);
	}
	
	@Override
	public String toString() {
		return "NewOpenCard: " + " kind: " + kind;
	}
	
}