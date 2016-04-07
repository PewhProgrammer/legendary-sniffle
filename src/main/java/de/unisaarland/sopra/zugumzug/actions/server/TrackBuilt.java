package de.unisaarland.sopra.zugumzug.actions.server;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;

public class TrackBuilt extends Broadcast {

	public static final int actionId = 14;
	
	public final int pid;
	public final short tid;
	public final TrackKind kind;
	public final int locomotives;

	public TrackBuilt(int pid, short tid, TrackKind color, int locomotives) {
		this.pid = pid;
		this.tid = tid;
		this.kind = color;
		this.locomotives = locomotives;
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
		return "TrackBuilt: pid: " + pid + " tid: " + tid + " kind: " + kind;
	}

}
