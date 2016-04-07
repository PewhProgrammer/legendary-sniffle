package de.unisaarland.sopra.zugumzug.AI;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.Validator;
import de.unisaarland.sopra.zugumzug.AI.AIUtils.AITactic;
import de.unisaarland.sopra.zugumzug.AI.AIUtils.T_EnemyEnd;
import de.unisaarland.sopra.zugumzug.AI.AIUtils.T_LongestTrack;
import de.unisaarland.sopra.zugumzug.AI.AIUtils.T_MyEnd;
import de.unisaarland.sopra.zugumzug.AI.AIUtils.T_SolvePath;
import de.unisaarland.sopra.zugumzug.AI.AIUtils.Util;
import de.unisaarland.sopra.zugumzug.actions.client.BuildTrack;
import de.unisaarland.sopra.zugumzug.actions.client.ClientAction;
import de.unisaarland.sopra.zugumzug.actions.client.Leave;
import de.unisaarland.sopra.zugumzug.actions.client.PayTunnel;
import de.unisaarland.sopra.zugumzug.actions.client.ReturnMissions;
import de.unisaarland.sopra.zugumzug.actions.client.TakeCard;
import de.unisaarland.sopra.zugumzug.actions.client.TakeSecretCard;
import de.unisaarland.sopra.zugumzug.actions.server.ServerAction;
import de.unisaarland.sopra.zugumzug.actions.server.TookCard;
import de.unisaarland.sopra.zugumzug.actions.server.TookSecretCard;
import de.unisaarland.sopra.zugumzug.actions.server.Winner;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.AdditionalCost;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.Failure;
import de.unisaarland.sopra.zugumzug.actions.server.conceal.NewMissions;
import de.unisaarland.sopra.zugumzug.model.GameState;
import de.unisaarland.sopra.zugumzug.model.Mission;
import de.unisaarland.sopra.zugumzug.model.Player;
import de.unisaarland.sopra.zugumzug.model.Track;
import de.unisaarland.sopra.zugumzug.model.Tupel;

public class AI_Aiden extends AIBase {

	private static final int LONGEST_TRACK = 0;
	private static final int PANIC_END = 1;
	@SuppressWarnings("unused")
	private static final int MY_END = 2;
	private static final int SOLVE_PATH = 3;

	private boolean game_over = false;

	private ClientAction lastcommand;

	private int mode;

	private static final String NAME = "Aiden";

	private Player p;
	private Util util;

	// we have to be set to active by initialize, else the gameboard is not
	// fully set up, and we can make no informed decisions
	private boolean active = false;

	Set<Mission> chooseMissions = null;
	Mission mission = null;
	AdditionalCost tunnel = null;
	boolean drawstep2 = false;

	public AI_Aiden(GameState gs, int pid, Validator val) {
		super(gs, pid, val);

		this.util = new Util(gs);
		mode = LONGEST_TRACK;
	}

	@Override
	public void update(ServerAction a) {
		if (a instanceof Winner) {
			game_over = true;
			System.out.println("ressources: " + p.getSumRessourceCards());
			for (TrackKind tK : TrackKind.values())
				System.out.println(tK.toString() + ": " + p.getRessourceMap().get(tK));
			System.out.println("points: " + p.getScore());
			for (Track t : gs.getGameBoard().getTrackSet(pid)) {
				System.out.println("track: " + t.getID() + " " + t.getCosts() + " " + t.getColor());
			}
			for (Mission m : p.getMissionList()) {
				System.out.println("mission: " + m.getPoints() + " from: "
						+ gs.getGameBoard().getCityFromId(m.getCityA()).getName() + " to: "
						+ gs.getGameBoard().getCityFromId(m.getCityB()).getName() + " "
						+ (gs.getGameBoard().isMissionAccomplished(pid, m.getCityA(), m.getCityB())?"fulfilled":"failed"));
			}

		}

		if (!active)
			return;
		// update out tactics
		for (AITactic ai : aiList)
			ai.update(a);
		// if a is newMissions, we save it
		if (a instanceof NewMissions) {
			NewMissions b = (NewMissions) a;
			this.chooseMissions = new HashSet<Mission>(b.missions);
		}

		// we update our missionsolver so that he knows what mission to solve
		if (mission != null) {
			if (!gs.getGameBoard().isMissionImpossible(mission.id, mission.getCityA(), mission.getCityB())) {
				aiList.get(SOLVE_PATH).update(new de.unisaarland.sopra.zugumzug.actions.server.conceal.Mission(pid,
						mission.getCityA(), mission.getCityB(), mission.getPoints()));
			}
		}

		if (a instanceof AdditionalCost) {
			tunnel = (AdditionalCost) a;
		}
		if (a instanceof TookSecretCard) {
			if (((TookSecretCard) a).pid == pid) {
				// toggle the drawstep
				drawstep2 = !drawstep2;
			}
		}
		if (a instanceof TookCard) {
			if (((TookCard) a).pid == pid) {
				// toggle the drawstep
				drawstep2 = !drawstep2;
			}
		}

	}

	@Override
	public ClientAction getInput() {
		if (game_over)
			return new Leave(pid);
		if (!active)
			return null;
		// if we are forced to pay a tunnel, we do this:
		if (tunnel != null)
			return paytunnel();

		// if we are forced to draw cards, we do this:
		if (drawstep2)
			return drawstep();

		// choose a mission, since new missions are available
		if (chooseMissions != null) {
			// just choose the cheapest mission for now
			return chooseMissions();
		}

		// choose the Tactic we want to use in this Situation:
		mode = LONGEST_TRACK;

		// is there a mission to be fulfilled?
		// is our current mission fulfilled?
		if (mission != null) {
			mode = SOLVE_PATH;
			if (gs.getGameBoard().isMissionAccomplished(pid, mission.getCityA(), mission.getCityB())){
				mission = null;
				mode = LONGEST_TRACK;
			} else {
				if (gs.getGameBoard().isMissionImpossible(pid, mission.getCityA(), mission.getCityB())) {
					mission = null;
					mode = LONGEST_TRACK;
				}
			}
		}

		// is my End Step coming?
		/*
		 * if (gs.getMaxTracks() - p.getTrackCounter() < 13) mode = MY_END;
		 */
		// is an enemy ending the game?
		/*
		 * for (Player p : this.gs.getPlayerSet()) { if (p.getPid() == pid)
		 * continue; int dist = gs.getMaxTracks() - p.getTrackCounter(); int
		 * handsize = p.getSumRessourceCards(); // this means, the player is
		 * dangerously close if (0 < dist - (handsize / 3) * 2 - 3) this.mode =
		 * PANIC_END; }
		 */

		// now we know what we have to do, we will ask our Tactic to do it
		ClientAction command = aiList.get(mode).getInput();



		// if our tactic does not know a good move, we draw cards
		if (command == null
				&& gs.getMaxHandSize() - p.getSumRessourceCards() > 2
				&& gs.getRessourceManager().getCurrentSizeClosedDeck() > 1)
			command = drawstep();

		// if we cannot draw cards, we ask our LT for a secondary possible
		// move
		if (command == null)
			command = aiList.get(LONGEST_TRACK).getSecondaryInput();

		// if that is not null, we return it
		lastcommand = command;
		if (command != null)
			return command;
		throw new IllegalStateException();
	}

	private ClientAction paytunnel() {

		for (int i = 0; i <= tunnel.additionalCosts; i++) {
			ClientAction command = new PayTunnel(pid, i);
			if (val.test(pid, tunnel, command)) {
				tunnel = null;
				return command;
			}
		}
		throw new IllegalStateException();
	}

	private ClientAction drawstep() {
		ClientAction command = null;
		if (!drawstep2)
			lastcommand = null;


		command = new TakeSecretCard(pid);
		if (val.test(pid, lastcommand, command)) {
			// if we can take a secret card, we will
			return command;
		} else {
			// else we will attempt to draw the open card, we already have most
			// of (no locomotive)
			for (int i = 0; i < 5; i++) {
				command = new TakeCard(pid, gs.getRessourceManager().getOpenRessourceDeck()[i]);
				if (val.test(pid, lastcommand, command))
					return command;
			}
		}

		throw new IllegalStateException();
	}

	private ClientAction chooseMissions() {
		int min = Integer.MAX_VALUE;
		Mission mis = null;

		for (Mission m : chooseMissions) {
			HashMap<Short, Integer> dist = util.Dijkstra(m.getCityA(), 6, pid).getx();
			
			if (dist == null || dist.get(m.getCityB()) == null) {
				continue;
			}
			
			int i = dist.get(m.getCityB());
			if (i < min) {
				mis = m;
				min = i;
			}
		}
		if (mis == null){
			for (Mission m : chooseMissions){
				mis = m;
			}
		}
		chooseMissions.remove(mis);
		mission = mis;
		Set<Mission> returnlist = chooseMissions;
		chooseMissions = null;
		return new ReturnMissions(pid, returnlist);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void initialize() {
		active = true;
		this.val = new Validator(gs);
		aiList.add(new T_LongestTrack(gs, pid, val));
		aiList.add(new T_EnemyEnd(gs, pid, val));
		aiList.add(new T_MyEnd());
		aiList.add(new T_SolvePath(gs, pid, val));
		this.p = gs.getPlayerFromId(pid);
		this.util = new Util(gs);

	}

	@Override
	public void updateActivePlayer(Player p) {
		// TODO Auto-generated method stub
		
	}

}
