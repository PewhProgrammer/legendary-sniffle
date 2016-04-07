package de.unisaarland.sopra.zugumzug.actions;

import de.unisaarland.sopra.zugumzug.actions.client.BuildTrack;
import de.unisaarland.sopra.zugumzug.actions.client.ClientAction;
import de.unisaarland.sopra.zugumzug.actions.client.GetMissions;
import de.unisaarland.sopra.zugumzug.actions.client.Leave;
import de.unisaarland.sopra.zugumzug.actions.client.NewObserver;
import de.unisaarland.sopra.zugumzug.actions.client.PayTunnel;
import de.unisaarland.sopra.zugumzug.actions.client.Register;
import de.unisaarland.sopra.zugumzug.actions.client.ReturnMissions;
import de.unisaarland.sopra.zugumzug.actions.client.TakeCard;
import de.unisaarland.sopra.zugumzug.actions.client.TakeSecretCard;
import de.unisaarland.sopra.zugumzug.actions.client.Timeout;

public interface ActionClientVisitor<T> {

	// client
	T visit(BuildTrack a);
	T visit(ClientAction a);
	T visit(GetMissions a);
	T visit(Leave a);
	T visit(NewObserver a);
	T visit(PayTunnel a);
	T visit(Register a);
	T visit(ReturnMissions a);
	T visit(TakeCard a);
	T visit(TakeSecretCard a);
	T visit(Timeout a);
	
}
