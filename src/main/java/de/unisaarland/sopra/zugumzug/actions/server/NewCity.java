package de.unisaarland.sopra.zugumzug.actions.server;

import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;

public class NewCity extends Broadcast {

	public static final int actionId = 22;
	
	public final short cid;
	public final String name;
	public final int x;
	public final int y;

	public NewCity(short cid, String name, int x, int y) {
		this.cid = cid;
		this.name = name;
		this.x = x;
		this.y = y;
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
		return "NewCity: "
					+ "cid: " + cid
					+ " name: " + name
					+ " x: " + x
					+ " y: " + y;
	}

}
