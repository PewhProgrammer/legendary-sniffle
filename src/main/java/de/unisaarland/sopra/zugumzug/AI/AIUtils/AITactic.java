package de.unisaarland.sopra.zugumzug.AI.AIUtils;

import de.unisaarland.sopra.zugumzug.actions.client.ClientAction;
import de.unisaarland.sopra.zugumzug.actions.server.ServerAction;

public interface AITactic {

	public ClientAction getInput();
	
	public ClientAction getSecondaryInput();
	
	public void update(ServerAction a);

}