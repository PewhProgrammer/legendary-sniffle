package de.unisaarland.sopra.zugumzug.converter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class converts our de.unisaarland.sopra.zugumzug.actions*
 * into the needed de.unisaarland.sopra.* from kommlib
 * 
 * @author umar
 *
 */
public class ActionConverter {

	/**
	 * This method is used to convert our missions to sopra library missions
	 * 
	 * @param list
	 * @return
	 */
	static public de.unisaarland.sopra.Mission[] convertOurMissions(
				Set<de.unisaarland.sopra.zugumzug.model.Mission> missions) {

		de.unisaarland.sopra.Mission[] arrayMissions = new de.unisaarland.sopra.Mission[missions.size()];
		Iterator<de.unisaarland.sopra.zugumzug.model.Mission> it = missions.iterator();
		
		for (int i = 0; it.hasNext(); i++) {
			de.unisaarland.sopra.zugumzug.model.Mission m = it.next();
			arrayMissions[i] = new de.unisaarland.sopra.Mission((short) m.getCityA(), (short) m.getCityB(),
					m.getPoints());
		}

		return arrayMissions;
	}

	/**
	 * Note: Internal missions id is set to -1 as a default value
	 * ServerGameController will reset the proper id
	 * 
	 * This method is used to convert sopra library missions to our missions
	 */
	static public Set<de.unisaarland.sopra.zugumzug.model.Mission> convertSopraMissions(
			de.unisaarland.sopra.Mission[] arrayMissions) {
		Set<de.unisaarland.sopra.zugumzug.model.Mission> missions = new HashSet<>();
		for (int i = 0; i < arrayMissions.length; i++) {
			de.unisaarland.sopra.Mission m = arrayMissions[i];
			missions.add(
					new de.unisaarland.sopra.zugumzug.model.Mission(-1, m.getPoints(), m.getStart(), m.getDestination()));
		}

		return missions;
	}
	
}
