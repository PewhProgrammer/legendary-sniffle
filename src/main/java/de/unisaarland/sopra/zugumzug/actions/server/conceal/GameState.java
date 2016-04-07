package de.unisaarland.sopra.zugumzug.actions.server.conceal;

import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.Conceal;

public class GameState extends Conceal {

	public static final int actionId = 0;
	
	public final int numPlayers;

	public GameState(int pid, int numPlayers) {
		super(pid);
		this.numPlayers = numPlayers;
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
