package de.unisaarland.sopra.zugumzug.AI;

import java.util.HashSet;
import java.util.Set;

import de.unisaarland.sopra.zugumzug.Validator;
import de.unisaarland.sopra.zugumzug.actions.client.ClientAction;
import de.unisaarland.sopra.zugumzug.actions.client.Leave;
import de.unisaarland.sopra.zugumzug.actions.client.ReturnMissions;
import de.unisaarland.sopra.zugumzug.actions.client.TakeSecretCard;
import de.unisaarland.sopra.zugumzug.actions.server.ServerAction;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.NewMissions;
import de.unisaarland.sopra.zugumzug.model.GameState;
import de.unisaarland.sopra.zugumzug.model.Mission;
import de.unisaarland.sopra.zugumzug.model.Player;

public class AIDummyLeave extends AIBase {

	private int leaveCounter = 4;
	
	Set<Mission> chooseMissions = null;
	
	public AIDummyLeave(GameState gs, int pid, Validator val) {
		super(gs, pid, val);
	}

	@Override
	public String getName() {
		return "AIDummyLeave";
	}

	@Override
	public void initialize() {
	}
	
	@Override
	public void update(ServerAction a) {
		if (a instanceof NewMissions) {
			NewMissions b = (NewMissions) a;
			this.chooseMissions = new HashSet<Mission>(b.missions);
		}
	};
	
	@Override
	public ClientAction getInput() {
		
		leaveCounter--;
		if (chooseMissions != null) {
			ReturnMissions action = new ReturnMissions(pid, new HashSet<>());
			chooseMissions = null;
			return action;
		}
		if (leaveCounter == 0) {
			return new Leave(pid);
		} else {
			return new TakeSecretCard(pid);
		}
	}

	@Override
	public void updateActivePlayer(Player p) {
		// TODO Auto-generated method stub
		
	}

}
