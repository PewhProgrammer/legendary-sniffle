package de.unisaarland.sopra.zugumzug.model;

public class Mission {

	public final int id;
	private final int points;

	private final short cidA;
	private final short cidB;

	public Mission(int id, int points, short cidA, short cidB) {
		assert points > 0
				&& cidA != cidB
				&& cidA >= 0
				&& cidB >= 0;

		this.id = id;
		this.points = points;
		this.cidA = cidA;
		this.cidB = cidB;
	}

	public int getPoints() {
		return points;
	}

	public short getCityA() {
		return cidA;
	}

	public short getCityB() {
		return cidB;
	}
	
	/**
	 * returns a unique hash code independent from the id,
	 * to get the right order of missions at drawing in missionManager 
	 */
	@Override
	public int hashCode() {					
		return (cidB << 16) | cidA;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Mission))
			return false;
		
		Mission other = (Mission) obj;
		return cidA == other.cidA && cidB == other.cidB;
	}

}
