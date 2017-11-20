package worldData;

import playerMinds.MindTemplate;
import replay.EnergyDelta;
import replay.ObstructionDelta;
import replay.RobitPlaceholder;
import replay.SimulationRecord;

import java.util.LinkedList;
import java.util.Random;

import robits.CheaterException;
import robits.Robit;
import worldData.Obstruction;

import java.awt.Color;
import java.lang.Math;
import java.awt.Point;

public class World  {

	//holds Robits
	private Robit[][] population;
	private int[] speciesScores;

	//holds a single instantiation of each player's mind
	private MindTemplate[] contenders;

	private final String SECURE_KEY;

	//Super important note: Coordinates 0,0 is in the UPPER left hand corner of the map. Y values increase as you go down (for the sake of arrays and a few other things)
	private Robit[][] robitMap;
	private int[][] energyMap;
	private Obstruction[][] obstructionMap;

	private boolean nextTickFlag; 

	public final int WORLD_SIZE;

	public final int baseSensorRange = 10;
	public final boolean linearActivation = true;
	public final double smellDistanceModifier = 0.5;
	
	private boolean goingForward = true;
	
	LinkedList<EnergyDelta> energyChanges;
	LinkedList<ObstructionDelta> obstructionChanges;

	private final boolean usingRecord;
	private SimulationRecord record;
	
	//=============================================================================Constructors
	public World(SimulationRecord record) {
		this.usingRecord = true;
		
		this.record = record;
		
		this.SECURE_KEY = record.getKey();
		this.WORLD_SIZE = record.getWorldSize();
		
		Obstruction[][]rObstructions = record.getStartingObstrucitons();
		int[][] rEnergy = record.getStartingEnergy();
		RobitPlaceholder[][] rRobits = record.getPopulation();
		
		this.robitMap = new Robit[this.WORLD_SIZE][this.WORLD_SIZE];
		this.energyMap = new int[this.WORLD_SIZE][this.WORLD_SIZE];
		this.obstructionMap = new Obstruction[this.WORLD_SIZE][this.WORLD_SIZE];
		
		//deep copy over from starting configuration
		for(int y = 0; y < rEnergy.length; y++) {
			for(int x = 0; x < rEnergy[0].length; x++) {
				this.obstructionMap[y][x] = new Obstruction(rObstructions[y][x]);
				this.energyMap[y][x] = rEnergy[y][x];
			}
		}
		
		//deep copy over robits
		this.population = new Robit[rRobits.length][];
		for(int s = 0; s < rRobits.length; s++) {
			this.population[s] = new Robit[rRobits[s].length];
			for(int m = 0; m < rRobits[s].length;m++) {
				this.population[s][m] = new Robit(rRobits[s][m],this.WORLD_SIZE, this.SECURE_KEY);
				Robit r = this.population[s][m];
				this.robitMap[r.getYpos()][r.getXpos()] = r;
			}
			
		}
		
		this.speciesScores = new int[this.population.length];
		
		this.record.setRecordingMode(false);
		this.record.attachWorld(this);
	}
	
	public World(int size, int numWalls, MindTemplate[] contenders, String key) {
		this.contenders = contenders;
		this.usingRecord = false;
		WORLD_SIZE = size;
		robitMap = new Robit[WORLD_SIZE][WORLD_SIZE];
		obstructionMap = new Obstruction[WORLD_SIZE][WORLD_SIZE];
		energyMap = new int[WORLD_SIZE][WORLD_SIZE];  

		for (int y = 0; y < WORLD_SIZE; y++){
			for (int x = 0; x < WORLD_SIZE; x++){
				robitMap[y][x] = null;
				energyMap[y][x] = 0;
				obstructionMap[y][x] = new Obstruction(x,y);
			}
		}
		this.nextTickFlag = true;
		this.SECURE_KEY = key;
		
		this.energyChanges = new LinkedList<>();
		this.obstructionChanges = new LinkedList<>();
		
		int numSplits = log2n((double)WORLD_SIZE * 2);
		splitSquare(numSplits, 0,0,WORLD_SIZE,WORLD_SIZE,-1);
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
				while(robitMap[rndY][rndX] != null || !obstructionMap[rndY][rndX].isEmpty());

				population[i][j] = new Robit((MindTemplate)minds[i][j], this,((Integer)i).toString(),rndX,rndY, this.SECURE_KEY);
				robitMap[rndY][rndX] = population[i][j];
				minds[i][j].setRobit(population[i][j]);

			}
		}
		this.speciesScores = new int[this.population.length];
		this.record = new SimulationRecord(this,this.SECURE_KEY);
	}
	//=============================================================================Utilities
	//-----------------------------------------------------------------tick
		public void tick(){
			boolean recordNewTick = false;
			
			//exicutes mind code if all queued actions are complete
			if(usingRecord) {
				if(!this.record.hasNext())
					goingForward = false;
				if(!this.record.hasPrevoius())
					goingForward = true;
				
				
				if(goingForward)
					this.record.next();
				else
					this.record.previous();
			}
			else {
				if(nextTickFlag){       
					for (int i = 0; i < population.length;i++) {
						for (Robit robit: population[i]){
							feedSensorData(robit);
							robit.tick();
						}
					}
					recordNewTick = true;
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
				catch(CheaterException e){}
				
				if(recordNewTick)
					this.record.recordTick();
				else
					this.record.recordState();
				
				this.energyChanges.clear();
				this.obstructionChanges.clear();
			}
			updateScores();
		}
	
	
	
	//-----------------------------------------------------------------placeWalls
	private void scatterStuff(int minX, int minY, int maxX, int maxY, 
			int energyDensity, int obstructionDensity, 
			ObstructionType type) {
		int energyTotal;
		if(energyDensity == -1)
			energyTotal = 0;
		else
			energyTotal = (maxY - minY) * (maxX - minX) / energyDensity;
		
		int obstructionTotal;
		if(obstructionDensity == -1)
			obstructionTotal = 0;
		else
			obstructionTotal = (maxY - minY) * (maxX - minX) / obstructionDensity;
		
		Random rnd = new Random();
		
		int rndX = rnd.nextInt(maxX-minX) + minX;
		int rndY = rnd.nextInt(maxY-minY) + minY;
		
		for(int i = 0; i < energyTotal;i++) {
			while(energyMap[rndY][rndX] != 0) {
				rndX = rnd.nextInt(maxX-minX) + minX;
				rndY = rnd.nextInt(maxY-minY) + minY;
			}
			energyMap[rndY][rndX] = rnd.nextInt(15) + 5;
		}
		
		rndX = rnd.nextInt(maxX-minX) + minX;
		rndY = rnd.nextInt(maxY-minY) + minY;
		for(int i = 0; i < obstructionTotal;i++) {
			while(!obstructionMap[rndY][rndX].isEmpty()) {
				rndX = rnd.nextInt(maxX-minX) + minX;
				rndY = rnd.nextInt(maxY-minY) + minY;
			}
			obstructionMap[rndY][rndX] = new Obstruction(type,rndX,rndY);
		}
	}
	
	private void placePOI(int x1, int y1, int x2, int y2) {
		int width = x2 - x1;
		int sizeCat = log2n(width);
		Random rnd = new Random();
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
		
		rotateArea(x1,y1,width,rnd.nextInt(4));
	}
	
	private void blanketArea(int minX, int minY, int maxX, int maxY, 
			boolean placeEnergy, boolean placeObstructions, ObstructionType type) {
		Random rnd = new Random();
		for(int x = minX; x < maxX; x++) {
			for(int y = minY; y < maxY;y++) {
				if(placeEnergy)
					energyMap[y][x] = rnd.nextInt(6)+1;
				if(placeObstructions)
					obstructionMap[y][x] = new Obstruction(type,x,y);
			}
		}
	}
	
	private void splitSquare(int numSplits, int x1, int y1, int x2, int y2, int type) {
		int width = x2 - x1;
		Random rnd = new Random();
		if(numSplits <= 0 || width <= 4) {
			int biome = type;
			if(biome == -1) biome = rnd.nextInt(7);	
			
			switch(biome) {
			case 0:
				//energy ocean
				blanketArea(x1,y1,x2,y2,true,false,null);
				break;
			case 1:
			case 5:  				//TODO bastion
				//plains
				scatterStuff(x1,y1,x2,y2,60,-1,null);
				break;
			case 2:
				//pillar forest
				scatterStuff(x1,y1,x2,y2,200,60,ObstructionType.PILLAR);
				break;
			case 3:
				//dense city
				splitSquare(width,x1,y1,x2,y2,7);
				break;
			case 4:
				//light city
				splitSquare(width/4,x1,y1,x2,y2,7);
				break;

			case 6:
				//empty section, do nothing
				break;
			case 7:
				//Place POI
				placePOI(x1,y1,x2,y2);
				break;
			}
				
		}
		else {
			int total = numSplits - 1;
			int newSplits = rnd.nextInt(total+1);
			splitSquare(newSplits,x1,y1,x1+width/2,y1+width/2,type);
			
			total -= newSplits;
			newSplits = rnd.nextInt(total+1);
			splitSquare(newSplits,x1,y1+width/2,x1+width/2,y2,type);
			
			total -= newSplits;
			newSplits = rnd.nextInt(total+1);
			splitSquare(newSplits,x1+width/2,y1+width/2,x2,y2,type);
			
			total -= newSplits;
			splitSquare(total,x1+width/2,y1,x2,y1 + width/2,type);
		}
	}
	
	private void rotateArea(int startX, int startY, int width, int rotation) {
		for(;width > 0 && rotation != 0; width -= 2, startX += 1, startY += 1) {
			Point[] points = new Point[4];
			for(int i = 0; i < points.length; i++) {
				points[i] = new Point(0,0);
			}
			
			for (int mod = 0; mod < width-1; mod++) {
				points[0].setLocation(startX + mod, startY);
				points[1].setLocation(startX + width-1, startY + mod);
				points[2].setLocation(startX + width-1 - mod,startY + width-1);
				points[3].setLocation(startX, startY + width-1 - mod);
				
				if(rotation == 2) {
					swap(points[0], points[2]);
					swap(points[1], points[3]);
				}else {
					int tempE = getEnergyAt(points[0]);
					Obstruction tempO = getObstructionAt(points[0]);
					int[] indicies = new int[] {0,1,2,3};
					if(rotation == 3) indicies = new int[] {0,3,2,1};
					
					for(int i = 0; i < indicies.length -1; i++) {
						int cur = indicies[i];
						int next = indicies[i+1];
						setEnergyAt(points[cur],getEnergyAt(points[next]));
						setObstructionAt(points[cur],getObstructionAt(points[next]));
					}
					setEnergyAt(points[indicies[3]],tempE);
					setObstructionAt(points[indicies[3]],tempO);
				}
				
			}
		}
	}
	
	private void swap(Point p1, Point p2) {
		Obstruction tempO = getObstructionAt(p1);
		int tempE = getEnergyAt(p1);
		setObstructionAt(p1,getObstructionAt(p2));
		setEnergyAt(p1,getEnergyAt(p2));
		setObstructionAt(p2,tempO);
		setEnergyAt(p2,tempE);
	}
	
	private int log2n(double d) {
		if(d <= 4) return 0;
		return 1 + log2n(Math.ceil(d/2.0));
	}
	
	
	private void updateScores() {
		for(int s = 0; s < this.speciesScores.length;s++) {
			int total = 0;
			for(int m = 0; m < population[s].length;m++) {
				total += population[s][m].getScore();
			}
			speciesScores[s] = total;
		}
		/*System.out.print("[");
		for(int s : speciesScores)
			System.out.print(s + ",");
		System.out.println("]");*/
	}
	
	//-----------------------------------------------------------------killRobitAt
	public void killRobit(int xPos, int yPos){
		int x = fc(xPos);
		int y = fc(yPos);
		Robit toKill = robitMap[y][x];

		if(robitMap[y][x] != null){
			robitMap[y][x] = null;
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

		if(robitMap[startY][startX] != null && 
				robitMap[endY][endX] == null && 
				obstructionMap[endY][endX].isEmpty()){

			robitMap[endY][endX] = robitMap[startY][startX];
			robitMap[startY][startX] = null;
			return true;
		}
		return false;
	}

	//-----------------------------------------------------------------isEmpty
	public boolean isEmpty(int x, int y, String key) throws CheaterException{
		if(!key.equals(SECURE_KEY))
			throw new CheaterException();

		//no need to precheck the cordinates as they are rectified
		return (robitMap[fc(y)][fc(x)] == null && obstructionMap[fc(y)][fc(x)].isEmpty());
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

		int startAmmt = energyMap[y][x];
		
		int ammt = energyMap[y][x];
		if (ammt > maxAmmt)
			ammt = maxAmmt;

		energyMap[y][x] -= ammt;
		
		if(ammt != 0) 
			this.energyChanges.add(new EnergyDelta(x, y, startAmmt,energyMap[y][x]));
		
		return ammt;
	}

	//-----------------------------------------------------------------damageSquare
	public boolean damageSquare(int xVal, int yVal, int atk, String key)throws CheaterException{
		if(!key.equals(this.SECURE_KEY))
			throw new CheaterException();

		int x = fc(xVal);
		int y = fc(yVal);

		if(robitMap[y][x] != null){
			robitMap[y][x].incIncomingDmg(atk,SECURE_KEY);
			return true;
		} 
		else if (!obstructionMap[y][x].isEmpty()) {
			ObstructionType beforeType = obstructionMap[y][x].getType();
			int beforeHP = obstructionMap[y][x].getHP();
			if(obstructionMap[y][x].damage(atk)) {
				this.obstructionChanges.add(new ObstructionDelta(
						x, y,
						beforeType, 
						obstructionMap[y][x].getType(), 
						beforeHP, 
						obstructionMap[y][x].getHP()));
				return true;
			}
			else
				this.obstructionChanges.add(new ObstructionDelta(
						x, y,
						beforeType, 
						obstructionMap[y][x].getType(), 
						beforeHP, 
						obstructionMap[y][x].getHP()));
		}
		return false;       
	}

	//-----------------------------------------------------------------feedSensoryData
	private void feedSensorData(Robit c){  
		c.getSensorSuite().updateSenses(this, c);
	}

	//=============================================================================Gets/Sets
	public Color getColor(int xVal, int yVal){
		int x = fc(xVal);
		int y = fc(yVal);

		if (robitMap[y][x] != null)
			return robitMap[y][x].getColor();
		if (!obstructionMap[y][x].isEmpty())
			return obstructionMap[y][x].getType().COLOR;
		if (energyMap[y][x] != 0){
			float v = energyMap[y][x]/50;
			if(v > .9) v = 0.9f;
			return new Color(0f,1-v,1-v,1);
		}
		return obstructionMap[y][x].getType().COLOR;
	}   

	public Obstruction getObstructionAt(Point p) {
		int x = fc((int)p.getX());
		int y = fc((int)p.getY());
		return this.obstructionMap[y][x];
	}
	
	public int getEnergyAt(Point p) {
		int x = fc((int)p.getX());
		int y = fc((int)p.getY());
		return this.energyMap[y][x];
	}
	
	public void setObstructionAt(Point p, Obstruction o) {
		int x = fc((int)p.getX());
		int y = fc((int)p.getY());
		this.obstructionMap[y][x] = new Obstruction(o.getType(),x,y);
	}
	
	public void setEnergyAt(Point p, int e) {
		int x = fc((int)p.getX());
		int y = fc((int)p.getY());
		this.energyMap[y][x] = e;
	}
	
	public LinkedList<EnergyDelta> getEnergyChanges(){
		return this.energyChanges;
	}
	
	public LinkedList<ObstructionDelta> getObstrucitonChanges(){
		return this.obstructionChanges;
	}
	
	public Robit[][] getPopulation() {
		return this.population;
	}
	
	public Robit[][] getRobitMap(){
		return this.robitMap;
	}
	
	public int[][] getEnergyMap(){
		return this.energyMap;
	}
	
	public Obstruction[][] getObstructionMap(){
		return this.obstructionMap;
	}
	
	public SimulationRecord getSimulationRecord() {
		return this.record;
	}
	
	public int[] getScores() {
		return this.speciesScores;
	}
}



