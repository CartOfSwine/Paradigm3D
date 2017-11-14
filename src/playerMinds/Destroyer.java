package playerMinds;

import java.awt.Color;
import java.util.Random;

import action.Action;
import robits.Robit;
import robits.SensorSuite;


public class Destroyer implements MindTemplate{
   //Needed by interface
   private Robit robit;
   private Color color = Color.GREEN;
   
   //private int oldness;
   
   //{MAX_HEALTH, MAX_ENERGY, ATTACK, DEFENCE, SPEED, EAT, SENSE, STEALTH}
   private int[] stats= {150,150,200,50,100,50,100,0};
   private final String species = "Destroyer"; 
   
   public Destroyer(){
   }
   
   public void tick(){
         //oldness++;
         
         SensorSuite senses = this.robit.getSensorSuite();
         int[] energyTouchSense = senses.getEnergyTouchSense();
         boolean[] enemyTouchSense = senses.getEnemyTouchSense();
         int[] energySmellSense = senses.getEnergySmellSense();
         int[] obstructionTouchSense = senses.getObstructionTouchSense();
         //int[] enemySmellSense = senses.getEnemySmellSense();
         
         this.robit.setColor(Color.GREEN);
         
         int attkDir = SensorSuite.findFirst(enemyTouchSense);
         if (attkDir == -1) {

        	 int foodDir = SensorSuite.findGreatest(energyTouchSense);
        	 if (foodDir == -1) {
        		 //priority 3
        		 foodDir = SensorSuite.findGreatest(energySmellSense);
        		 if(foodDir == -1) {
        			 Random rnd = new Random();
        			 int moveDir = rnd.nextInt(4);
        			 Action move = Action.getMoveAction(moveDir);
        			 this.robit.addAction(move);
        		 }
        		 else {
        			 Action move = Action.getMoveAction(foodDir);
        			 if (obstructionTouchSense[foodDir] > 0) {
        				 move = Action.getAttackAction(foodDir);
        			 }
        			
        			 this.robit.addAction(move);
        		 }
        		 
        	 }
        	 else {
            	 //priority 2        	 
	        	 Action eat = Action.getEatAction(foodDir);
	        	 this.robit.addAction(eat);
        	 }
        	 
         }
         else {
        	 //priority 1
        	 Action attk = Action.getAttackAction(attkDir);
        	 this.robit.addAction(attk);
        	 this.robit.addAction(attk);
         }
         
   }

   public Color getColor(){
	   return color;
	   
   }
   public int[] getStats(){return this.stats;}
   public String getSpecies(){return this.species;}
   public void setRobit(Robit me){if(this.robit == null) this.robit = me;}
   public boolean isAlly(String speciesName) {
	   return this.species.equals(speciesName);
   }
}