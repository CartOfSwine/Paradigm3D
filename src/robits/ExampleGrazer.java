package robits;

import com.jme3.math.ColorRGBA;
import java.util.Random;

import action.Action;
import worldData.SensorSuite;
import worldData.SightTarget;

public class ExampleGrazer implements MindTemplate{
   //Needed by interface
   private Robit robit;
   private ColorRGBA color;
   //{MAX_HEALTH, MAX_ENERGY, ATTACK, DEFENCE, SPEED, EAT, SENSE, STEALTH}
   private int[] stats= {100,100,100,100,100,100,100,100};
   private final String species = "Wandering Grazer"; //or something like that. preferebly cooler
   
   //extras for functionality

   private int searchDir = 0;
   
   public ExampleGrazer(){
      //do something here... idk. 
      //i should add the option to pass in a robit index to the constructor, i think that might be usefull for making sperate robit roles.
      //well, i guess you could just gen rnd numbers but that would be pretty inconsistant. best to use reflection to get a constructor with an int. 
	   this.color = new ColorRGBA(0f,0f,1f,.001f);
   }
   
   public void tick(){
         //Pull sensory information from attached robit for later use
         SensorSuite senses = this.robit.getSensorSuite();
         int[] energySmellSense = senses.getEnergySmellSense();
         int[] energyTouchSense = senses.getEnergyTouchSense();
         
         //ensure that our sight sense is looking for energy targets
         if(senses.getSightTargetType() != SightTarget.ENERGY)
            senses.setSightTargetType(SightTarget.ENERGY);
         
         //check to see if we are allready touching some energy
         int dir = SensorSuite.findGreatest(energyTouchSense);
         
         //if we are allready touching energy, eat some and exit
         if(dir != -1){
            this.robit.addAction(Action.getEatAction(dir));
            return;
         }
         
         //we were not touching energy earlier, so lets see if we can smell any energy.
         //pick the most likely direction to travel by smell
         dir = SensorSuite.findGreatest(energySmellSense,1.2);
              
         //shoot, we couldnt smell any energy. well, we have a facorite direction called searchDir. lets go that way
         if (dir == -1){
            Random rnd = new Random();
              
            //lets add a 10% chance of randomly turning left or right though
            int rndNum = rnd.nextInt(100);
            if(rndNum < 5)
               searchDir++;
            else if(rndNum < 10)
               searchDir--;
            if(searchDir > 3)
               searchDir = 0;
            if(searchDir < 0)
               searchDir = 3;  
            dir = searchDir;
         }
         
         //cool, we have our heading from the earlier bit. lets move out
         this.robit.addAction(Action.getMoveAction(dir));         
   }

   public ColorRGBA getColor(){return this.color;}
   public int[] getStats(){return this.stats;}
   public String getSpecies(){return this.species;}
   public void setRobit(Robit me){if(this.robit == null) this.robit = me;}
}