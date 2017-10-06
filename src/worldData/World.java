package worldData;

import com.jme3.math.ColorRGBA;
import java.util.Random;

import action.AOE;
import robits.CheaterException;
import robits.MindTemplate;
import robits.Robit;

import java.lang.Math;

public class World {

	//holds Robits
	private Robit[][] population;

	//holds a single instantiation of each player's mind
	private MindTemplate[] contenders;

	private String SECURE_KEY = "password";

	private Robit[][] RobitMap;
	private int[][] resourceMap;
	private Obstruction[][] obstructionMap;

	private boolean nextTickFlag; 

	public final int WORLD_SIZE;

	public final int baseSensorRange = 10;
	public final boolean linearActivation = true;
	public final double smellDistanceModifier = 0.5;
	//=============================================================================Constructors
	public World(int size, int numWalls, MindTemplate[] contenders){
		this.contenders = contenders;

		WORLD_SIZE = size;
		RobitMap = new Robit[WORLD_SIZE][WORLD_SIZE];
		obstructionMap = new Obstruction[WORLD_SIZE][WORLD_SIZE];
		resourceMap = new int[WORLD_SIZE][WORLD_SIZE];  

		for (int y = 0; y < WORLD_SIZE; y++){
			for (int x = 0; x < WORLD_SIZE; x++){
				RobitMap[y][x] = null;
				resourceMap[y][x] = 0;
				obstructionMap[y][x] = new Obstruction();
			}
		}
		this.nextTickFlag = true;

		placeWalls(numWalls);
		placeResources();
	}

	//Construction is broken up into two phases. this is because Robits need a referance to the world they exist in
	
	public void initialize(int robitPop) {
		int[] a = new int[contenders.length];
		for (int i = 0; i < a.length;i++)
			a[i] = robitPop;
		
		initialize(a);
	}
	
	//call this one instead to make a jagged population array. 
	public void initialize(int[] robitPops){
		Random rnd = new Random();
		int rndX, rndY;

		//i is the contender index
		//j is the robit index
		MindTemplate[][] minds = new MindTemplate[robitPops.length][];
		this.population = new Robit[robitPops.length][];
		
		for (int i = 0; i < robitPops.length;i++) {
			minds[i] = new MindTemplate[robitPops[i]];
			population[i] = new Robit[robitPops[i]];
			
			for (int j = 0; j < robitPops[i]; j++){

				//use the mind class objects to instantiate minds
				try{
					minds[i][j] = this.contenders[i].getClass().newInstance();
				}
				catch (Exception e){
					System.out.println("Failed to instantiate contender mind classes:" + e);
				}

				do{        
					rndX = rnd.nextInt(WORLD_SIZE);
					rndY = rnd.nextInt(WORLD_SIZE);
				}
				while(RobitMap[rndY][rndX] != null || !obstructionMap[rndY][rndX].isEmpty());

				population[i][j] = new Robit((MindTemplate)minds[i][j], this,((Integer)i).toString(),rndX,rndY);
				RobitMap[rndY][rndX] = population[i][j];
				minds[i][j].setRobit(population[i][j]);

			}
			
			
		}

	}
	//=============================================================================Utilities
	//-----------------------------------------------------------------placeWalls
	private void placeWalls(int numWalls){
		//map[5][6] = new TileContents(ContentType.WALL,100);

	}
	//-----------------------------------------------------------------placeResources
	private void placeResources(){
		Random rnd = new Random();
		int rndX,rndY,rndNum;

		for(int c = 0; c < 1; c++){
			rndX = rnd.nextInt(WORLD_SIZE);
			rndY = rnd.nextInt(WORLD_SIZE);
			rndNum = rnd.nextInt(50);

			resourceMap[rndY][rndX] = rndNum;
		}
	}
	//-----------------------------------------------------------------tick
	public void tick(){
		//exicutes mind code if all queued actions are complete

		if(nextTickFlag){       
			for (int i = 0; i < population.length;i++) {
				for (Robit robit: population[i]){
					feedSensorData(robit);
					robit.tick();
				}
			}
		}
		try{
			//will queue up the effects of actions one at a time
			this.nextTickFlag = true;
			for (int i = 0; i < population.length; i++) {
				for (Robit robit: population[i]){
					if (robit.nextAction(SECURE_KEY) != true)
						this.nextTickFlag = false;
				}
			}
			//will resolve all queued effects on Robits
			for (int i = 0; i < population.length; i++) {
				for (Robit robit: population[i]){
					robit.resolve(SECURE_KEY);
				}
			}

		}
		catch(CheaterException e){
			System.out.println("Somehow my own code is cheating...?");
		}

	}
	//-----------------------------------------------------------------killRobitAt
	public void killRobit(int xPos, int yPos){
		int x = fc(xPos);
		int y = fc(yPos);
		Robit toKill = RobitMap[y][x];

		if(RobitMap[y][x] != null){
			RobitMap[y][x] = null;
			resourceMap[y][x] += toKill.getMaxEnergy()/2;
			resourceMap[y][x] += toKill.getDefence()/2;
			obstructionMap[y][x] = new Obstruction(ObstructionType.CORPSE);
		}
	}

	//-----------------------------------------------------------------moveRobitAt
	public boolean moveRobitAt(int sX, int sY, int changeX, int changeY, String key) throws CheaterException{
		if (!key.equals(SECURE_KEY))
			throw new CheaterException();
		int startX, startY, endX, endY;

		startX = fc(sX);
		startY = fc(sY);
		endX = fc(sX+changeX);
		endY = fc(sY+changeY);

		if(RobitMap[startY][startX] != null && 
				RobitMap[endY][endX] == null && 
				obstructionMap[endY][endX].isEmpty()){

			RobitMap[endY][endX] = RobitMap[startY][startX];
			RobitMap[startY][startX] = null;
			return true;
		}
		return false;
	}

	//-----------------------------------------------------------------isEmpty
	public boolean isEmpty(int x, int y, String key) throws CheaterException{
		if(!key.equals(SECURE_KEY))
			throw new CheaterException();

		//no need to precheck the cordinates as they are rectified
		return (RobitMap[fc(y)][fc(x)] == null && obstructionMap[fc(y)][fc(x)].isEmpty());
	}

	//-----------------------------------------------------------------fc
	public int fc(int cord){   //ensures the number is a valid cordinate on a square world map
		int temp = cord % WORLD_SIZE;
		if(temp < 0)
			temp = WORLD_SIZE + temp;
		return temp;
	}

	//-----------------------------------------------------------------eatSquare
	public int eatSquare(int xVal, int yVal, int maxAmmt,String key) throws CheaterException{
		if(!key.equals(SECURE_KEY))
			throw new CheaterException();

		int x = fc(xVal);
		int y = fc(yVal);

		int ammt = resourceMap[y][x];
		if (ammt > maxAmmt)
			ammt = maxAmmt;

		resourceMap[y][x] -= ammt;
		return ammt;
	}

	//-----------------------------------------------------------------damageSquare
	public boolean damageSquare(int xVal, int yVal, int atk, String key)throws CheaterException{
		if(!key.equals(this.SECURE_KEY))
			throw new CheaterException();

		int x = fc(xVal);
		int y = fc(yVal);

		if(RobitMap[y][x] != null){
			RobitMap[y][x].incIncomingDmg(atk,SECURE_KEY);
			return true;
		}
		return false;       
	}

	//-----------------------------------------------------------------feedSensoryData
	private void feedSensorData(Robit c){   //sorry to anyone trying to read this. needed it to run fast AF so i klueged the whole thing into the one loop set
		int senses,senseBuff,senseDistance,x,y,dx,dy,xVal,yVal,otherStealth,otherStealthAddition;
		double distance, stealthedDistance;

		SensorSuite RobitSenses = c.getSensorSuite();
		senses = c.getSense();                          //sense stat
		x = fc(c.getXpos());                            //xpos of Robit
		y = fc(c.getYpos());                            //ypos of Robit
		senseBuff = c.getSenseBuff();
		senseDistance = (int)((baseSensorRange + (senses-100)/10.0)*((100+senseBuff)/100.0)) + 1;//max sensory distance

		double minSightDist = 20000000;                 //the smallest distance to a sight target
		SightTarget sightType = RobitSenses.getSightTargetType();
		int smellMaxRange;

		int newSightSense = 0;
		int newSightAngle = 0;
		boolean newSightHasTarget = false;

		int[] newHearingSense = new int[4];
		int[] newEnergySmellSense = new int[4];
		int[] newEnemySmellSense = new int[4];
		int[] newAllySmellSense = new int[4];

		int[] newObstructionTouchSense = new int[4];
		int[] newEnergyTouchSense = new int[5];
		boolean[] newEnemyTouchSense = new boolean[4];
		boolean[] newAllyTouchSense = new boolean[4]; 

		for(int xv = x-senseDistance;xv <= x+senseDistance;xv++){ //loop through a box around the Robit checking each square's contents
			for(int yv = y-senseDistance;yv <= y+senseDistance;yv++){
				xVal = fc(xv);                            //the currently scanning square cords after wrapping sides
				yVal = fc(yv); 
				dx = x-xv;                                //the difference in xvals
				dy = y-yv;                                //the difference in yvals

				//distance formula
				distance = Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2));
				smellMaxRange = (int)(senseDistance * smellDistanceModifier); //smell is short range, factor in the range limiter


				//if we found a Robit other than the current Robit
				if(RobitMap[yVal][xVal] != null && (xVal != x && yVal != y)){     //handle if tile contains Robit
					Robit o = RobitMap[yVal][xVal];                             //the Robit in scanning range

					otherStealth = o.getStealth();                                    //others stealth stat
					otherStealthAddition = (otherStealth-100)/10;

					stealthedDistance = distance + (otherStealth-100)/10;             //the distance factoring in o's stealth stat


					if(c.getSpecies().equals(o.getSpecies())){
						populateSmellSenseArray(newAllySmellSense,smellMaxRange,dx,dy,1);             
						if((sightType == SightTarget.ALLY || sightType == SightTarget.ROBIT) && minSightDist > distance){
							newSightHasTarget = true;
							minSightDist = distance;
							newSightSense = calcActivity(senseDistance,(int)distance);
							newSightAngle = calcAngle(dy,dx);
						}
					}
					else{                       
						populateSmellSenseArray(newEnemySmellSense,smellMaxRange,dx+otherStealthAddition,dy+otherStealthAddition,1);
						if((sightType == SightTarget.ENEMY || sightType == SightTarget.ROBIT) && minSightDist > stealthedDistance){
							newSightHasTarget = true;
							minSightDist = stealthedDistance;
							newSightSense = calcActivity(senseDistance,(int)stealthedDistance);
							newSightAngle = calcAngle(dy+otherStealthAddition,dx+otherStealthAddition);
						}
					} 
					populateSmellSenseArray(newHearingSense,senseDistance,dx+otherStealthAddition,dy+otherStealthAddition,1);
				}
				//if there is a resource patch in the selected square
				if(resourceMap[yVal][xVal] > 0){
					populateSmellSenseArray(newEnergySmellSense,smellMaxRange,dx,dy,resourceMap[yVal][xVal]/50.0);
					if(sightType == SightTarget.ENERGY && minSightDist > distance){
						newSightHasTarget = true;
						minSightDist = distance;
						newSightSense = (int)(calcActivity(senseDistance,(int)distance) * resourceMap[yVal][xVal]/50.0);
						newSightAngle = calcAngle(dy,dx);
					}
				}
				//if the selected square contains a corpse and we are looking for a coprpse
				if(obstructionMap[yVal][xVal].type == ObstructionType.CORPSE && minSightDist > distance){
					newSightHasTarget = true;
					minSightDist = distance;
					newSightSense = calcActivity(senseDistance,(int)distance);
					newSightAngle = calcAngle(dy,dx);
				}
				//if the selected square contains an obsticle and we are looking for one
				if(obstructionMap[yVal][xVal].type != ObstructionType.EMPTY && minSightDist > distance){
					newSightHasTarget = true;
					minSightDist = distance;
					newSightSense = calcActivity(senseDistance,(int)distance);
					newSightAngle = calcAngle(dy,dx);
				}
			}
		}  
		//populate touch senses
		AOE myAOE = AOE.ADJACENT;
		for(int i = 0; i < myAOE.locations.length;i++){
			Robit selectC = RobitMap[fc(y+myAOE.locations[i].yMod)][fc(x+myAOE.locations[i].xMod)];
			Obstruction selectO = obstructionMap[fc(y+myAOE.locations[i].yMod)][fc(x+myAOE.locations[i].xMod)];         
			if(selectC != null){
				if(selectC.getSpecies().equals(c.getSpecies()))
					newAllyTouchSense[i] = true;
				else
					newEnemyTouchSense[i] = true;
			}
			newEnergyTouchSense[i] = resourceMap[fc(y+myAOE.locations[i].yMod)][fc(x+myAOE.locations[i].xMod)];
			newObstructionTouchSense[i] = selectO.curHP;
		}   
		newEnergyTouchSense[4] = resourceMap[y][x];
		RobitSenses.setSightSense(newSightSense);
		RobitSenses.setSightAngle(newSightAngle);
		RobitSenses.setSightHasTarget(newSightHasTarget);

		RobitSenses.setHearingSense(newHearingSense);
		RobitSenses.setEnergySmellSense(newEnergySmellSense);
		RobitSenses.setEnemySmellSense(newEnemySmellSense);
		RobitSenses.setAllySmellSense(newAllySmellSense);
		RobitSenses.setObstructionTouchSense(newObstructionTouchSense);
		RobitSenses.setEnergyTouchSense(newEnergyTouchSense);
		RobitSenses.setEnemyTouchSense(newEnemyTouchSense);
		RobitSenses.setAllyTouchSense(newAllyTouchSense);
	}

	//-----------------------------------------------------------------calcAngle
	private int calcAngle(int dy, int dx){
		//rewrite this when you have a moment. You were on a plane when you wrote this and were running
		//on 1.5 hrs of sleep. NEEDS POLISH. Its way to klueged rn 

		dx = dx * -1; //im not sure why i have to do this tbh. 

		if(dx == 0){
			if(dy > 0)
				return 90;
			if(dy < 0)
				return 270;
		}
		if(dy == 0 && dx < 0)
			return 180;

		int tempAngle = (int)Math.toDegrees(Math.atan((dy/(double)dx)));

		if(dy < 0 && dx < 0)
			tempAngle += 180;
		else if(dy > 0 && dx < 0)
			tempAngle = -1*tempAngle + 90;
		else if(dy < 0 && dx > 0)
			tempAngle += 360;

		return tempAngle;
	}

	//-----------------------------------------------------------------populateSmellSenseArray
	private void populateSmellSenseArray(int[] ara, int maxRange, int dx, int dy, double muiltiplier){
		double d = Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2));

		//
		if(Math.abs(dy) <= Math.abs(dx) && dx > 0)
			ara[3] +=(int)(calcActivity(maxRange,d-1)*muiltiplier);
		if(Math.abs(dy) <= Math.abs(dx) && dx < 0)
			ara[1] +=(int)(calcActivity(maxRange,d-1)*muiltiplier);     
		if(Math.abs(dy) >= Math.abs(dx) && dy > 0)
			ara[0] +=(int)(calcActivity(maxRange,d-1)*muiltiplier);
		if(Math.abs(dy) >= Math.abs(dx) && dy < 0)
			ara[2] +=(int)(calcActivity(maxRange,d-1)*muiltiplier);

	}

	//-----------------------------------------------------------------calcActivity
	private int calcActivity(int maxSensorRange, double measuredRange){
		int activity;
		if(this.linearActivation)
			activity = (int)(-1.0 * measuredRange * (100.0/maxSensorRange) + 100.0);
		else
			activity = (int)(-1* Math.pow(measuredRange - maxSensorRange,3) * (100.0/Math.pow(maxSensorRange,3)));
		if(activity < 0)
			activity = 0;
		if(activity > 100)
			activity = 100;
		return activity;
	}

	//=============================================================================Gets/Sets
	public ColorRGBA getColor(int xVal, int yVal){
		int x = fc(xVal);
		int y = fc(yVal);

		if (RobitMap[y][x] != null)
			return RobitMap[y][x].getColor();
		if (!obstructionMap[y][x].isEmpty())
			return obstructionMap[y][x].type.COLOR;
		if (resourceMap[y][x] != 0){
			float v = resourceMap[y][x]/50;
			if(v > .9) v = 0.9f;
			return new ColorRGBA(0f,1-v,1-v,1);
		}
		return obstructionMap[y][x].type.COLOR;
	}   


	public Robit[][] getPopulation() {
		return this.population;
	}
	public Robit[][] getRobitMap(){
		return this.RobitMap;
	}
	public int[][] getResourceMap(){
		return this.resourceMap;
	}
	public Obstruction[][]getObstructionMap(){
		return this.obstructionMap;
	}

	private class Obstruction{
		private int curHP;
		private ObstructionType type;

		public Obstruction(){
			this.type = ObstructionType.EMPTY;
			this.curHP = this.type.MAXHP;
		}

		public Obstruction(ObstructionType type){
			this.type = type;
			this.curHP = type.MAXHP;
		}

		public boolean isEmpty(){
			return this.type == ObstructionType.EMPTY;
		}

		@SuppressWarnings("unused")
		public void damage(int attk, String key)throws CheaterException{
			if (!key.equals(SECURE_KEY))
				throw new CheaterException();
			this.curHP -= (int)attk * this.type.DEFENCE;
			if(this.curHP <= 0){
				this.curHP = 0;
				this.type = ObstructionType.EMPTY;
			}
		}
	}
}

enum ObstructionType{
	EMPTY(1,ColorRGBA.White,0),
	BUSH(1.0, ColorRGBA.Green,20),
	TREE(0.7,ColorRGBA.Brown,50), //brown color
	ROCK(0.3, ColorRGBA.Gray,100),
	CORPSE(1.1, ColorRGBA.Orange,20);

	public final double DEFENCE;
	public final ColorRGBA COLOR;
	public final int MAXHP;

	ObstructionType(double defence, ColorRGBA color, int maxHP){
		this.DEFENCE = defence;
		this.COLOR = color;
		this.MAXHP = maxHP;
	}
}

