package de.unisaarland.sopra.zugumzug.actions.server;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;

public class NewTrack extends Broadcast {

	public static final int actionId = 21;
	
	public final short tid, cityA, cityB;
	public final int length;
	public final TrackKind kind;
	public final boolean tunnel;

	public NewTrack(short tid, short city1, short city2, int length, TrackKind color, boolean tunnel) {
		this.tid = tid;
		this.cityA = city1;
		this.cityB = city2;
		this.length = length;
		this.kind = color;
		this.tunnel = tunnel;
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
		return "NewTrack: "
					+ " tid: " + tid
					+ " cityA: " + cityA
					+ " cityB: " + cityB
					+ " length: " + length
					+ " kind: " + kind
					+ " tunnel: " + tunnel;
	}
	
}
