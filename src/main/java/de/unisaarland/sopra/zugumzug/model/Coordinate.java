package de.unisaarland.sopra.zugumzug.model;

public class Coordinate extends Tupel<Integer, Integer> {

	public Coordinate(Integer x, Integer y) {
		super(x, y);

	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof Coordinate))
			return false;

		Coordinate other = (Coordinate) obj;

		return this.getx() == other.getx() && this.gety() == other.gety();
	}

}
