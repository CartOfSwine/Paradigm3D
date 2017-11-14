package replay;

import java.util.LinkedList;

public class MapChange{
		private LinkedList<EnergyDelta> energyChanges;
		private LinkedList<ObstructionDelta> obstructionChanges;
		
		public MapChange(LinkedList<EnergyDelta> energyChanges, LinkedList<ObstructionDelta> obstructionChanges) {
			this.energyChanges = energyChanges;
			this.obstructionChanges = obstructionChanges;
		}
		
		public LinkedList<EnergyDelta> getEnergyChanges(){
			return energyChanges;
		}
		
		public LinkedList<ObstructionDelta> getObstructionChanges(){
			return this.obstructionChanges;
		}
	}