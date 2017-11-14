package playerMinds;

import java.awt.Color;

import action.Action;
import robits.Robit;
import robits.SensorSuite;

public class TestRekr implements MindTemplate {
	//Needed by interface
   private Robit robit;

   //{MAX_HEALTH, MAX_ENERGY, ATTACK, DEFENCE, SPEED, EAT, SENSE, STEALTH}
   private int[] stats= {50,200,200,50,150,50,50,50};
   private final String species = "TestRekr"; //or something like that. preferebly cooler
   private int age = 0;

	@Override
	public void tick() {
		this.robit.setColor(Color.YELLOW);
		
		int dir = 0;
		if(age%20 == 0)dir = 1;
		
		tryMove(dir);
		
		age++;
	}

	private void tryMove(int dir) {
		SensorSuite senses = this.robit.getSensorSuite();
		int[] touch = senses.getObstructionTouchSense();
		if(touch[dir] > 0) 
			this.robit.addAction(Action.getAttackAction(dir));
		else
			this.robit.addAction(Action.getMoveAction(dir));
	}
	
	@Override
	public void setRobit(Robit me) {
		this.robit = me;
	}

	@Override
	public int[] getStats() {
		return this.stats;
	}

	@Override
	public String getSpecies() {
		return this.species;
	}
	
	@Override
	public boolean isAlly(String speciesName) {
		   return this.species.equals(speciesName);
	   }

}
