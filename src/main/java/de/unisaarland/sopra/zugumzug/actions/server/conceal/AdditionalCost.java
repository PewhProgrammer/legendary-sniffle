package de.unisaarland.sopra.zugumzug.actions.server.conceal;

import de.unisaarland.sopra.zugumzug.actions.Action;
import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionValidatorVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;
import de.unisaarland.sopra.zugumzug.actions.client.BuildTrack;

public class AdditionalCost extends Conceal {

	public static final int actionId = 24;
	
	public final int additionalCosts;
	public final BuildTrack track;

	public AdditionalCost(int pid, int additionalCosts, BuildTrack track) {
		super(pid);
		this.additionalCosts = additionalCosts;
		this.track = track;
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
	public <T> T accept(ActionServerVisitor<T> v) {
		return v.visit(this);
	}

}
