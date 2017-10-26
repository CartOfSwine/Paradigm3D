package worldData;

import com.jme3.math.ColorRGBA;

import playerMinds.MindTemplate;

import java.util.Random;

import robits.CheaterException;
import robits.Robit;
import worldData.Obstruction;

import java.lang.Math;

public class World  {

	//holds Robits
	private Robit[][] population;

	//holds a single instantiation of each player's mind
	private MindTemplate[] contenders;

	private String SECURE_KEY = "password";

	//Super important note: Coordinates 0,0 is in the UPPER left hand corner of the map. Y values increase as you go down (for the sake of arrays and a few other things)
	private Robit[][] RobitMap;
	private int[][] energyMap;
	private Obstruction[][] obstructionMap;

	private boolean nextTickFlag; 

	public final int WORLD_SIZE;

	public final int baseSensorRange = 10;
	public final boolean linearActivation = true;
	public final double smellDistanceModifier = 0.5;
	//=============================================================================Constructors
	public World(int size, int numWalls, MindTemplate[] contenders) {
		this.contenders = contenders;

		WORLD_SIZE = size;
		RobitMap = new Robit[WORLD_SIZE][WORLD_SIZE];
		obstructionMap = new Obstruction[WORLD_SIZE][WORLD_SIZE];
		energyMap = new int[WORLD_SIZE][WORLD_SIZE];  

		for (int y = 0; y < WORLD_SIZE; y++){
			for (int x = 0; x < WORLD_SIZE; x++){
				RobitMap[y][x] = null;
				energyMap[y][x] = 0;
				obstructionMap[y][x] = new Obstruction(x,y);
			}
		}
		this.nextTickFlag = true;

		placeWalls(numWalls);
		placeEnergys();
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
		int total = this.WORLD_SIZE * this.WORLD_SIZE;
		total = total/120;
		Random rnd = new Random();
		
		int rndX = rnd.nextInt(WORLD_SIZE);
		int rndY = rnd.nextInt(WORLD_SIZE);
		
		for(int i = 0; i < total; i++) {
			while(!obstructionMap[rndY][rndX].isEmpty()) {
				rndX = rnd.nextInt(WORLD_SIZE);
				rndY = rnd.nextInt(WORLD_SIZE);
			}
			obstructionMap[rndY][rndX] = new Obstruction(ObstructionType.PEDESTAL,rndX,rndY);
		}
		int numSplits = log2n((double)WORLD_SIZE * 2);
		splitSquare(numSplits, 0,0,WORLD_SIZE,WORLD_SIZE);
	}
	
	private void splitSquare(int numSplits, int x1, int y1, int x2, int y2) {
		int width = x2 - x1;
		Random rnd = new Random();
		if(numSplits <= 0 || width <= 4) {
			int sizeCat = log2n(width);
			
			Prefab toPlace = PrefabList.getRandomPrefab(sizeCat);
			int maxStartX = width - toPlace.maxX;
			int maxStartY = width - toPlace.maxY;
			
			int x0 = rnd.nextInt(maxStartX+1) + x1;
			int y0 = rnd.nextInt(maxStartY+1) + y1;

			Obstruction[][] oMap = toPlace.oMap;
			int[][] rMap = toPlace.rMap;

			for(int y = 0; y < rMap.length; y++) {
				for (int x = 0; x < rMap[y].length; x++) {
					obstructionMap[y0 + y][x0 + x] = new Obstruction(oMap[y][x].getType(),x0 + x,y0 + y);
					if(rMap[y][x] != 0)
						energyMap[y0+y][x0+x] = rMap[y][x]; 
				}
			}
			
		}
		else {
			int total = numSplits - 1;
			int newSplits = rnd.nextInt(total+1);
			splitSquare(newSplits,x1,y1,x1+width/2,y1+width/2);
			
			total -= newSplits;
			newSplits = rnd.nextInt(total+1);
			splitSquare(newSplits,x1,y1+width/2,x1+width/2,y2);
			
			total -= newSplits;
			newSplits = rnd.nextInt(total+1);
			splitSquare(newSplits,x1+width/2,y1+width/2,x2,y2);
			
			total -= newSplits;
			splitSquare(total,x1+width/2,y1,x2,y1 + width/2);
		}
	}
	
	private int log2n(double d) {
		if(d <= 4) return 0;
		return 1 + log2n(Math.ceil(d/2.0));
	}
	//-----------------------------------------------------------------placeEnergys
	private void placeEnergys(){
		int total = this.WORLD_SIZE * this.WORLD_SIZE;
		total = total/60;
		Random rnd = new Random();
		
		int rndX = rnd.nextInt(WORLD_SIZE);
		int rndY = rnd.nextInt(WORLD_SIZE);
		
		for(int i = 0; i < total; i++) {
			while(!(energyMap[rndY][rndX] == 0)) {
				rndX = rnd.nextInt(WORLD_SIZE);
				rndY = rnd.nextInt(WORLD_SIZE);
			}
			energyMap[rndY][rndX] = rnd.nextInt(20) + 20 ;
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
			energyMap[y][x] += toKill.getMaxEnergy()/2;
			energyMap[y][x] += toKill.getDefence()/2;
			obstructionMap[y][x] = new Obstruction(ObstructionType.CORPSE,x,y);
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

		int ammt = energyMap[y][x];
		if (ammt > maxAmmt)
			ammt = maxAmmt;

		energyMap[y][x] -= ammt;
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
		else if (!obstructionMap[y][x].isEmpty()) {
			return obstructionMap[y][x].damage(atk);
		}
		return false;       
	}

	//-----------------------------------------------------------------feedSensoryData
	private void feedSensorData(Robit c){  
		c.getSensorSuite().updateSenses(this, c);
	}

	//=============================================================================Gets/Sets
	public ColorRGBA getColor(int xVal, int yVal){
		int x = fc(xVal);
		int y = fc(yVal);

		if (RobitMap[y][x] != null)
			return RobitMap[y][x].getColor();
		if (!obstructionMap[y][x].isEmpty())
			return obstructionMap[y][x].getType().COLOR;
		if (energyMap[y][x] != 0){
			float v = energyMap[y][x]/50;
			if(v > .9) v = 0.9f;
			return new ColorRGBA(0f,1-v,1-v,1);
		}
		return obstructionMap[y][x].getType().COLOR;
	}   


	public Robit[][] getPopulation() {
		return this.population;
	}
	public Robit[][] getRobitMap(){
		return this.RobitMap;
	}
	public int[][] getEnergyMap(){
		return this.energyMap;
	}
	public Obstruction[][] getObstructionMap(){
		return this.obstructionMap;
	}
}



