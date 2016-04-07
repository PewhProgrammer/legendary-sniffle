package de.unisaarland.sopra.zugumzug.actions;

import de.unisaarland.sopra.zugumzug.actions.client.BuildTrack;
import de.unisaarland.sopra.zugumzug.actions.client.ClientAction;
import de.unisaarland.sopra.zugumzug.actions.client.GetMissions;
import de.unisaarland.sopra.zugumzug.actions.client.PayTunnel;
import de.unisaarland.sopra.zugumzug.actions.client.ReturnMissions;
import de.unisaarland.sopra.zugumzug.actions.client.TakeCard;
import de.unisaarland.sopra.zugumzug.actions.client.TakeSecretCard;
import de.unisaarland.sopra.zugumzug.actions.server.Broadcast;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.AdditionalCost;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.Conceal;

public interface ActionValidatorVisitor {

	Boolean visit(BuildTrack a, Action b);
	Boolean visit(ClientAction a, Action lastAction);
	Boolean visit(GetMissions a, Action lastAction);
	Boolean visit(PayTunnel a, Action lastAction);
	Boolean visit(ReturnMissions a, Action lastAction);
	Boolean visit(TakeCard a, Action lastAction);
	Boolean visit(TakeSecretCard a, Action lastAction);
	
	Boolean visit(Broadcast a, Action lastAction);
	
	Boolean visit(Conceal a, Action lastAction);
	Boolean visit(AdditionalCost a, Action lastAction);
	
}
