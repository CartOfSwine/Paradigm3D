package playerMinds;

import java.awt.Color;
import action.Action;
import robits.Robit;
import robits.SensorSuite;

public class ExampleHunter implements MindTemplate{
   //Needed by interface
   private Robit robit;
   private Color color = Color.RED;
   
   //{MAX_HEALTH, MAX_ENERGY, ATTACK, DEFENCE, SPEED, EAT, SENSE, STEALTH}
   private int[] stats= {100,100,200,50,50,50,200,50};
   private final String species = "Wandering Hunter"; //or something like that. preferebly cooler
   
   public ExampleHunter(){
      //do something here... idk. 
      //i should add the option to pass in a robit index to the constructor, i think that might be usefull for making sperate robit roles.
      //well, i guess you could just gen rnd numbers but that would be pretty inconsistant. best to use reflection to get a constructor with an int. 
   }
   
   public void tick(){
         SensorSuite senses = this.robit.getSensorSuite();
         int[] energyTouchSense = senses.getEnergyTouchSense();
         boolean[] enemyTouchSense = senses.getEnemyTouchSense();
         int[] energySmellSense = senses.getEnergySmellSense();
         int[] enemySmellSense = senses.getEnemySmellSense();
         
         //look for enemies in attack range
         int dir = SensorSuite.findFirst(enemyTouchSense);      
         
         //attack anything nearby with extreme predjudice
         if (dir != -1){
            Action toDo = Action.getAttackAction(dir);
            this.robit.addAction(toDo);  
            this.robit.addAction(toDo);
            this.robit.addAction(toDo);
            this.robit.setShoutText("attacking", 10);
            return;
         }
         
         this.robit.setColor(color);
         
         //no robits adjacent. lets see if there are any big lumps of energy lying around
         dir = SensorSuite.findGreatest(energyTouchSense);
         
         //eat any nearby energy
         if(dir != -1 && energyTouchSense[dir] > 25){
            this.robit.addAction(Action.getEatAction(dir));
            return;
         }
         
         //well, no robits or energy adjacent, lets try and smell a robit to hunt
         dir = SensorSuite.findGreatest(enemySmellSense);
         
         //move toward the nearest robit
         if(dir != -1){
            this.robit.addAction(Action.getMoveAction(dir));
            return;
         }
         
         //could smell any robits, lets just go park ourselves on the nearest energy spot and wait for one
         dir = SensorSuite.findGreatest(energySmellSense);
         
         //move toward the strongest energy smell
         if(dir != -1)
            this.robit.addAction(Action.getMoveAction(dir));
   }

   public Color getColor(){return this.color;}
   public int[] getStats(){return this.stats;}
   public String getSpecies(){return this.species;}
   public void setRobit(Robit me){if(this.robit == null) this.robit = me;}
   public boolean isAlly(String speciesName) {
	   return this.species.equals(speciesName);
   }
}