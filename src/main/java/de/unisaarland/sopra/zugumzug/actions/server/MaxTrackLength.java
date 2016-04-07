package de.unisaarland.sopra.zugumzug.actions.server;

import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;

public class MaxTrackLength extends Broadcast {

	public static final int actionId = 20;
	
	public final int maxTracks;

	public MaxTrackLength(int maxTracks) {
		this.maxTracks = maxTracks;
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
		return "MaxTrackLength: " + " maxTracks: " + maxTracks;
	}
	
}
