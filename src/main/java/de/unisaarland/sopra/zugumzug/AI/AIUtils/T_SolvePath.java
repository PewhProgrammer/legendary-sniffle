package de.unisaarland.sopra.zugumzug.AI.AIUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.Validator;
import de.unisaarland.sopra.zugumzug.actions.client.BuildTrack;
import de.unisaarland.sopra.zugumzug.actions.client.ClientAction;
import de.unisaarland.sopra.zugumzug.actions.server.ServerAction;
import de.unisaarland.sopra.zugumzug.model.Mission;
import de.unisaarland.sopra.zugumzug.model.City;
import de.unisaarland.sopra.zugumzug.model.GameState;
import de.unisaarland.sopra.zugumzug.model.Track;
import de.unisaarland.sopra.zugumzug.model.Tupel;

public class T_SolvePath implements AITactic {

	private GameState gs;
	private int pid;
	private Validator val;

	private Util util;

	private Mission mission;

	private Set<Track> path;

	public T_SolvePath(GameState gs, int pid, Validator val) {
		this.gs = gs;
		this.pid = pid;
		this.val = val;
		this.path = new HashSet<Track>();
		this.util = new Util(gs);
		this.mission = null;
	}

	@Override
	public ClientAction getInput() {
		if (mission == null)
			return null;
		refreshPath();
		// try to build any track from the path
		for (Track t : path) {
			int max = 0;
			TrackKind colour = TrackKind.BLACK;
			if (t.getColor() == TrackKind.ALL) {
				// colourless track -> get maximal value and fill it up with
				// locomotives
				for (TrackKind tK : TrackKind.values()) {
					if (tK == TrackKind.ALL)
						continue;
					if (gs.getPlayerFromId(pid).getRessourceMap().get(tK) > max) {
						max = gs.getPlayerFromId(pid).getRessourceMap().get(tK);
						colour = tK;
					}
				}
			} else {
				// coloured track -> try and build it
				colour = t.getColor();
				max = gs.getPlayerFromId(pid).getRessourceMap().get(t.getColor());
			}
			BuildTrack command = new BuildTrack(pid, t.getID(), colour, Math.max(0, t.getCosts() - max));
			if (val.test(pid, null, command))
				return command;
		}
		// there is no track to be built, we need to draw a card now
		// giving back null leads the AI to draw
		return null;
	}

	@Override
	public ClientAction getSecondaryInput() {
		// This Tactic does not have a secondary. this should not become a
		// problem.
		return null;
	}

	private void refreshPath() {
		// test if all Track in the path are still unoccupied or built by you
		for (Track t : path) {
			if (t.getOwner() == Track.NO_OWNER || t.getOwner() == pid) {
				// everything is fine
			} else {
				// the path is blocked and we need to rerun Dijkstra
				Dijkstra();
				break;
			}
		}
	}

	private void Dijkstra() {
		// run Dijkstra and write the output to List<Track> path
		if (mission == null)
			return;
		Tupel<HashMap<Short, Integer>, HashMap<Short, Short>> dPath = util.Dijkstra(mission.getCityA(), 6, pid);
		if (dPath.getx().get(mission.getCityB()) == Util.NOT_CONNECTED){
			mission = null;
			return;
		}
		short head = mission.getCityB();
		HashMap<Short, Short> path = dPath.gety();
		this.path.clear();
		// search until you reach the start
		while (head != mission.getCityA()) {
			City c = gs.getGameBoard().getCityFromId(head);
			short newhead = path.get(head);
			Set<Track> connections = c.getTrackSet(newhead);
			Track connection = null;
			int min = Integer.MAX_VALUE;
			// get the cheapest connection
			for (Track t : connections) {
				if (t.getCosts() < min) {
					min = t.getCosts();
					connection = t;
				}
			}
			// add it to the List
			this.path.add(connection);
			head = newhead;
		}
	}

	@Override
	public void update(ServerAction a) {
		if (a instanceof de.unisaarland.sopra.zugumzug.actions.server.conceal.Mission) {
			mission = (new Mission(0, ((de.unisaarland.sopra.zugumzug.actions.server.conceal.Mission) a).points,
					((de.unisaarland.sopra.zugumzug.actions.server.conceal.Mission) a).start,
					((de.unisaarland.sopra.zugumzug.actions.server.conceal.Mission) a).dest));
			Dijkstra();
		}

	}

}
