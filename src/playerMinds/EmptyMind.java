package playerMinds;

import com.jme3.math.ColorRGBA;

import robits.Robit;

public class EmptyMind implements MindTemplate{

   private Robit robit;
   private ColorRGBA color = ColorRGBA.Green;
   
   private int[] stats= {
		   100,		//MAX_HEALTH
		   100,		//MAX_ENERGY
		   100,		//ATTACK
		   100,		//DEFENCE
		   100,		//SPEED
		   100,		//EAT
		   100,		//SENSE
		   100};	//STEALTH
   private final String species = "Default Species Name"; 
   
   public EmptyMind(){

   }
   
   public void tick(){
       
         
   }

   public ColorRGBA getColor(){
	   return color;
   }
   
   
   //Dont change these
   public int[] getStats(){return this.stats;}
   public String getSpecies(){return this.species;}
   public void setRobit(Robit me){if(this.robit == null) this.robit = me;}
}