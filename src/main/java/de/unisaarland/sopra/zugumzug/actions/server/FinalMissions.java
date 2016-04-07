package de.unisaarland.sopra.zugumzug.actions.server;

import java.util.Set;

import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;
import de.unisaarland.sopra.zugumzug.model.Mission;

public class FinalMissions extends Broadcast {

	public static final int actionId = 8;
	
	public final int pid;
	public final Set<Mission> missionSuccess;
	public final Set<Mission> missionFail;

	public FinalMissions(int pid, Set<Mission> missionSuccess, Set<Mission> missionFail) {
		this.pid = pid;
		this.missionSuccess = missionSuccess;
		this.missionFail = missionFail;
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
		return "FinalMissions: " + pid
					+ " missionSuccess.size: " + missionSuccess.size()
					+ " missionFail.size: " + missionFail.size();
	}

}
