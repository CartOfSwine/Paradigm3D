package robits;

import worldData.Obstruction;

//general purpose storage tank for arbitrary game objects. this could have probably been done better
public class GameObject{
		private int energyAmmt;
		private Robit robit;
		private Obstruction obstruction;
		
		private final boolean isEnergy;
		private final boolean isRobit;
		private final boolean isObstruciton;
		
		public GameObject(int energyAmmt) {
			this.energyAmmt = energyAmmt;
			this.isEnergy = true;
			this.robit = null;
			this.isRobit = false;
			this.obstruction = null;
			this.isObstruciton = false;
		}
		
		public GameObject(Robit robit) {
			this.energyAmmt = 0;
			this.isEnergy = false;
			this.robit = robit;
			this.isRobit = true;
			this.obstruction = null;
			this.isObstruciton = false;
		}
		
		public GameObject(Obstruction obstruction) {
			this.energyAmmt = 0;
			this.isEnergy = false;
			this.robit = null;
			this.isRobit = false;
			this.obstruction = obstruction;
			this.isObstruciton = true;
		}

		public int getEnergyAmmt() {
			return energyAmmt;
		}

		public Robit getRobit() {
			return robit;
		}

		public Obstruction getObstruction() {
			return obstruction;
		}

		public boolean isEnergy() {
			return isEnergy;
		}

		public boolean isRobit() {
			return isRobit;
		}

		public boolean isObstruciton() {
			return isObstruciton;
		}
		
		
	}