package de.unisaarland.sopra.zugumzug.client;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class ClientCommunicatorTests {

	/**
	 * Tries to register a user with null host
	 * 
	 * @throws IOException
	 */
	@Test
	public void testNullHost() {
		try {
			ClientCommunicator clientComm = new ClientCommunicator(null, 0);
		} catch (IllegalArgumentException e) {
			return;
		} catch (IOException e) {
			fail("IllegalArgumenException expected");
		}
		fail("Impossible to construct ClientCommunicator with null host name value");

	}

	/**
	 * Tries to construct a ClientCommunicator with an empty host name
	 */
	@Test
	public void testHostEmpty() {
		try {
			ClientCommunicator clientComm = new ClientCommunicator("", 0);
		} catch (IOException e) {
			return;
		}
		fail("Cannot construct ClientCommunicator with an emtpy host name");

	}

	/**
	 * Check for maximum port number
	 */
	@Test
	public void testLargePortValue() {
		try {
			ClientCommunicator clientComm = new ClientCommunicator("Server", 49152);
		} catch (IllegalArgumentException e) {
			return;
		} catch (IOException e) {
			fail("IllegalArgumentException expected");
		}
		fail("Port number is above allowed maximum of 49151");

	}

	/**
	 * Check for minimum port number
	 */
	@Test
	public void testNegativePortValue() {
		try {
			ClientCommunicator clientComm = new ClientCommunicator("Server", -1);
		} catch (IllegalArgumentException e) {
			return;
		} catch (IOException e) {
			fail("IllegalArgumentException expected");
		}
		fail("Port number is negative");

	}

}
