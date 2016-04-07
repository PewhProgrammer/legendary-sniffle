package de.unisaarland.sopra.zugumzug.actions.server.conceal;

import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;

public class SendId extends Conceal {

	public static final int actionId = 1;
	
	public SendId(int pid) {
		super(pid);
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
