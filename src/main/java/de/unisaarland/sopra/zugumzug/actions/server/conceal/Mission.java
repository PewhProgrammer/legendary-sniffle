package de.unisaarland.sopra.zugumzug.actions.server.conceal;

import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;

public class Mission extends Conceal {

	public static final int actionId = 3;
	
	public final int points;
	public final short start, dest;

	public Mission(int pid, short start, short dest, int points) {
		super(pid);
		this.start = start;
		this.dest = dest;
		this.points = points;
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
