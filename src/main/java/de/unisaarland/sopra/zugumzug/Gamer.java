package de.unisaarland.sopra.zugumzug;

import de.unisaarland.sopra.zugumzug.actions.client.ClientAction;
import de.unisaarland.sopra.zugumzug.actions.server.ServerAction;
import de.unisaarland.sopra.zugumzug.model.Player;

public interface Gamer {
	
	/**
	 * 
	 * @param p
	 */
	public void updateActivePlayer(Player p);

	/**
	 * The Gamer receives new events to update his model.
	 * 
	 * @param a
	 */
	public void update(ServerAction a);
	
	/**
	 * The Gamer returns his new action.
	 * 
	 * @return
	 */
	public ClientAction getInput();

	/**
	 * 
	 * @return Name of Gamer (AI/ GUI) for ClientGameController
	 */
	public String getName();

	/**
	 * Notifies Gamer to update his state, because
	 * gameState is now properly initialized.
	 */
	public void initialize();

}
