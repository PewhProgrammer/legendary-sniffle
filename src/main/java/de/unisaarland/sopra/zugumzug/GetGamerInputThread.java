package de.unisaarland.sopra.zugumzug;

import de.unisaarland.sopra.zugumzug.actions.client.ClientAction;
import de.unisaarland.sopra.zugumzug.actions.server.ServerAction;
import de.unisaarland.sopra.zugumzug.client.ClientGameController;


/**
 * This class is used for the purpose of getting user input and sending it
 * It reacts when the ClientGameController set the Flag isMyTurn to true; 
 * 
 * @author jannic
 *
 */

public class GetGamerInputThread implements Runnable {
	
	 private ClientGameController cgc;
	 private Flag isMyTurn;
	 ServerAction serverAction;
	
	public GetGamerInputThread(ClientGameController cgc, Flag flag) {
		super();
		this.cgc = cgc;
		this.isMyTurn = flag;
		
	}
	
	public synchronized void run(){
		
		while (!Thread.currentThread().isInterrupted()) { 				
			if (isMyTurn.isFlag()){
				ClientAction gamerAction = cgc.getGamerInput();
				cgc.communicator.send(gamerAction); 
				cgc.myOwnLastAction = gamerAction;
				System.out.println("sended: " +gamerAction.toString());
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
			
	}
}
