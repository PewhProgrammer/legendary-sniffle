package de.unisaarland.sopra.zugumzug.AI.AIUtils;

import java.util.HashSet;
import java.util.Set;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.Validator;
import de.unisaarland.sopra.zugumzug.actions.client.BuildTrack;
import de.unisaarland.sopra.zugumzug.actions.client.ClientAction;
import de.unisaarland.sopra.zugumzug.actions.server.ServerAction;
import de.unisaarland.sopra.zugumzug.model.GameState;
import de.unisaarland.sopra.zugumzug.model.Player;
import de.unisaarland.sopra.zugumzug.model.Track;

public class T_EnemyEnd implements AITactic {

	private GameState gs;
	private int pid;
	private Validator val;
	private Player player;

	public T_EnemyEnd(GameState gs, int pid, Validator val) {
		this.gs = gs;
		this.pid = pid;
		this.val = val;
		this.player = gs.getPlayerFromId(pid);
	}

	@Override
	public ClientAction getInput() {

		// get the maximal color you have in hand (except TK.ALL)
		int max = 0;
		for (TrackKind tK : TrackKind.values()) {
			if (tK == TrackKind.ALL)
				continue;
			if (player.getRessourceMap().get(tK) > max) {
				max = player.getRessourceMap().get(tK);
			}
		}
		int all = player.getRessourceMap().get(TrackKind.ALL);
		// the length of the track we want to build
		int length = all + max;
		// if the track is longer than the number of tracks we have left, we
		// have to reduce that numbers
		length = (length > (gs.getMaxTracks() - player.getTrackCounter()))
				? gs.getMaxTracks() - player.getTrackCounter() : length;
		// if length > 2, it is worth it to build a track.
		// try all possible lengths from length down to 2
		while (length > 2) {
			Set<TrackKind> colours = new HashSet<TrackKind>();
			// save all colors we can use for this cost!!!
			for (TrackKind tK : TrackKind.values()) {
				if (tK == TrackKind.ALL)
					continue;
				if (player.getRessourceMap().get(tK) + all >= length)
					colours.add(tK);
			}
			// go through all tracks that have the specified cost and also can
			// be paid with the colors we specified
			for (Track t : gs.getGameBoard().getTrackSet()) {
				if (t.getOwner() != Track.NO_OWNER)
					continue;
				if (t.getCosts() == length && colours.contains(t.getColor())) {
					ClientAction a = new BuildTrack(pid, t.getID(), t.getColor(),
							t.getCosts() - player.getRessourceMap().get(t.getColor()));
					if (this.val.test(pid, null, a)) {
						return a;
					}
				}
			}
			length--;
		}
		// this AI cannot help you, if you reach this spot
		return null;
	}

	@Override
	public ClientAction getSecondaryInput() {
		// this AI also has no secondary input. this will especially never happen
		return null;
	}

	@Override
	public void update(ServerAction a) {

	}

}
