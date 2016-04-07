package de.unisaarland.sopra.zugumzug.actions.server;

import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;

public class NewPlayer extends Broadcast {
	
	public static final int actionId = 12;
	
	public final int pid;
	public final String name;

	public NewPlayer(int pid, String name) {
		this.pid = pid;
		this.name = name;
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
		return "NewPlayer: " + " pid: " + pid + "name: " + name;
	}
	
}
