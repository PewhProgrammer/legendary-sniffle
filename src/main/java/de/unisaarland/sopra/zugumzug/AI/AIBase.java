package de.unisaarland.sopra.zugumzug.AI;

import java.util.ArrayList;

import de.unisaarland.sopra.zugumzug.Gamer;
import de.unisaarland.sopra.zugumzug.Validator;
import de.unisaarland.sopra.zugumzug.AI.AIUtils.AITactic;
import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.client.ClientAction;
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
import de.unisaarland.sopra.zugumzug.model.GameState;

public abstract class AIBase implements Gamer, ActionServerVisitor<Void> {

	protected ArrayList<AITactic> aiList = new ArrayList<AITactic>();
	protected GameState gs;
	protected Validator val;
	protected int pid;

	public AIBase(GameState gs, int pid, Validator val) {
		this.gs = gs;
		this.val = val;
		this.pid = pid;
	}

	@Override
	public void update(ServerAction a) {
		// TODO Auto-generated method stub

	}

	@Override
	public ClientAction getInput() {
		// TODO Auto-generated method stub
		return null;
	}

	//////////////////////////////////
	// Setup Events
	//////////////////////////////////

	@Override
	public Void visit(de.unisaarland.sopra.zugumzug.actions.server.conceal.GameState a) {
		return null;
	}

	@Override
	public Void visit(GameStart a) {
		return null;
	}

	@Override
	public Void visit(SendId a) {
		return null;
	}

	//////////////////////////////////
	// Server Events
	//////////////////////////////////

	@Override
	public Void visit(ServerAction a) {
		return null;
	}

	@Override
	public Void visit(Broadcast a) {
		return null;
	}

	@Override
	public Void visit(MaxTrackLength a) {
		return null;
	}

	@Override
	public Void visit(NewCity a) {
		return null;
	}

	@Override
	public Void visit(NewOpenCard a) {
		return null;
	}

	@Override
	public Void visit(NewPlayer a) {
		return null;
	}

	@Override
	public Void visit(NewTrack a) {
		return null;
	}

	@Override
	public Void visit(PlayerLeft a) {
		return null;
	}

	@Override
	public Void visit(TookCard a) {
		return null;
	}

	@Override
	public Void visit(TookMissions a) {
		return null;
	}

	@Override
	public Void visit(TookSecretCard a) {
		return null;
	}

	@Override
	public Void visit(TrackBuilt a) {
		return null;
	}

	@Override
	public Void visit(TunnelBuilt a) {
		return null;
	}

	@Override
	public Void visit(TunnelNotBuilt a) {
		return null;
	}

	//////////////////////////////////
	// Conceal Events
	//////////////////////////////////

	@Override
	public Void visit(Conceal a) {
		return null;
	}

	@Override
	public Void visit(AdditionalCost a) {
		return null;
	}

	@Override
	public Void visit(Die a) {
		return null;
	}

	@Override
	public Void visit(Failure a) {
		return null;
	}

	@Override
	public Void visit(Mission a) {
		return null;
	}

	@Override
	public Void visit(NewCards a) {
		return null;
	}

	@Override
	public Void visit(NewMissions a) {
		return null;
	}

	//////////////////////////////////
	// Game Over Events
	//////////////////////////////////

	@Override
	public Void visit(FinalMissions a) {
		return null;
	}

	@Override
	public Void visit(FinalPoints a) {
		return null;
	}

	@Override
	public Void visit(LongestTrack a) {
		return null;
	}

	@Override
	public Void visit(Winner a) {
		return null;
	}

	@Override
	public Void visit(NewPlayersToObserver a) {
		return null;
	}

	@Override
	public Void visit(ObserverLeft a) {
		return null;
	}
}