package de.unisaarland.sopra.zugumzug.client;

import java.io.IOException;
import java.util.Set;

import de.unisaarland.sopra.Player;
import de.unisaarland.sopra.ServerEvent;
import de.unisaarland.sopra.zugumzug.actions.ActionClientVisitor;
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
import de.unisaarland.sopra.zugumzug.actions.server.conceal.Die;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.Failure;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.GameState;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.NewCards;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.NewMissions;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.SendId;
import de.unisaarland.sopra.zugumzug.converter.ActionConverter;

public class ClientCommunicator implements ActionClientVisitor<Void> {

	private final Player player;

	public ClientCommunicator(final String host, final int port) throws IOException {
		if (port < 0 
				|| port > 49151 
				|| host == null) {
			throw new IllegalArgumentException();
		}
		player = new Player(host, port);

	}

	public int getOwnId() {
		return player.getPlayerId();
	}

	public ServerAction receive() throws IOException {

		ServerEvent current = (ServerEvent) player.pollNextEvent();
		System.out.println("ClientComm receive: " + current);
		ServerAction receivedAction;
		switch (current.getId()) {

		case de.unisaarland.sopra.AdditionalCost.ID:
			de.unisaarland.sopra.AdditionalCost addCost = (de.unisaarland.sopra.AdditionalCost) current;
			// TODO vllt BuildTrack speichern?
			receivedAction = new AdditionalCost(player.getPlayerId(), addCost.getAdditionalCosts(), null);
			break;

		case de.unisaarland.sopra.Die.ID:
			receivedAction = new Die(player.getPlayerId(), "Game over!");
			break;

		case de.unisaarland.sopra.Failure.ID:
			receivedAction = new Failure(player.getPlayerId(), ((de.unisaarland.sopra.Failure)current).getMessage());
			break;

		case de.unisaarland.sopra.FinalMissions.ID:
			de.unisaarland.sopra.FinalMissions finalMissions = (de.unisaarland.sopra.FinalMissions) current;
			Set<de.unisaarland.sopra.zugumzug.model.Mission> fails = ActionConverter
					.convertSopraMissions(finalMissions.getMissionFail());
			Set<de.unisaarland.sopra.zugumzug.model.Mission> success = ActionConverter
					.convertSopraMissions(finalMissions.getMissionSuccess());
			receivedAction = new FinalMissions(finalMissions.getPlayerId(), fails, success);
			break;

		case de.unisaarland.sopra.FinalPoints.ID:
			de.unisaarland.sopra.FinalPoints finalPoints = (de.unisaarland.sopra.FinalPoints) current;
			receivedAction = new FinalPoints(finalPoints.getPlayerId(), finalPoints.getPoints());
			break;

		case de.unisaarland.sopra.GameStart.ID:
			receivedAction = new GameStart();
			break;

		case de.unisaarland.sopra.GameState.ID:
			receivedAction = new GameState(player.getPlayerId(),((de.unisaarland.sopra.GameState) current).getPlayers());
			break;

		case de.unisaarland.sopra.LongestTrack.ID:
			de.unisaarland.sopra.LongestTrack longestTrack = (de.unisaarland.sopra.LongestTrack) current;
			receivedAction = new LongestTrack(longestTrack.getPlayerId(), longestTrack.getLength());
			break;

		case de.unisaarland.sopra.MaxTrackLength.ID:
			receivedAction = new MaxTrackLength(((de.unisaarland.sopra.MaxTrackLength) current).getMaxTracks());
			break;

		case de.unisaarland.sopra.NewCards.ID:
			de.unisaarland.sopra.NewCards newCards = (de.unisaarland.sopra.NewCards) current;
			receivedAction = new NewCards(player.getPlayerId(), newCards.getKind(), newCards.getCount());
			break;

		case de.unisaarland.sopra.NewCity.ID:
			de.unisaarland.sopra.NewCity newCity = (de.unisaarland.sopra.NewCity) current;
			receivedAction = new NewCity(newCity.getCityId(), newCity.getName(), newCity.getX(), newCity.getY());
			break;

		case de.unisaarland.sopra.NewMissions.ID:
			de.unisaarland.sopra.NewMissions newMissions = (de.unisaarland.sopra.NewMissions) current;
			Set<de.unisaarland.sopra.zugumzug.model.Mission> missions = ActionConverter
					.convertSopraMissions(newMissions.getMissions());
			receivedAction = new NewMissions(newMissions.getId(), missions);
			break;

		case de.unisaarland.sopra.NewOpenCard.ID:
			de.unisaarland.sopra.NewOpenCard newOpenCard = (de.unisaarland.sopra.NewOpenCard) current;
			receivedAction = new NewOpenCard(newOpenCard.getKind());
			break;

		case de.unisaarland.sopra.NewPlayer.ID:
			de.unisaarland.sopra.NewPlayer newPlayer = (de.unisaarland.sopra.NewPlayer) current;
			receivedAction = new NewPlayer(newPlayer.getPlayerId(), newPlayer.getName());
			break;

		case de.unisaarland.sopra.NewTrack.ID:
			de.unisaarland.sopra.NewTrack newTrack = (de.unisaarland.sopra.NewTrack) current;
			receivedAction = new NewTrack(newTrack.getTrackId(), newTrack.getCity1(), newTrack.getCity2(),
					newTrack.getLength(), newTrack.getKind(), newTrack.getTunnel());
			break;
			
		case de.unisaarland.sopra.PlayerLeft.ID:
			de.unisaarland.sopra.PlayerLeft playerLeft = (de.unisaarland.sopra.PlayerLeft) current;
			receivedAction = new PlayerLeft(playerLeft.getPlayerId());
			break;

		case de.unisaarland.sopra.SendId.ID:
			de.unisaarland.sopra.SendId sendId = (de.unisaarland.sopra.SendId) current;
			receivedAction = new SendId(sendId.getPlayerId());
			break;

		case de.unisaarland.sopra.TookCard.ID:
			de.unisaarland.sopra.TookCard tookCard = (de.unisaarland.sopra.TookCard) current;
			receivedAction = new TookCard(tookCard.getPlayerId(), tookCard.getKind());
			break;

		case de.unisaarland.sopra.TookMissions.ID:
			de.unisaarland.sopra.TookMissions tookMissions = (de.unisaarland.sopra.TookMissions) current;
			receivedAction = new TookMissions(tookMissions.getPlayerId(), tookMissions.getCount());
			break;

		case de.unisaarland.sopra.TookSecretCard.ID:
			de.unisaarland.sopra.TookSecretCard tookSecretCard = (de.unisaarland.sopra.TookSecretCard) current;
			receivedAction = new TookSecretCard(tookSecretCard.getPlayerId());
			break;

		case de.unisaarland.sopra.TrackBuilt.ID:
			de.unisaarland.sopra.TrackBuilt trackBuilt = (de.unisaarland.sopra.TrackBuilt) current;
			receivedAction = new TrackBuilt(trackBuilt.getPlayerId(), trackBuilt.getTrackId(), trackBuilt.getKind(),
					trackBuilt.getLocomotives());
			break;

		case de.unisaarland.sopra.TunnelBuilt.ID:
			de.unisaarland.sopra.TunnelBuilt tunnelBuilt = (de.unisaarland.sopra.TunnelBuilt) current;
			receivedAction = new TunnelBuilt(tunnelBuilt.getPlayerId(), tunnelBuilt.getTrackId(),
					tunnelBuilt.getAdditionalCosts(), tunnelBuilt.getKind(), tunnelBuilt.getLocomotives());
			break;

		case de.unisaarland.sopra.TunnelNotBuilt.ID:
			de.unisaarland.sopra.TunnelNotBuilt tunnelNotBuilt = (de.unisaarland.sopra.TunnelNotBuilt) current;
			receivedAction = new TunnelNotBuilt(tunnelNotBuilt.getPlayerId(), tunnelNotBuilt.getTrackId());
			break;

		case de.unisaarland.sopra.Winner.ID:
			de.unisaarland.sopra.Winner winner = (de.unisaarland.sopra.Winner) current;
			receivedAction = new Winner(winner.getPlayerId());
			break;

		default:
			throw new IOException();
		}

		return receivedAction;
	}

	public void send(ClientAction action) {
		System.out.println("ClientComm send: " + action);
		action.accept(this);
	}

	@Override
	public Void visit(BuildTrack a) {
		try {
			player.buildTrack(a.tid, a.kind, a.locomotives);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param a
	 * @return
	 */
	@Override
	public Void visit(ClientAction a) {
		return a.accept(this); // TODO check
	}

	/**
	 * A player wants to get new missions
	 * 
	 * @param a
	 * @return
	 */
	@Override
	public Void visit(GetMissions a) {
		try {
			player.getMissions();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * A player leaves the game
	 * 
	 * @param a
	 * @return
	 */
	@Override
	public Void visit(Leave a) {
		try {
			player.close();
		} catch (Exception e) {
			System.exit(0);
		}
		return null; // TODO implement
	}

	/**
	 * Informs the server about new observer in the game. This command will be
	 * send automatically, no need to implement the method.
	 * 
	 * @param a
	 * @return
	 */
	@Override
	public Void visit(NewObserver a) {
		return null; // TODO check
	}

	/**
	 * A player informs the server, if he wants to pay the additional costs with
	 * locomotives, or normal cards of the necessary color
	 * 
	 * @param a
	 * @return
	 */
	@Override
	public Void visit(PayTunnel a) {
		try {
			player.payTunnel(a.locomotives);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Player want to change his name, or register for the first time
	 * 
	 * @param a
	 * @return
	 */
	@Override
	public Void visit(Register a) {
		try {
			player.register(a.myName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * player sends a list of missions he wants to return
	 * 
	 * @param a
	 * @return
	 */
	@Override
	public Void visit(ReturnMissions a) {
		try {
			player.returnMissions(ActionConverter.convertOurMissions(a.missions));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Pulls a card from the open deck
	 * 
	 * @param a
	 * @return
	 */
	@Override
	public Void visit(TakeCard a) {
		try {
			player.takeCard(a.kind);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Pulls a card from the closed deck
	 * 
	 * @param a
	 * @return
	 */
	@Override
	public Void visit(TakeSecretCard a) {
		try {
			player.takeSecretCard();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Sends timeout. This method is not implemented, since the communication
	 * library does it on its own.
	 * 
	 * @param a
	 * @return
	 */
	@Override
	public Void visit(Timeout a) {
		return null; // TODO check
	}

}