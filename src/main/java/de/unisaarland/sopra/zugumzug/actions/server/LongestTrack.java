package de.unisaarland.sopra.zugumzug.actions.server;

import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;

public class LongestTrack extends Broadcast {
	
	public static final int actionId = 9;
	
	public final int pid;
	public final int length;

	public LongestTrack(int pid, int length) {
		this.pid = pid;
		this.length = length;
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
		return "LongestTrack: " + pid + " length: " + length;
	}

}
