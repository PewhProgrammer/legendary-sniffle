package de.unisaarland.sopra.zugumzug.actions;

import de.unisaarland.sopra.zugumzug.actions.client.*;
import de.unisaarland.sopra.zugumzug.actions.server.*;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.*;

public interface ActionVisitor<T> {
	
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
	
	// server
	T visit(ServerAction a);
	T visit(Broadcast a);
	T visit(FinalMissions a);
	T visit(FinalPoints a);
	T visit(GameStart a);
	T visit(GameState a);
	T visit(LongestTrack a);
	T visit(MaxTrackLength a);
	T visit(NewCity a);
	T visit(NewOpenCard a);
	T visit(NewPlayer a);
	T visit(NewTrack a);
	T visit(PlayerLeft a);
	T visit(TookCard a);
	T visit(TookMissions a);
	T visit(TookSecretCard a);
	T visit(TrackBuilt a);
	T visit(TunnelBuilt a);
	T visit(TunnelNotBuilt a);
	T visit(Winner a);
	
	// server conceal
	T visit(Conceal a);
	T visit(AdditionalCost a);
	T visit(Die a);
	T visit(Failure a);
	T visit(Mission a);
	T visit(NewCards a);
	T visit(NewMissions a);
	T visit(SendId a);
	T visit(ObserverLeft a);

}
