package de.unisaarland.sopra.zugumzug.actions;

import de.unisaarland.sopra.zugumzug.actions.server.Broadcast;
import de.unisaarland.sopra.zugumzug.actions.server.FinalMissions;
import de.unisaarland.sopra.zugumzug.actions.server.FinalPoints;
import de.unisaarland.sopra.zugumzug.actions.server.GameStart;
import de.unisaarland.sopra.zugumzug.actions.server.LongestTrack;
import de.unisaarland.sopra.zugumzug.actions.server.MaxTrackLength;
import de.unisaarland.sopra.zugumzug.actions.server.NewCity;
import de.unisaarland.sopra.zugumzug.actions.server.NewOpenCard;
import de.unisaarland.sopra.zugumzug.actions.server.NewPlayer;
import de.unisaarland.sopra.zugumzug.actions.server.NewTrack;
import de.unisaarland.sopra.zugumzug.actions.server.PlayerLeft;
import de.unisaarland.sopra.zugumzug.actions.server.ServerAction;
import de.unisaarland.sopra.zugumzug.actions.server.TookCard;
import de.unisaarland.sopra.zugumzug.actions.server.TookMissions;
import de.unisaarland.sopra.zugumzug.actions.server.TookSecretCard;
import de.unisaarland.sopra.zugumzug.actions.server.TrackBuilt;
import de.unisaarland.sopra.zugumzug.actions.server.TunnelBuilt;
import de.unisaarland.sopra.zugumzug.actions.server.TunnelNotBuilt;
import de.unisaarland.sopra.zugumzug.actions.server.Winner;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.AdditionalCost;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.Conceal;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.Die;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.Failure;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.Mission;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.NewCards;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.NewMissions;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.NewPlayersToObserver;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.ObserverLeft;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.SendId;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.GameState;

public interface ActionServerVisitor<T> {
	
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
	T visit(NewPlayersToObserver a);
	T visit(ObserverLeft a);

}
