package de.unisaarland.sopra.zugumzug.model;

public class Tupel<T1, T2> {

	public final T1 x;
	public final T2 y;

	public Tupel(T1 x, T2 y) {
		this.x = x;
		this.y = y;
	}

	public T1 getx() {
		return x;
	}

	public T2 gety() {
		return y;
	}

}
