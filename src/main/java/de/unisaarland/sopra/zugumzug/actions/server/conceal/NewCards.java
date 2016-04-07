package de.unisaarland.sopra.zugumzug.actions.server.conceal;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;

public class NewCards extends Conceal {

	public static final int actionId = 2;
	
	public final TrackKind kind;
	public final int count;

	public NewCards(int pid, TrackKind color, int count) {
		super(pid);
		this.kind = color;
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
		return "NewCards: pid: " + pid + " kind: " + kind + " count: " + count;
	}
	
}
