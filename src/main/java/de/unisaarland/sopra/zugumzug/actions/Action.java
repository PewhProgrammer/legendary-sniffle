package de.unisaarland.sopra.zugumzug.actions;

public abstract class Action {
	
	// All own actionIds begin with 100 to prevent confusion with
	// the other Ids taken over from kommlib
	// all actionIds are equivalent to the ids from kommlib
	// (own.BuildTrack.actionId == kommlib.BuildTrack.actionId)
	public static final int actionId = 100;
	
	// the ActionVisitor is used to get the actionId (via ActionIdHelper)
	public abstract <T> T accept(ActionVisitor<T> v);
	public abstract Boolean accept(ActionValidatorVisitor v, Action b);
	
}
