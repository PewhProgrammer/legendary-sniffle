package de.unisaarland.sopra.zugumzug.actions.server.conceal;

import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;

public class Failure extends Conceal {

	public static final int actionId = 5;
	
	public final String message;

	public Failure(int pid, String message) {
		super(pid);
		this.message = message;
	}
	
	@Override
	public <T> T accept(ActionVisitor<T> v) {
		return v.visit(this);
	}
	
	@Override
	public <T> T accept(ActionServerVisitor<T> v) {
		return v.visit(this);
	}

}
