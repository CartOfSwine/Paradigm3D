package robits;

import com.jme3.math.ColorRGBA;

public interface MindTemplate{
   
   //the species' name
   public final String species = "default value";
   
   //This is a link to the Robit the mind controls. Mind is created before Robit, so thats why setRobit is there. 
   public Robit robit = null;
   
   //Robit calls this once to establish the mind's link to it's Robit
   //public void setRobit(Robit me){this.Robit = me;} <== just use that
   public void setRobit(Robit me);
   
   //this is an array of the Robit's stats. 8 elements, values of 0-200 each, must add up to 800 (or lower i guess)
   //override it in your MindTemplate implementation to specify it for your Robit
   public int[] stats = {100,100,100,100,100,100,100,100};
    
   //this is the color your Robit will have on the map. Override to change
   public ColorRGBA color = ColorRGBA.Gray;
   
   //Each Robit will have this method called once each per game tick. Put all your code in here
   //nonblocking code only for obvious reasons.
   public void tick();
   
   public ColorRGBA getColor();
   public int[] getStats();
   public String getSpecies();
}