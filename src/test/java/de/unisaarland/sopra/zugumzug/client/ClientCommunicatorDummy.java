package de.unisaarland.sopra.zugumzug.client;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import de.unisaarland.sopra.Mission;
import de.unisaarland.sopra.ServerEvent;
import de.unisaarland.sopra.TrackKind;
import de.unisaarland.sopra.zugumzug.actions.Action;
import de.unisaarland.sopra.zugumzug.actions.client.ClientAction;
import de.unisaarland.sopra.zugumzug.actions.server.ServerAction;

public class ClientCommunicatorDummy extends ClientCommunicator {
	
	final int id;
	
	List<ServerEvent> receives;
//	Iterator<ServerEvent> receivesIterator;

	public ClientCommunicatorDummy(final int id, String host, int port, List<ServerEvent> recieves ) 
										throws IOException {
		super(host, port);
		this.id = id;
		this.receives = receives;
//		this.receivesIterator = receives.iterator();
	}

	@Override
	public ServerAction receive() {
		return null;
	}
	
	@Override
	public void send(ClientAction action) {

	}
	
	@Override
	public int getOwnId() {
		return id;
	}

}
