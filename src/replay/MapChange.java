package replay;

import java.io.Serializable;
import java.util.LinkedList;

public class MapChange implements Serializable{
	private static final long serialVersionUID = 1L;
		private LinkedList<EnergyDelta> energyChanges;
		private LinkedList<ObstructionDelta> obstructionChanges;
		private int stepNum;
		
		public MapChange(LinkedList<EnergyDelta> energyChanges, LinkedList<ObstructionDelta> obstructionChanges, int stepNum) {
			this.energyChanges = energyChanges;
			this.obstructionChanges = obstructionChanges;
			this.stepNum = stepNum;
		}
		
		public LinkedList<EnergyDelta> getEnergyChanges(){
			return energyChanges;
		}
		
		public LinkedList<ObstructionDelta> getObstructionChanges(){
			return this.obstructionChanges;
		}
		
		public int getStepNum() {
			return this.stepNum;
		}
	}