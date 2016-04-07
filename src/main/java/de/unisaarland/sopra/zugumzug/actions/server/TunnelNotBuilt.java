package de.unisaarland.sopra.zugumzug.actions.server;

import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;

public class TunnelNotBuilt extends Broadcast {
	
	public static final int actionId = 16;
	
	public final int pid;
	public final short tid;
	
	public TunnelNotBuilt(int pid, short tid) {
		this.pid = pid;
		this.tid = tid;
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
		return "TunnelNotBuilt: pid: " + pid + " tid: " + tid;
	}

}
