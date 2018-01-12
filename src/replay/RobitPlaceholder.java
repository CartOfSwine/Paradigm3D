package replay;

import java.io.Serializable;
import java.util.LinkedList;

import robits.Robit;


public class RobitPlaceholder implements Serializable{
	private static final long serialVersionUID = 1L;

	private final String species;
	
	private final int MAX_HEALTH; 	//increases the upper limit for health
	private final int MAX_ENERGY; 	//increases the upper limit for energy
	private final int ATTACK;     	//increases effectivness of attack actions. increases attack efficiency somewhat
	private final int DEFENCE;    	//reduces incoming damage per action
	private final int SPEED;      	//reduces costs of movement
	private final int EAT;        	//increases energy gained from eating
	private final int SENSE;      	//increases the creature's sensory distance
	private final int STEALTH;		//passive stat, determines how easy the creature is to hear/smell to other creatures
	
	LinkedList<Tick<RobitState>> history;
	
	public RobitPlaceholder(Robit r) {
		this(	r.getMaxHealth(),
				r.getMaxEnergy(),
				r.getAttack(),
				r.getDefence(),
				r.getSpeed(),
				r.getEat(),
				r.getSense(),
				r.getStealth(),
				r.getSpecies());
	}
	
	public RobitPlaceholder(
			int health,
			int energy,
			int attack,
			int defence,
			int speed,
			int eat,
			int sense,
			int stealth,
			String species) {
		this.MAX_HEALTH = health;
		this.MAX_ENERGY = energy;
		this.ATTACK = attack;
		this.DEFENCE = defence;
		this.SPEED = speed;
		this.EAT = eat;
		this.SENSE = sense;
		this.STEALTH = stealth;
		
		this.species = species;
		history = new LinkedList<>();
	}
	
	public void recordTick(Robit r, int tickNum) {
		this.history.addLast(new Tick<>(tickNum));
		recordState(r);
	}
	
	public void recordState(Robit r) {
		RobitState s = new RobitState(r);
		this.history.getLast().recordState(s);
	}
	
	public int getMaxHealth()  	{return this.MAX_HEALTH;}
	public int getMaxEnergy()  	{return this.MAX_ENERGY;}
	public int getAttack()		{return this.ATTACK;}
	public int getDefence()    	{return this.DEFENCE;}
	public int getSpeed()		{return this.SPEED;}
	public int getEat()			{return this.EAT;}
	public int getSense()      	{return this.SENSE;}
	public int getStealth()    	{return this.STEALTH;}
	
	
	public String getSpecies() {
		return this.species;
	}
}
