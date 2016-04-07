package de.unisaarland.sopra.zugumzug.actions.server;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;

public class TookCard extends Broadcast {

	public static final int actionId = 18;
	
	public final int pid;
	public final TrackKind kind;

	public TookCard(int pid, TrackKind color) {
		this.pid = pid;
		kind = color;
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
		return "TookCard: pid: " + pid + " kind: " + kind;
	}

}
