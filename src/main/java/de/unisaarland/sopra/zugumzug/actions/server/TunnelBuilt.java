package de.unisaarland.sopra.zugumzug.actions.server;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;

public class TunnelBuilt extends Broadcast {
	
	public static final int actionId = 15;
	
	public final int additionalCosts;
	public final int pid, locomotives;
	public final short tid;
	public final TrackKind kind;

	public TunnelBuilt(int pid, short tid, int additionalCosts, TrackKind kind, int locomotives) {
		this.pid = pid;
		this.tid = tid;
		this.kind = kind;
		this.locomotives = locomotives;
		this.additionalCosts = additionalCosts;
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
		return "TunnelBuilt: pid: "
					+ pid + " tid: "
					+ tid + " kind: "
					+ kind + " addCosts: "
					+ additionalCosts;
	}
	
}
