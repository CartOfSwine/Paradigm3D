package replay;

import java.io.Serializable;
import java.util.LinkedList;

public class MapChange implements Serializable{
	private static final long serialVersionUID = 1L;
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