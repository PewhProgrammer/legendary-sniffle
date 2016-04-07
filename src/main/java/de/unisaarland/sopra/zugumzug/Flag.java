package de.unisaarland.sopra.zugumzug;


/**
 * This is a simple set and get flag class
 * It is used to the running game to communicate between:
 * 1. Receive Thread
 * 2. Client Thread
 * 
 * @author jannic
 *
 */
public class Flag  {

	boolean flag;

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public Flag(boolean flag) {
		super();
		this.flag = flag;
	}
}
