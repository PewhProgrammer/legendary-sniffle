package de.unisaarland.sopra.zugumzug.actions.server.conceal;

import de.unisaarland.sopra.zugumzug.actions.Action;
import de.unisaarland.sopra.zugumzug.actions.ActionValidatorVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;
import de.unisaarland.sopra.zugumzug.actions.server.ServerAction;

public abstract class Conceal extends ServerAction {

	public static final int actionId = 104;
	
	public final int pid;

	public Conceal(int pid) {
		this.pid = pid;
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
	public String toString() {
		return "pid: " + pid;
	}

}
