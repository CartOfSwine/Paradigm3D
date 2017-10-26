package robits;

import worldData.Obstruction;

public enum SightTargetType{
   ALLY,       		//all creatures of the same species
   ENEMY,      		//all creatures of other species
   ROBIT,   		//all creatures in general
   ENERGY,       	//all energy sources
   OBSTRUCTION;		//all obstructions. look at the ObstructionType enum in the World class file for all types
	
	public static boolean matches(SightTargetType s, Object o) {
		if(s == SightTargetType.ALLY || s == SightTargetType.ENEMY)
			throw new IllegalArgumentException("Cannot check for ally or enemy without creature point of referance. please consider using one of the overloaded methods");
		
		switch(s) {
			case ROBIT:
				return(o instanceof Robit);
			case ENERGY:
				return(o instanceof Integer);
			case OBSTRUCTION:
				return (o instanceof Obstruction);
			default:
				throw new IllegalStateException("wat");
		}
	}
	
	public static boolean matches(SightTargetType s, Robit r1, Object o) {
		if((s != SightTargetType.ALLY || s != SightTargetType.ENEMY))
			return matches(s,o);
		
		if(o instanceof Robit) {
			Robit r2 = (Robit)o;
			if(s == SightTargetType.ALLY && r2.getSpecies().equals(r1.getSpecies()))
					return true;
			if(s == SightTargetType.ENEMY && !r2.getSpecies().equals(r1.getSpecies()))	
				return true;
				
		}
		
		return false;
	}
}