package de.unisaarland.sopra.zugumzug.server;

import static org.junit.Assert.*;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class ServerCommunicatorTests {

	/**
	 * Tests if constructor works properly
	 * 
	 * @throws Exception
	 */
	@Test
	public void test() {
		ServerCommunicator serverComm;
		try {
			serverComm = new ServerCommunicator(7777);
			Assert.assertNotNull("Server is null! Error in constructor!", serverComm);
			serverComm.close();
		} catch (IOException e) {
			fail("No Exception expected!");
		}
	}

	/**
	 * Tests if given port is above allowed maximum
	 */
	@Test
	public void testWrongPortBig() {

		try {
			ServerCommunicator servComm = new ServerCommunicator(49152);
		} catch (IllegalArgumentException e) {
			return;
		} catch (IOException e) {
			fail("IllegalArgumentException expected");
		}
		fail("Port number is above allowed maximum of 49151");

	}

	/**
	 * Tests if given port is below allowed minimum
	 */
	@Test
	public void testWrongPortNegative() {

		try {
			ServerCommunicator serverComm = new ServerCommunicator(-1);
		} catch (IllegalArgumentException e) {
			return;
		} catch (IOException e) {
			fail("IllegalArgumentException expected");
		}
		fail("Port number is below allowed minimum of 0");
	}
}
