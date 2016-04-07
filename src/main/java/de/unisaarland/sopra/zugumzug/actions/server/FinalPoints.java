package de.unisaarland.sopra.zugumzug.actions.server;

import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;

public class FinalPoints extends Broadcast {

	public static final int actionId = 7;
	
	public final int pid;
	public final int points;

	public FinalPoints(int pid, int p) {
		this.pid = pid;
		this.points = p;
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
		return "FinalPoints: " + pid + " points: " + points;
	}

}
