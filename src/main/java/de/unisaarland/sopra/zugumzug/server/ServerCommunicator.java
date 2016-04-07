package de.unisaarland.sopra.zugumzug.server;

import java.io.IOException;
import java.util.Set;

import de.unisaarland.sopra.ClientConnector;
import de.unisaarland.sopra.Command;
import de.unisaarland.sopra.Server;
import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.client.BuildTrack;
import de.unisaarland.sopra.zugumzug.actions.client.ClientAction;
import de.unisaarland.sopra.zugumzug.actions.client.GetMissions;
import de.unisaarland.sopra.zugumzug.actions.client.Leave;
import de.unisaarland.sopra.zugumzug.actions.client.NewObserver;
import de.unisaarland.sopra.zugumzug.actions.client.PayTunnel;
import de.unisaarland.sopra.zugumzug.actions.client.Register;
import de.unisaarland.sopra.zugumzug.actions.client.TakeCard;
import de.unisaarland.sopra.zugumzug.actions.client.TakeSecretCard;
import de.unisaarland.sopra.zugumzug.actions.client.Timeout;
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
import de.unisaarland.sopra.zugumzug.actions.server.conceal.GameState;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.Mission;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.NewCards;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.NewMissions;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.NewPlayersToObserver;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.ObserverLeft;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.SendId;
import de.unisaarland.sopra.zugumzug.converter.ActionConverter;

public class ServerCommunicator implements ActionServerVisitor<Void> {

	private final Server server;

	public ServerCommunicator(final int port) throws IOException {
		if (port < 0 || port > 49151) {
			throw new IllegalArgumentException();
		}
		server = new Server(port);
		server.start();
	}
	
	public void close() {
		try {
			server.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Receives and translates commands from clients into internal events
	 * 
	 * @return
	 */
	public ClientAction receive() {

		Command current = server.pollNextCommand();
		System.out.println("ServerComm: received: " + current);
		ClientAction receivedCommand;
		int pid = current.getPlayerId();
		
		/*
		 * To translate incoming messages, we do a case split on command IDs, we
		 * use command IDs, because it is a static final field
		 * 
		 */
		switch (current.getId()) {
			case de.unisaarland.sopra.BuildTrack.ID:
				de.unisaarland.sopra.BuildTrack buildTrack = (de.unisaarland.sopra.BuildTrack) current;
				receivedCommand = new BuildTrack(pid, buildTrack.getTrackId(), buildTrack.getKind(),
						buildTrack.getLocomotives());
				break;
	
			case de.unisaarland.sopra.GetMissions.ID:
				receivedCommand = new GetMissions(pid, null);
				break;
	
			case de.unisaarland.sopra.TakeCard.ID:
				receivedCommand = new TakeCard(pid, ((de.unisaarland.sopra.TakeCard) current).getKind());
				break;
	
			case de.unisaarland.sopra.Leave.ID:
				receivedCommand = new Leave(pid);
				break;
	
			case de.unisaarland.sopra.NewObserver.ID:
				receivedCommand = new NewObserver(pid);
				break;
	
			case de.unisaarland.sopra.PayTunnel.ID:
				de.unisaarland.sopra.PayTunnel payTunnel = (de.unisaarland.sopra.PayTunnel) current;
				receivedCommand = new PayTunnel(pid, payTunnel.getLocomotives());
				break;
	
			case de.unisaarland.sopra.Register.ID:
				de.unisaarland.sopra.Register register = (de.unisaarland.sopra.Register) current;
				receivedCommand = new Register(register.getMyName(), pid);
				break;
	
			case de.unisaarland.sopra.ReturnMissions.ID:
				de.unisaarland.sopra.ReturnMissions returnMissions = (de.unisaarland.sopra.ReturnMissions) current;
				Set<de.unisaarland.sopra.zugumzug.model.Mission> missions = 
						ActionConverter.convertSopraMissions(returnMissions.getMissions());
				receivedCommand = new de.unisaarland.sopra.zugumzug.actions.client.ReturnMissions(pid, missions);
				break;
	
			case de.unisaarland.sopra.TakeSecretCard.ID:
				receivedCommand = new TakeSecretCard(pid);
				break;
	
			case de.unisaarland.sopra.Timeout.ID:
				receivedCommand = new Timeout(pid);
				break;
	
			default:
				throw new IllegalArgumentException("ServerCommunicator no valid command");
		}

		return receivedCommand;
	}

	/**
	 * This method uses a !VISITOR! to do stuff
	 * @param action
	 * @throws IOException
	 */
	public void send(ServerAction action) throws IOException {
		System.out.println("ServerComm: send: " + action);
		action.accept(this);
	}

	/**
	 * 
	 * @param a
	 * @return
	 */
	@Override
	public Void visit(ServerAction a) {
		return a.accept(this); // TODO check
	}

	/**
	 * 
	 * @param a
	 * @return
	 */
	@Override
	public Void visit(Broadcast a) {
		return a.accept(this); // TODO check
	}

	/**
	 * Informs other players about missions of this player(a.pid) (successfully
	 * finished & failed)
	 * 
	 * @param a
	 * @return void
	 */
	@Override
	public Void visit(FinalMissions a) {

		de.unisaarland.sopra.Mission[] arrayFail = ActionConverter.convertOurMissions(a.missionFail);
		de.unisaarland.sopra.Mission[] arraySuccess = ActionConverter.convertOurMissions(a.missionSuccess);

		try {
			for (ClientConnector c : server.allClients()) {
				c.finalMissions(a.pid, arraySuccess, arrayFail);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Informs each player about the final points of this player (a.pid)
	 * 
	 * @param action
	 * @return void
	 */
	@Override
	public Void visit(FinalPoints a) {

		try {
			for (ClientConnector c : server.allClients()) {
				c.finalPoints(a.pid, a.points);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This event indicates the start of the game
	 */
	@Override
	public Void visit(GameStart a) {

		try {
			for (ClientConnector c : server.allClients()) {
				c.gameStart();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Greets a new player in the game and provides information about the number
	 * of players in the game
	 */
	@Override
	public Void visit(GameState a) {
		try {
			server.toClient(a.pid).gameState(a.numPlayers);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Server informs all players about the player, who has built with the
	 * longest track & announces the length of this track
	 */
	@Override
	public Void visit(LongestTrack a) {

		try {
			for (ClientConnector c : server.allClients()) {
				c.longestTrack(a.pid, a.length);
			}
		} catch (IOException e) {
			e.printStackTrace();

		}

		return null;
	}

	/**
	 * Informs all players of the allowed maximum track length on the map
	 */
	@Override
	public Void visit(MaxTrackLength a) {

		try {
			for (ClientConnector c : server.allClients()) {
				c.maxTrackLength(a.maxTracks);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * This event is used to send the cities on the map to players, informs ALL
	 * players about the new City on the map
	 */
	@Override
	public Void visit(NewCity a) {

		try {
			for (ClientConnector c : server.allClients()) {
				c.newCity(a.cid, a.name, a.x, a.y);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Informs all players about the new card (color) in the OpenDeck
	 */
	@Override
	public Void visit(NewOpenCard a) {

		try {
			for (ClientConnector c : server.allClients()) {
				c.newOpenCard(a.kind);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Informs all players about the appearance of a new player in the game
	 */
	@Override
	public Void visit(NewPlayer a) {

		try {
			
			for (ClientConnector c : server.allClients()) {
				c.newPlayer(a.pid, a.name);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * This informs an Observer about players joined the game already
	 * @param a
	 * @return
	 */
	public Void visit(NewPlayersToObserver a) {
		
		try {
			server.toClient(a.pid).newPlayer(a.newPlayer.pid, a.newPlayer.name);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	
	
	/**
	 * Announces a new track to all players (before calling this method, please
	 * ensure that all cities have been sent already)
	 */
	@Override
	public Void visit(NewTrack a) {

		try {
			for (ClientConnector c : server.allClients()) {
				c.newTrack(a.tid, a.cityA, a.cityB, a.length, a.kind, a.tunnel);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Notifies all players that a certain player has left the game
	 */
	@Override
	public Void visit(PlayerLeft a) {
		try {
			for (ClientConnector c : server.allClients()) {
				c.playerLeft(a.pid);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Informs all players that a player has taken a card (from the open deck)
	 */
	@Override
	public Void visit(TookCard a) {

		try {
			for (ClientConnector c : server.allClients()) {
				c.tookCard(a.pid, a.kind);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Informs all players that a player has taken a mission (this event is sent
	 * to observers & players)
	 */
	@Override
	public Void visit(TookMissions a) {

		try {
			for (ClientConnector c : server.allClients()) {
				c.tookMissions(a.pid, a.count);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Informs all players that a player has taken a secret card
	 */
	@Override
	public Void visit(TookSecretCard a) {

		try {
			for (ClientConnector c : server.allClients()) {
				c.tookSecretCard(a.pid);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Informs all players that a player has built a track
	 */
	@Override
	public Void visit(TrackBuilt a) {

		try {
			for (ClientConnector c : server.allClients()) {
				c.trackBuilt(a.pid, a.tid, a.kind, a.locomotives);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Informs all players that a player has built a tunnel
	 */
	@Override
	public Void visit(TunnelBuilt a) {

		try {
			for (ClientConnector c : server.allClients()) {
				c.tunnelBuilt(a.pid, a.tid, a.additionalCosts, a.kind, a.locomotives);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * informs all players that player has tried to built a tunnel
	 */
	@Override
	public Void visit(TunnelNotBuilt a) {
		try {
			for (ClientConnector c : server.allClients()) {
				c.tunnelNotBuilt(a.pid, a.tid);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Informs all players of the winner of the game
	 */
	@Override
	public Void visit(Winner a) {
		try {
			for (ClientConnector c : server.allClients()) {
				c.winner(a.pid);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Void visit(Conceal a) {
		return a.accept(this); // TODO check
	}

	/**
	 * Tells a player how many extra cards he has to pay to build a tunnel.
	 */
	@Override
	public Void visit(AdditionalCost a) {
		try {
			server.toClient(a.pid).additionalCost(a.additionalCosts);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Sends a bold guy with a bar code on the back of his head to kill a
	 * player.
	 * 
	 */
	@Override
	public Void visit(Die a) {
		try {
			server.toClient(a.pid).die(a.message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * If a player does a wrong move - he gets this event
	 */
	@Override
	public Void visit(Failure a) {
		try {
			server.toClient(a.pid).failure(a.message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * The server sends a new Mission to a player, this method should be used
	 * together with other methods (take mission, etc.)
	 */
	@Override
	public Void visit(Mission a) {
		try {
			server.toClient(a.pid).mission(a.start, a.dest, a.points);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Sends new cards to a player, this method is used to answer at
	 * 'takeSecretCard' event and at the beginning of the game
	 */
	@Override
	public Void visit(NewCards a) {
		try {
			server.toClient(a.pid).newCards(a.kind, a.count);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Sends new missions to a player
	 */
	@Override
	public Void visit(NewMissions a) {

		de.unisaarland.sopra.Mission[] arrayNewMissions
				= ActionConverter.convertOurMissions(a.missions);

		try {
			server.toClient(a.pid).newMissions(arrayNewMissions);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This is an internal action, which we don't need
	 */
	@Override
	public Void visit(SendId a) {
		return null;
	}

	@Override
	public Void visit(ObserverLeft a) {
		try {
			server.toClient(a.pid).playerLeft(a.pid);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
