package de.unisaarland.sopra.zugumzug.actions.server;

import de.unisaarland.sopra.zugumzug.actions.Action;
import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;

public abstract class ServerAction extends Action {

	public static final int actionId = 102;
	
	@Override
	public <T> T accept(ActionVisitor<T> v) {
		return v.visit(this);
	}
	
	public abstract <T> T accept(ActionServerVisitor<T> v);
	
}
