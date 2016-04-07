package de.unisaarland.sopra.zugumzug.actions;

import de.unisaarland.sopra.zugumzug.actions.client.*;
import de.unisaarland.sopra.zugumzug.actions.server.*;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.*;

public class ActionIdHelper implements ActionVisitor<Integer> {

	//////////////////////////////////////
	//	visit methods for getting actionId
	//////////////////////////////////////
	
	@Override
	public Integer visit(BuildTrack a) {
		return BuildTrack.actionId;
	}

	@Override
	public Integer visit(ClientAction a) {
		return ClientAction.actionId;
	}

	@Override
	public Integer visit(GetMissions a) {
		return GetMissions.actionId;
	}

	@Override
	public Integer visit(Leave a) {
		return Leave.actionId;
	}

	@Override
	public Integer visit(NewObserver a) {
		return NewObserver.actionId;
	}

	@Override
	public Integer visit(PayTunnel a) {
		return PayTunnel.actionId;
	}

	@Override
	public Integer visit(Register a) {
		return Register.actionId;
	}

	@Override
	public Integer visit(ReturnMissions a) {
		return ReturnMissions.actionId;
	}

	@Override
	public Integer visit(TakeCard a) {
		return TakeCard.actionId;
	}

	@Override
	public Integer visit(TakeSecretCard a) {
		return TakeSecretCard.actionId;
	}

	@Override
	public Integer visit(Timeout a) {
		return Timeout.actionId;
	}

	@Override
	public Integer visit(Broadcast a) {
		return a.accept(this);
	}

	@Override
	public Integer visit(Conceal a) {
		return a.accept(this);
	}

	@Override
	public Integer visit(AdditionalCost a) {
		return AdditionalCost.actionId;
	}

	@Override
	public Integer visit(ServerAction a) {
		return a.accept(this);
	}

	@Override
	public Integer visit(FinalMissions a) {
		return FinalMissions.actionId;
	}

	@Override
	public Integer visit(FinalPoints a) {
		return FinalPoints.actionId;
	}

	@Override
	public Integer visit(GameStart a) {
		return GameStart.actionId;
	}

	@Override
	public Integer visit(GameState a) {
		return GameState.actionId;
	}

	@Override
	public Integer visit(LongestTrack a) {
		return LongestTrack.actionId;
	}

	@Override
	public Integer visit(MaxTrackLength a) {
		return MaxTrackLength.actionId;
	}

	@Override
	public Integer visit(NewCity a) {
		return NewCity.actionId;
	}

	@Override
	public Integer visit(NewOpenCard a) {
		return NewOpenCard.actionId;
	}

	@Override
	public Integer visit(NewPlayer a) {
		return NewPlayer.actionId;
	}

	@Override
	public Integer visit(NewTrack a) {
		return NewTrack.actionId;
	}

	@Override
	public Integer visit(PlayerLeft a) {
		return PlayerLeft.actionId;
	}

	@Override
	public Integer visit(TookCard a) {
		return TookCard.actionId;
	}

	@Override
	public Integer visit(TookMissions a) {
		return TookMissions.actionId;
	}

	@Override
	public Integer visit(TookSecretCard a) {
		return TookSecretCard.actionId;
	}

	@Override
	public Integer visit(TrackBuilt a) {
		return TrackBuilt.actionId;
	}

	@Override
	public Integer visit(TunnelBuilt a) {
		return TunnelBuilt.actionId;
	}

	@Override
	public Integer visit(TunnelNotBuilt a) {
		return TunnelNotBuilt.actionId;
	}

	@Override
	public Integer visit(Winner a) {
		return Winner.actionId;
	}

	@Override
	public Integer visit(Die a) {
		return Die.actionId;
	}

	@Override
	public Integer visit(Failure a) {
		return Failure.actionId;
	}

	@Override
	public Integer visit(Mission a) {
		return Mission.actionId;
	}

	@Override
	public Integer visit(NewCards a) {
		return NewCards.actionId;
	}

	@Override
	public Integer visit(NewMissions a) {
		return NewMissions.actionId;
	}

	@Override
	public Integer visit(SendId a) {
		return SendId.actionId;
	}
	
	@Override
	public Integer visit(ObserverLeft a){
		return ObserverLeft.actionId;
	}
	
}
