package de.unisaarland.sopra.zugumzug.actions.server.conceal;

import java.util.Set;

import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;
import de.unisaarland.sopra.zugumzug.model.Mission;

public class NewMissions extends Conceal {

	public static final int actionId = 4;
	
	public final Set<Mission> missions;

	public NewMissions(int pid, Set<Mission> missions) {
		super(pid);
		this.missions = missions;
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
