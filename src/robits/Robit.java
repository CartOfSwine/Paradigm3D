package robits;

import java.awt.Color;
import java.util.LinkedList;
//import java.util.concurrent.*;

import action.Action;
import action.CordModifier;
import playerMinds.MindTemplate;
import replay.RobitPlaceholder;
import worldData.World;

public class Robit{
	private final String SECURE_KEY;
	//prevents any instances of the Mind class from changing key parts of the creature

	private int xPos;
	private int yPos;

	private int health;
	private int energy;

	private boolean isDead;

	private final String species;

	//The 8 creature stats. They must all add up to 800. If they dont, and exception will occur. minimum values of 0, max of 200 each
	private final int MAX_HEALTH; 	//increases the upper limit for health
	private final int MAX_ENERGY; 	//increases the upper limit for energy
	private final int ATTACK;     	//increases effectivness of attack actions. increases attack efficiency somewhat
	private final int DEFENCE;    	//reduces incoming damage per action
	private final int SPEED;      	//reduces costs of movement
	private final int EAT;        	//increases energy gained from eating
	private final int SENSE;      	//increases the creature's sensory distance
	//maximum sensory distance = 7 + (senses-100)/10
	//sensory activity is determined with either
	//A = -(D-S)^3 * 100/(S^3) or
	//A = -D*(100/S)+100  depending on whether sensor type is set to linear or nonlinear (Allways linear in current version)
	//A = activity (0-100 min/max)
	//D = distance = sqrt(dY^2 + dX^2)         (+(stealth-100)/10 depending on the stat) 
	//S = maximum sensor distance
	private final int STEALTH;    	//passive stat, determines how easy the creature is to hear/smell to other creatures

	private int qRawIncomingDmg;  	//the ammount of queued incoming damage to be applied in the resolution phase
	private int qRawHealing;		//the ammount of queued healing to be applied in the resolution phase
	private int qAttackBuff;		//the percentage based damage beff appplied this tick
	private int qDefenceBuff;     	//the percentage based damage reduction buff applied this tick
	private int qSenseBuff;       	//the percentage based sensory range buff applied this tick
	private int qXchange;         	//the queued change in the creature's x cordinate
	private int qYchange;         	//the queued change in the creature's y cordinate
	@SuppressWarnings("unused")
	private int qEnergyChange;    	//the ammoun of energy the creatue has eaten this phanse, applied at resolution
	private Action lastActionTaken;	//the last action the creature preformed

	private World myWorld;        	//referance to the world the creature inhabits
	private int worldSize;

	private LinkedList<Action> actionQueue;

	private Color color;
	
	private String id;          	 //Any type of unique identifier for the creature

	private MindTemplate mind;       //the reference to the player-made mind object the creature uses for decision making

	private SensorSuite sensorSuite;
	
	private boolean firstStateThisTick = true;
	
	private int score = 0;


	//=============================================================================Constructors
	public Robit(RobitPlaceholder p,  int worldSize, String key) {
		this.MAX_HEALTH = p.getMaxHealth();
		this.MAX_ENERGY = p.getMaxEnergy();
		this.ATTACK = p.getAttack();
		this.DEFENCE = p.getDefence();
		this.SPEED = p.getSpeed();
		this.EAT = p.getEat();
		this.SENSE = p.getSense();
		this.STEALTH = p.getStealth();
		this.species = p.getSpecies();
		this.SECURE_KEY = key;
		this.color = Color.GRAY;
		this.worldSize = worldSize;
	}
	
	public Robit(MindTemplate mind, World myWorld, String id, int xPos, int yPos, String key){
		this.mind = mind;
		this.myWorld = myWorld;
		this.worldSize = myWorld.WORLD_SIZE;

		this.color = Color.GRAY;
		
		if (mind.getSpecies() == null)
			this.species = MindTemplate.species; //get the species value from the interface if they havent provided one
		else
			this.species = mind.getSpecies(); 

		this.sensorSuite = new SensorSuite();

		int[] stats = mind.getStats();
		MAX_HEALTH = stats[0];
		MAX_ENERGY = stats[1];
		ATTACK = stats[2];
		DEFENCE = stats[3];
		SPEED = stats[4];
		EAT = stats[5];
		SENSE = stats[6];
		STEALTH = stats[7];

		this.health = MAX_HEALTH;

		if(MAX_ENERGY < 100)
			this.energy = MAX_ENERGY;
		else
			this.energy = 100;

		this.actionQueue = new LinkedList<>();

		this.xPos = xPos;
		this.yPos = yPos;

		this.isDead = false;
		
		this.SECURE_KEY = key;
	}

	//=============================================================================Utilities
	//-----------------------------------------------------------------addAction
	public int addAction(Action toDo){  //if there is enough energy for the task, returns the number of actions in queue
		//else it will return -1
		int energyCost = toDo.baseCost;

		if (toDo.isMovement())
			energyCost = energyCost * (250-SPEED)/100;
		else if (toDo.isAttack())
			energyCost = energyCost * (250-ATTACK)/100;
		else if (toDo.isDefence())
			energyCost = energyCost * (250-DEFENCE)/100; 
		else if (toDo.isEat())
			energyCost = energyCost * (250-EAT)/100;
		else if (toDo.isSense())
			energyCost = energyCost * (250-SENSE)/100;
		else if (toDo.isFocus())
			energyCost = energyCost * (250-ATTACK)/100;

		energyCost = energyCost * actionQueue.size();

		if(energy - energyCost < 0)
			return -1;
		else{
			energy -= energyCost;
			actionQueue.addLast(toDo);
			return actionQueue.size();
		}  
	}


	//returns true if creature's action queue is empty either before or after method exicution
	//-----------------------------------------------------------------nextAction
	public boolean nextAction(String psk)throws CheaterException{
		if(!psk.equals(SECURE_KEY))
			throw new CheaterException();

		if(this.actionQueue.isEmpty()) {
			if(firstStateThisTick)
				this.qRawHealing = this.MAX_HEALTH/10;
			return true;	
		}
		
		if(firstStateThisTick && actionQueue.size() == 1)
			this.qRawHealing = this.MAX_HEALTH/20;
		
		Action toDo = actionQueue.removeLast();

		this.qXchange += toDo.xChange;
		this.qYchange += toDo.yChange;

		this.qDefenceBuff += (int)(toDo.defence * (this.DEFENCE/100.0));
		
		this.qAttackBuff += (int)(toDo.attack * (this.ATTACK/100.0));
		
		this.qSenseBuff += (int)(toDo.sense * (this.SENSE/100.0));

		if(toDo.isEat()){
			int eatAmmt;
			int maxEatAmmt = (int)(toDo.eat * (this.EAT/100.0));

			for(CordModifier cm : toDo.aoe.locations){
				eatAmmt = this.myWorld.eatSquare(this.xPos + cm.xMod, this.yPos + cm.yMod, maxEatAmmt ,SECURE_KEY);
				this.qEnergyChange += (eatAmmt * (this.EAT/100.0));
				this.score += eatAmmt;
				if(toDo.singleTarget && eatAmmt != 0)
					continue;
			}
		}
		if(toDo.isAttack()){
			if(this.qAttackBuff > 100)
				this.qAttackBuff = 100;
			
			for(CordModifier cm : toDo.aoe.locations){
				boolean didDamage = this.myWorld.damageSquare(this.xPos + cm.xMod, this.yPos + cm.yMod, (int)(toDo.attack * (this.qAttackBuff/100.0) * (this.ATTACK/100.0) * ((100 + this.qAttackBuff)/100.0)),SECURE_KEY);
				if(didDamage && toDo.singleTarget)
					continue;
			}
		}

		this.lastActionTaken = toDo;
		this.firstStateThisTick = false;
		return this.actionQueue.isEmpty();
	}

	//-----------------------------------------------------------------resolve
	public void resolve(String key) throws CheaterException{
		if(!key.equals(SECURE_KEY))
			throw new CheaterException();

		//max defencive buff of 90%
		if(this.qDefenceBuff > 90)
			this.qDefenceBuff = 90;

		//take the incoming damage, muiltiply the reduction percent. 
		//have a buff of 40? that means you take 60% of qRawIncomingDmg
		this.health -= this.qRawIncomingDmg *((100-this.qDefenceBuff)/100.0);
		this.health += this.qRawHealing;
		
		if(this.myWorld.moveRobitAt(this.xPos, this.yPos, this.qXchange, this.qYchange, SECURE_KEY)){
			this.xPos =this.myWorld.fc(this.xPos + qXchange);
			this.yPos =this.myWorld.fc(this.yPos + qYchange);
		}

		this.qRawIncomingDmg = 0;
		this.qDefenceBuff = (int)(this.qDefenceBuff*0.75);
		this.qAttackBuff = (int)(this.qAttackBuff * 0.75);

		this.qXchange = 0;
		this.qYchange = 0;


		//this if for potential use in mindTemplates. 
		@SuppressWarnings("unused")
		int worldSize = this.myWorld.WORLD_SIZE;      

		if(this.health <= 0){
			this.isDead = true;
			this.myWorld.killRobit(this.xPos,this.yPos);
		}
	}

	//-----------------------------------------------------------------tick
	public void tick(){
		if(!this.isDead){
			this.firstStateThisTick = true;
			try{
				mind.tick();
			}
			//reenable this once we are up and rolling
			//catch(CheaterException e){
			//   System.out.println("Creature " + this.id + " tried to cheat"); 
			//}
			catch(Exception e){
				System.out.println("Creature " + this.id + " encountered a problem and ended exicution prematurly" + e);
			}
		}
	}

	//-----------------------------------------------------------------incIncomingDmg
	public void incIncomingDmg(int ammt, String key)throws CheaterException{
		if(!key.equals(SECURE_KEY))
			throw new CheaterException();

		this.qRawIncomingDmg += ammt; 
	}
	//=============================================================================Gets/Sets
	public Color getColor() {return this.color;}

	public void setColor(Color c) {
		this.color = c;
	}
	
	public int getHealth()     	{return this.health;}
	public int getMaxHealth()  	{return this.MAX_HEALTH;}
	public int getAttack()		{return this.ATTACK;}
	public int getSpeed()		{return this.SPEED;}
	public int getEat()			{return this.EAT;}
	public int getStealth()    	{return this.STEALTH;} 
	public int getSense()      	{return this.SENSE;}
	public int getDefenceBuff()	{return this.qDefenceBuff;}
	public int getAttackBuff()	{return this.qAttackBuff;}
	public int getSenseBuff()  	{return this.qSenseBuff;}
	public int getEnergy()     	{return this.energy;}
	public int getMaxEnergy()  	{return this.MAX_ENERGY;}
	public int getDefence()    	{return this.DEFENCE;}
	public int getWorldSize()	{return this.worldSize;}
	
	public int getXpos()       	{return this.xPos;}
	public int getYpos()       	{return this.yPos;}  

	public String getId()      	{return this.id;}

	public boolean getIsDead() 	{return this.isDead;}

	public Action getLastAction() {return this.lastActionTaken;}

	public void setIsDead(boolean state,String key) {
		if(!key.equals(this.SECURE_KEY))
			this.isDead = state;	
	}

	public SensorSuite getSensorSuite(){return this.sensorSuite;} 

	public String getSpecies(){return this.species;}

	public boolean setHealth(int health, String securityCode){
		if (securityCode.equals(SECURE_KEY)){
			this.health = health;
			if(this.health > this.MAX_HEALTH)
				this.health = MAX_HEALTH;
			if(this.health < 0)
				this.health = 0;
			return true;
		}
		return false;
	}

	public void setWorld(World world, String key)throws CheaterException{
		if(!key.equals(SECURE_KEY))
			throw new CheaterException();

		this.myWorld = world;
	}

	public boolean isAlly(String speciedName) {
		return this.mind.isAlly(speciedName);
	}
	

	public LinkedList<Action> getActionQueue() {
		return actionQueue;
	}
	
	public int getScore() {
		return this.score;
	}
	
	public void setScore(int s, String psk) {
		if(psk.equals(this.SECURE_KEY))
			this.score = s;
	}
	
	public void changeScore(int change, String psk) {
		if(psk.equals(this.SECURE_KEY))
			this.score += change;
	}
	
	public void setEnergy(int e, String psk) {
		if(psk.equals(this.SECURE_KEY))
			this.energy = e;
	}
	
	public void setAttackBuff(int buff, String psk) {
		if(psk.equals(this.SECURE_KEY))
			this.qAttackBuff = buff;
	}
	
	public void setDefenceBuff(int buff,String psk) {
		if(psk.equals(this.SECURE_KEY))
			this.qDefenceBuff = buff;
	}
	
	public void setSenseBuff(int buff, String psk) {
		if(psk.equals(this.SECURE_KEY))
			this.qSenseBuff = buff;
	}
	
	public void setLastAction(Action a, String psk) {
		if(psk.equals(this.SECURE_KEY))
			this.lastActionTaken = a;
	}
	
	public void setXpos(int pos, String psk) {
		if(psk.equals(this.SECURE_KEY))
			this.xPos = pos;
	}
	
	public void setYpos(int pos, String psk) {
		if(psk.equals(this.SECURE_KEY))
			this.yPos = pos;
	}
}

