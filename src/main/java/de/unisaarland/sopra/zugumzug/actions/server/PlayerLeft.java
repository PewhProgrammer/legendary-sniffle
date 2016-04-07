package de.unisaarland.sopra.zugumzug.actions.server;

import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;

public class PlayerLeft extends Broadcast {
	
	public static final int actionId = 11;
	
	public final int pid;
	
	public PlayerLeft(int pid) {
		this.pid = pid;
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
		return "PlayerLeft: " + pid;
	}
	
}
