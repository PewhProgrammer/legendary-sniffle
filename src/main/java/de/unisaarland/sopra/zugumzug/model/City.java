package de.unisaarland.sopra.zugumzug.model;

import java.util.HashSet;
import java.util.Set;

/**
 * The class City forms the nodes of our graph structure. it contains a set of
 * tracks (edges) connected to it
 * 
 * @author juliuskilger
 * @version 1.0
 *
 */
public class City {

	private final short id;

	private final Coordinate coords;

	private final String name;

	private final Set<Track> tSet = new HashSet<Track>();

	public City(short id, int x, int y, String name) {
		this.id = id;
		this.coords = new Coordinate(x, y);
		this.name = name;
	}

	public City(short id, Coordinate coords, String name) {
		this.id = id;
		this.coords = coords;
		this.name = name;
	}

	public short getID() {
		return id;
	}

	public Coordinate getCoords() {
		return coords;
	}

	public int getX() {
		return coords.getx();
	}

	public int getY() {
		return coords.gety();
	}

	public String getName() {
		return name;
	}

	public Set<Track> getTrackSet() {
		return tSet;
	}

	public void addTrack(Track track) {
		assert track != null;
		tSet.add(track);
	}

	public Set<Track> getTrackSet(short cid) {
		Set<Track> set = new HashSet<Track>();
		for (Track t : this.getTrackSet()) {
			if (t.getCityA().equals(this) && t.getCityB().getID() == cid
					|| t.getCityA().getID() == cid && t.getCityB().equals(this))
				set.add(t);
		}
		return set;
	}

	public Set<Short> getReachable() {
		Set<Short> hSet = new HashSet<Short>();
		for (Track track : this.getTrackSet()) {
			if (track.getCityA().equals(this))
				hSet.add(track.getCityB().getID());
			else
				hSet.add(track.getCityA().getID());
		}
		return hSet;
	}

	public Set<Short> getReachable(int pid) {
		Set<Short> set = new HashSet<Short>();
		for (Track track : this.getTrackSet()) {
			if (track.getOwner() == pid)
				if (track.getCityA().equals(this))
					set.add(track.getCityB().getID());
				else
					set.add(track.getCityA().getID());
		}
		return set;
	}

	public Set<Short> getReachableBuild(int pid) {
		Set<Short> set = new HashSet<>();
		for (Track track : this.getTrackSet()) {
			if (track.getOwner() == pid || track.getOwner() == Track.NO_OWNER) {
				if (track.getCityA().equals(this))
					set.add(track.getCityB().getID());
				else
					set.add(track.getCityA().getID());
			}
		}
		return set;
	}


	@Override
	public int hashCode() {
		return id * 11 + coords.x * 7 + coords.y * 13;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof City))
			return false;
		City other = (City) obj;
		return this.id == other.id && this.name.equals(other.name) && this.tSet.equals(other.tSet)
				&& this.coords.equals(other.coords);
	}

}
