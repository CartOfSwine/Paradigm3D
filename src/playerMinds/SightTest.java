package playerMinds;

import com.jme3.math.ColorRGBA;

import action.Action;
import robits.Robit;
import robits.SensorSuite;
import robits.SightTargetType;

public class SightTest implements MindTemplate{

   private Robit robit;
   private ColorRGBA color = ColorRGBA.Pink;
   
   private int[] stats= {
		   100,		//MAX_HEALTH
		   100,		//MAX_ENERGY
		   100,		//ATTACK
		   100,		//DEFENCE
		   100,		//SPEED
		   100,		//EAT
		   200,		//SENSE
		   100};	//STEALTH
   private final String species = "Default Species Name"; 
   
   public SightTest(){

   }
   
   public void tick(){
       SensorSuite s = this.robit.getSensorSuite();
       s.setSightTargetType(SightTargetType.ENERGY);
       boolean hasTarget = s.getSightHasTarget();
       
       if(hasTarget) {
    	   double angle = s.getSightAngle();
    	   int newAngle = (int) Math.floor(angle / 360.0 * 4.0);
    	   tryMove(newAngle,angle,s);
    	   
       }
         
   }
   
   private void tryMove(int dir, double angle, SensorSuite s) {
	   int prefDir = dir;
	   if (angle / 360.0 * 4.0 > dir) {
		   prefDir = dir + 1;
	   }
	   else {
		   prefDir = dir - 1;
	   }
	   if(prefDir < 0) prefDir = 3;
	   if(prefDir > 3) prefDir = 0;
	   
	   int[] obs = s.getObstructionTouchSense();
	   if(obs[dir] == 0) 
		   this.robit.addAction(Action.getMoveAction(dir));
	   else if (obs[prefDir] == 0)
		   this.robit.addAction(Action.getMoveAction(prefDir));
	   else
		   this.robit.addAction(Action.getAttackAction(dir));
	   
   }

   public ColorRGBA getColor(){
	   return color;
   }
   
   
   //Dont change these
   public int[] getStats(){return this.stats;}
   public String getSpecies(){return this.species;}
   public void setRobit(Robit me){if(this.robit == null) this.robit = me;}
}