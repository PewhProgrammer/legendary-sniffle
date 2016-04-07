package de.unisaarland.sopra.zugumzug.actions.client;

import de.unisaarland.sopra.zugumzug.actions.Action;
import de.unisaarland.sopra.zugumzug.actions.ActionClientVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionValidatorVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;

public class PayTunnel extends ClientAction {

	public static final int actionId = 30;
	
	public final int locomotives;
	
	public PayTunnel(int pid, int locomotives) {
		super(pid);
		this.locomotives = locomotives;
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
		return "BuildTrack: " + super.toString() + " locomotives: " + locomotives;
	}
	
}
