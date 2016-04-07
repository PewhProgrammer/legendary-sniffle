package de.unisaarland.sopra.zugumzug.actions.client;

import de.unisaarland.sopra.zugumzug.actions.Action;
import de.unisaarland.sopra.zugumzug.actions.ActionClientVisitor;

public abstract class ClientAction extends Action {
	
	public static final int actionId = 101;
	
	public final int pid;
	
	public ClientAction(int pid){
		this.pid = pid;
	}
	
	public abstract <T> T accept(ActionClientVisitor<T> v);
	
	@Override
	public String toString() {
		return "pid: " + pid;
	}

}
