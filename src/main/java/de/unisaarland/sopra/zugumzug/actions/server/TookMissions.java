package de.unisaarland.sopra.zugumzug.actions.server;

import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;

public class TookMissions extends Broadcast {

	public static final int actionId = 13;
	
	public final int pid;
	public final int count;

	public TookMissions(int pid, int count) {
		this.pid = pid;
		this.count = count;
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
		return "TookMissions: " + pid + " count: " + count;
	}

}
