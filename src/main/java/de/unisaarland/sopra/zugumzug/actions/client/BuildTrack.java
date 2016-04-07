package de.unisaarland.sopra.zugumzug.actions.client;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.actions.Action;
import de.unisaarland.sopra.zugumzug.actions.ActionClientVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionValidatorVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;

public class BuildTrack extends ClientAction {

	public static final int actionId = 29;
	
	public final short tid;
	public final int locomotives;
	public final TrackKind kind;
	
	
	public BuildTrack(int pid, short tid, TrackKind kind, int locomotives) {
		super(pid);
		this.tid = tid;
		this.kind = kind;
		this.locomotives = locomotives;
	}
	
	@Override
	public <T> T accept(ActionVisitor<T> v) {
		return v.visit(this);
	}
	
	@Override
	public Boolean accept(ActionValidatorVisitor v, Action b) {
		return v.visit(this, b);
	}

	@Override
	public <T> T accept(ActionClientVisitor<T> v) {
		return v.visit(this);
	}
	
	@Override
	public String toString() {
		return "BuildTrack: "
					+ super.toString()
					+ " tid: " + tid
					+ " kind: " + kind
					+ " locomotives: " + locomotives;
	}

}
