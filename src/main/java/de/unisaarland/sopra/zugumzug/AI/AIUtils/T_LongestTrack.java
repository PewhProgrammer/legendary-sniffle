package de.unisaarland.sopra.zugumzug.AI.AIUtils;

import java.util.PriorityQueue;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.Validator;
import de.unisaarland.sopra.zugumzug.actions.client.BuildTrack;
import de.unisaarland.sopra.zugumzug.actions.client.ClientAction;
import de.unisaarland.sopra.zugumzug.actions.server.ServerAction;
import de.unisaarland.sopra.zugumzug.model.Player;
import de.unisaarland.sopra.zugumzug.model.GameBoard;
import de.unisaarland.sopra.zugumzug.model.GameState;
import de.unisaarland.sopra.zugumzug.model.Track;

public class T_LongestTrack implements AITactic {

	@SuppressWarnings("unused")
	private GameState gs;
	private int pid;
	private GameBoard safegb;
	private PriorityQueue<Track> Q;
	private Validator val;
	private Player p;


	public T_LongestTrack(GameState gs, int pid, Validator val) {
		this.gs = gs;
		this.pid = pid;
		this.val = val;
		this.safegb = Util.copyGB(gs.getGameBoard());
		this.Q = new PriorityQueue<Track>(new Priority(gs.getPlayerFromId(pid), gs));
		this.p = gs.getPlayerFromId(pid);
	}

	@Override
	public ClientAction getInput() {
		ClientAction command = null;
		refreshQ();

		// try to build the best Track
		// can you build a track at all?
		if (!Q.isEmpty()) {
			Track t = Q.poll();
			command = buildTrack(t, p);
		}

		// if you can build the best track, do it
		return command;
		// if you cannot build the best track, return null (command will equal
		// null in that case

	}

	@Override
	public ClientAction getSecondaryInput() {
		// try all tracks and build the best you can afford...
		ClientAction command = null;
		refreshQ();


		while (!Q.isEmpty()) {
			Track t = Q.poll();
			command = buildTrack(t, p);
			if (command != null)
				return command;
		}
		// command will be null, if this is called, which means we have a
		// problem. there currently is no backup plan for this case, since
		// secondaryinput is already a backup plan for the backup plan, I hope
		// this won't happen
		return command;
	}

	private ClientAction buildTrack(Track t, Player p) {
		// is the track colorless?
		if (t.getColor() != TrackKind.ALL) {
			// t is colored
			ClientAction command = new BuildTrack(this.pid, t.getID(), t.getColor(),
					Math.max(0, t.getCosts() - p.getRessourceMap().get(t.getColor())));
			if (val.test(pid, null, command))
				return command;
			else
				return null;
		} else {
			// t is colorless
			int max = 0;
			TrackKind tK = TrackKind.ALL;
			for (TrackKind c : TrackKind.values()) {
				if (c == TrackKind.ALL)
					continue;
				if (p.getRessourceMap().get(c) > max) {
					max = p.getRessourceMap().get(c);
					tK = c;
				}
			}
			ClientAction command = new BuildTrack(this.pid, t.getID(), tK,
					Math.max(0, t.getCosts() - p.getRessourceMap().get(tK)));
			if (val.test(pid, null, command))
				return command;
			else
				return null;
		}
	}

	@Override
	public void update(ServerAction a) {

	}

	private void refreshQ() {
		// refresh the Queue
		Q.clear();
		// add all unowned tracks, that have no parallel Track that is already
		// owned by you
		for (Track t : gs.getGameBoard().getTrackSet()) {
			if (t.getOwner() == Track.NO_OWNER) {
				if (!t.isParallelTrackOwned(pid) && t.getOwner() != pid)
					Q.add(t);
			}
		}
	}

}
