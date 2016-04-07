package de.unisaarland.sopra.zugumzug.actions.client;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.actions.Action;
import de.unisaarland.sopra.zugumzug.actions.ActionClientVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionValidatorVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;

public class TakeCard extends ClientAction {

	public static final int actionId = 31;
	
	public TrackKind kind;

	public TakeCard(int pid, TrackKind kind) {
		super(pid);
		this.kind = kind;
	}
	
	@Override
	public <T> T accept(ActionVisitor<T> v) {
		return v.visit(this);
	}
	
	@Override
	public Boolean accept(ActionValidatorVisitor v, Action b) {
		return v.visit(this, b);
	}
	
	@Override
	public <T> T accept(ActionClientVisitor<T> v) {
		return v.visit(this);
	}
	
	@Override
	public String toString() {
		return "TakeCard: " + super.toString() + " kind: " + kind;
	}

}
