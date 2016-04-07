package de.unisaarland.sopra.zugumzug.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.unisaarland.sopra.TrackKind;

/**
 * The class Track forms the edges of our graph structure. It contains
 * references to both nodes (cities) it connects.
 * 
 * @author juliuskilger
 * @version 1.0
 *
 */
public class Track {

	public final static int NO_OWNER = Integer.MIN_VALUE+831;

	private final short id;

	private final City cityA;
	private final City cityB;

	private final TrackKind color;
	private final int costs;

	private int owner = NO_OWNER;

	private final boolean isTunnel;

	/**
	 * The constructor assigns values to the fields. null != cityA != cityB !=
	 * null is enforced.
	 * 
	 * @param id
	 * @param color
	 * @param cityA
	 * @param cityB
	 * @param costs
	 * @param isTunnel
	 * 
	 */
	public Track(short id, TrackKind color, City cityA, City cityB, int costs, boolean isTunnel) {
		assert costs > 0; // TODO Lookup
		assert cityA != cityB && cityA != null && cityB != null;

		this.id = id;
		this.cityA = cityA;
		this.cityB = cityB;
		this.costs = costs;
		this.isTunnel = isTunnel;
		this.color = color;
	}

	/**
	 * 
	 * @return the Track ID
	 */
	public short getID() {
		return id;
	}

	/**
	 * 
	 * @return a reference to cityA
	 */
	public City getCityA() {
		return cityA;
	}

	/**
	 * 
	 * @return a reference to cityB
	 */
	public City getCityB() {
		return cityB;
	}

	/**
	 * 
	 * @return the color-value of the track
	 */
	public TrackKind getColor() {
		return color;
	}

	/**
	 * 
	 * @return this tracks owners Player ID
	 */
	public int getOwner() {
		return owner;
	}

	/**
	 * 
	 * @return the costs of this track
	 */
	public int getCosts() {
		return costs;
	}

	/**
	 * 
	 * @return whether this track is a tunnel
	 */
	public boolean isTunnel() {
		return isTunnel;
	}

	/**
	 * 
	 * @param pid
	 *            the Player ID of the player that has to be checked
	 * @return true, if the player owns a track, parallel to this one
	 *         (reminder: players cannot own more than one parallel track)
	 */
	public boolean isParallelTrackOwned(int pid) {
		Set<Track> tSet = new HashSet<Track>(this.getParallelTracks());
		for (Track t : tSet) {
			if (t.getOwner() == pid)
				return true;
		}
		return false;
	}

	/**
	 * 
	 * @param cid
	 *            the City ID of the city you already know
	 * @return the reference to the city this track connects with, that does not
	 *         have the given City ID
	 * 
	 * @throws IllegalArgumentException,
	 *             if neither of the cities coneted to this track match the
	 *             given City ID
	 */
	public City getOther(short cid) {
		if (cid == this.cityA.getID())
			return this.cityB;
		if (cid == this.cityB.getID())
			return this.cityA;
		throw new IllegalArgumentException();
	}

	/**
	 * 
	 * @return returns a list of all parallel Tracks, including this track.
	 */
	public List<Track> getParallelTracks() {
		List<Track> tracks = new LinkedList<Track>();
		tracks.addAll(this.cityA.getTrackSet());
		// System.out.println(tracks.size() + "SET");
		for (Iterator<Track> it = tracks.iterator(); it.hasNext();) {
			Track t = it.next();
			if ((t.cityA.equals(this.cityA) || t.cityA.equals(this.cityB))
					&& (t.cityB.equals(this.cityA) || t.cityB.equals(this.cityB))) {
				// System.out.println(t.getID());
			} else {
				it.remove();
			}
		}

		return tracks;
	}

	/**
	 * 
	 * @param c
	 *            the city you already know
	 * @return the reference to the city this track connects with, that does not
	 *         have the given City ID
	 * 
	 * @throws IllegalArgumentException,
	 *             if neither of the cities coneted to this track match the
	 *             given City
	 * 
	 * @see #getOther(short cid)
	 */
	public City getOther(City c) {
		return getOther(c.getID());
	}

	/**
	 * sets the owner of the track to the given Player ID
	 * 
	 * @param pid
	 *            the Player ID of the new owner
	 * 
	 * @throws IllegalArgumentException,
	 *             if the track already has an owner
	 * 
	 */
	public void setOwner(int pid) {
		if (this.owner != Track.NO_OWNER)
			throw new IllegalArgumentException();

		this.owner = pid;
	}

	@Override
	public int hashCode() {
		return id * 3 + cityA.getID() * 7 + cityB.getID() * 11 + costs;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Track))
			return false;
		Track other = (Track) obj;
		return id == other.id && cityA.equals(other.cityA) && cityB.equals(other.cityB) && color.equals(other.color)
				&& costs == other.costs && isTunnel == other.isTunnel;
	}

	@Override
	public String toString() {
		return "Track: " + id;
	}
}
