package de.unisaarland.sopra.zugumzug.actions.client;

import de.unisaarland.sopra.zugumzug.actions.Action;
import de.unisaarland.sopra.zugumzug.actions.ActionClientVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionValidatorVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;

public class Register extends ClientAction {

	public static final int actionId = 25;
	
	public final String myName;

	public Register(String myName, int pid) {
		super(pid);
		this.myName = myName;
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
		return "Register: " + super.toString() + " name: " + myName;
	}
	
}
