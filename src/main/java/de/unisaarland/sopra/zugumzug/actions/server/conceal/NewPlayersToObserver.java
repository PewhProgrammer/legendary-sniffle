package de.unisaarland.sopra.zugumzug.actions.server.conceal;

import de.unisaarland.sopra.zugumzug.actions.ActionServerVisitor;
import de.unisaarland.sopra.zugumzug.actions.ActionVisitor;
import de.unisaarland.sopra.zugumzug.actions.server.NewPlayer;
/**
 * This event is used to inform observer about players already present in the game
 * (this event is intern, used only in our implementation)
 * @author jannic
 *
 */
public class NewPlayersToObserver extends Conceal {

	public static final int actionId = 105;
	
	public final NewPlayer newPlayer;
	
	public NewPlayersToObserver(int pid, NewPlayer newPlayer) {
		super(pid);
		this.newPlayer = newPlayer;
	}

	@Override
	public <T> T accept(ActionVisitor<T> v) {
		return v.visit(this);
	}
	
	@Override
	public <T> T accept(ActionServerVisitor<T> v) {
		return v.visit(this);
	}
}
