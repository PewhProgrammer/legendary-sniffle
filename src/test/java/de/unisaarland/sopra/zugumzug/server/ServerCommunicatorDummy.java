package de.unisaarland.sopra.zugumzug.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.internal.Message;
import de.unisaarland.sopra.zugumzug.actions.Action;
import de.unisaarland.sopra.zugumzug.actions.client.ClientAction;
import de.unisaarland.sopra.zugumzug.actions.server.ServerAction;

public class ServerCommunicatorDummy extends ServerCommunicator{
	
	final static int PORT = 7779;
	private int sendCounter = 0;
	
	List<ClientAction> receives;
	List<ServerAction> sentActions;
	Iterator<ClientAction> receivesIterator;

	public ServerCommunicatorDummy(ArrayList<ClientAction> receives) throws IOException {
		super(PORT);
		this.receives = receives;
		this.receivesIterator = receives.iterator();
		this.sentActions = new ArrayList<ServerAction>();
	}
	
	@Override
	public ClientAction receive() {
		return this.receivesIterator.next();
	}
	
	@Override
	public void send(ServerAction action) {
		sendCounter++;
		sentActions.add(action);
	}
	
	public void printSentActions() {
		for (ServerAction ca: sentActions) {
			System.out.println(ca.toString());
		}
	}

	public int getSendCounter() {
		return this.sendCounter;
	}
	
	public List<ServerAction> getSentActions() {
		return sentActions;
	}
	
}
