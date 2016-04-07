package de.unisaarland.sopra.zugumzug.actions.client;

import java.util.Set;

import de.unisaarland.sopra.zugumzug.actions.Action;
import de.unisaarland.sopra.zugumzug.actions.ActionClientVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionValidatorVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;

import de.unisaarland.sopra.zugumzug.model.Mission;

public class GetMissions extends ClientAction {

	public static final int actionId = 27;
	
	public final Set<Mission> missions;
	
	public GetMissions(int pid, Set<Mission> missions) {
		super(pid);
		this.missions = missions;
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
		return "GetMissions: " + super.toString();
	}

}
