package replay;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.ListIterator;

import robits.Robit;
import worldData.Obstruction;
import worldData.World;

public class SimulationRecord implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String SECURE_KEY;
	
	private final RobitPlaceholder[][] population;
	private final LinkedList<Tick<MapChange>> mapHistory;
	
	private final int[][] startingEnergyMap;
	private final Obstruction[][] startingObstrucitonMap;
	
	//TODO the World class still doesnt do jackall to support this.  make it happen
	private ListIterator<Tick<RobitState>>[][] robitTickIterators;
	private ListIterator<RobitState>[][] robitStateIterators;
	
	private ListIterator<Tick<MapChange>> mapTickIterator;
	private ListIterator<MapChange>	mapStateIterator;
	
	private boolean recordMode = true;
	private World attachedWorld;
	
	private int simulationLength = 0;
	private int worldSize;
	
	public SimulationRecord(World w, String key) {
		this.SECURE_KEY = key;
		this.worldSize = w.WORLD_SIZE;
		//Deep copy starting energy configuration
		startingEnergyMap = new int[w.WORLD_SIZE][];
		int[][] wEnergy = w.getEnergyMap();
		for(int y = 0; y < w.WORLD_SIZE; y++) {
			startingEnergyMap[y] = new int[w.WORLD_SIZE];
			for(int x = 0; x < w.WORLD_SIZE; x++) {
				startingEnergyMap[y][x] = wEnergy[y][x];
			}
		}
		
		//Deep copy of obstruction objects
		this.startingObstrucitonMap = new Obstruction[w.WORLD_SIZE][];
		Obstruction[][] wObstructions = w.getObstructionMap();
		for(int y = 0; y < w.WORLD_SIZE; y++) {
			this.startingObstrucitonMap[y] = new Obstruction[w.WORLD_SIZE];
			for(int x = 0; x < w.WORLD_SIZE; x++) {
				this.startingObstrucitonMap[y][x] = new Obstruction(wObstructions[y][x]);
			}
		}
		
		//initialize robit placeholders
		Robit[][] pop = w.getPopulation();
		this.population = new RobitPlaceholder[pop.length][];
		for(int s = 0; s < pop.length; s++) {
			this.population[s] = new RobitPlaceholder[pop[s].length];
			for(int m = 0; m < pop[s].length; m++) {
				this.population[s][m] = new RobitPlaceholder(pop[s][m]);
			}
		}
		
		mapHistory = new LinkedList<>();
		
		robitTickIterators = null;
		robitStateIterators = null;
		
		mapTickIterator = null;
		mapStateIterator = null;
		
		this.attachedWorld = w;
	}
	
	public void attachWorld(World w) {
		if(w == null)
			throw new IllegalArgumentException("World to attach to SimulationRecord was null");
		if(w.WORLD_SIZE != this.startingEnergyMap.length|| checkPopulationSizes(w.getPopulation())) {
			this.attachedWorld = w;
		}
	}
	
	//returns false until the simulation reaches it's end
	public boolean next() {
		if(!this.hasNext())
			return true;
		
		if(recordMode)
			throw new IllegalStateException("Cannot read from SimulationRecord, in record mode");
		
		Robit[][] worldPop = this.attachedWorld.getPopulation();
		for(int s = 0; s < population.length; s++) {
			for(int m = 0; m < population[s].length; m++) {
				RobitState r;
				//if we are not done with the tick
				if(robitStateIterators[s][m].hasNext()){
					r = robitStateIterators[s][m].next();
				}
				else {
					robitStateIterators[s][m] = robitTickIterators[s][m].next().iterator();
					r = robitStateIterators[s][m].next(); //allways at least 1 state per tick
				}
				populateRobit(worldPop[s][m],r);
			}
		}
		
		MapChange m;
		if(this.mapStateIterator.hasNext()) {
			m = this.mapStateIterator.next();
		}
		else {
			this.mapStateIterator = this.mapTickIterator.next().iterator();
			m = this.mapStateIterator.next();
		}
		
		LinkedList<EnergyDelta> eDeltas = m.getEnergyChanges();
		LinkedList<ObstructionDelta> oDeltas = m.getObstructionChanges();
		
		int[][] eMap = this.attachedWorld.getEnergyMap();
		Obstruction[][] oMap = this.attachedWorld.getObstructionMap();
		
		for(EnergyDelta d : eDeltas) {
			eMap[d.getYpos()][d.getXpos()] = d.getAfter();
		}
		
		for(ObstructionDelta d : oDeltas) {
			oMap[d.getYpos()][d.getXpos()] = d.getAfter();
		}
		
		return false;
	}
	
	//returns false until the beginning is reached
	public boolean previous() {
		if(!this.hasPrevoius())
			return true;
		
		if(recordMode)
			throw new IllegalStateException("Cannot read from SimulationRecord, in record mode");
		
		Robit[][] worldPop = this.attachedWorld.getPopulation();
		for(int s = 0; s < population.length; s++) {
			for(int m = 0; m < population[s].length; m++) {
				RobitState r;
				//if we are not done with the tick
				if(robitStateIterators[s][m].hasPrevious()){
					r = robitStateIterators[s][m].previous();
				}
				else {
					Tick<RobitState> temp = robitTickIterators[s][m].previous();
					robitStateIterators[s][m] = temp.iterator(temp.getStates().size());
					r = robitStateIterators[s][m].previous(); //allways at least 1 state per tick
				}
				populateRobit(worldPop[s][m],r);
			}
		}
		
		MapChange m;
		if(this.mapStateIterator.hasPrevious()) {
			m = this.mapStateIterator.previous();
		}
		else {
			Tick<MapChange> temp = this.mapTickIterator.previous();
			this.mapStateIterator = temp.iterator(temp.getStates().size());
			m = this.mapStateIterator.previous();
		}
		
		LinkedList<EnergyDelta> eDeltas = m.getEnergyChanges();
		LinkedList<ObstructionDelta> oDeltas = m.getObstructionChanges();
		
		int[][] eMap = this.attachedWorld.getEnergyMap();
		Obstruction[][] oMap = this.attachedWorld.getObstructionMap();
		
		for(EnergyDelta d : eDeltas) {
			eMap[d.getYpos()][d.getXpos()] = d.getBefore();
		}
		
		for(ObstructionDelta d : oDeltas) {
			oMap[d.getYpos()][d.getXpos()] = d.getBefore();
		}
		
		return false;
	}
	
	public boolean hasPrevoius() {
		if(recordMode)
			throw new IllegalStateException("Cannot read from SimulationRecord, in record mode");
		
		if(this.mapStateIterator.hasPrevious())
			return true;
		else
			return this.mapTickIterator.hasPrevious();
	}
	
	public boolean hasNext() {
		if(recordMode)
			throw new IllegalStateException("Cannot read from SimulationRecord, in record mode");
		if(this.mapStateIterator.hasNext())
			return true;
		else
			return this.mapTickIterator.hasNext();
	}
	
	
	@SuppressWarnings("unchecked")
	public void setRecordingMode(boolean recording) {
		if(!recording) {
			this.mapTickIterator = mapHistory.listIterator();
			this.mapStateIterator = mapTickIterator.next().iterator();
			
			this.robitTickIterators = new ListIterator[population.length][];
			this.robitStateIterators = new ListIterator[population.length][];
			for(int s = 0; s < population.length; s++) {
				this.robitTickIterators[s] = new ListIterator[population[s].length];
				this.robitStateIterators[s] = new ListIterator[population[s].length];
				for(int m = 0; m < this.robitTickIterators[s].length; m++) {
					this.robitTickIterators[s][m]= population[s][m].history.listIterator();
					if(this.robitTickIterators[s][m].hasNext())
						this.robitStateIterators[s][m] = this.robitTickIterators[s][m].next().iterator();
				}
			}
			this.recordMode = false;
		}else {
			this.mapTickIterator = null;
			this.mapStateIterator = null;
			
			this.robitTickIterators = null;
			this.robitStateIterators = null;
			this.recordMode = true;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void recordTick() {
		if(!recordMode)
			throw new IllegalStateException("Cannot write to SimulationRecord, not in record mode");
		this.simulationLength++;
		Robit[][] pop = this.attachedWorld.getPopulation();
		LinkedList<EnergyDelta> eDeltas = (LinkedList<EnergyDelta>) this.attachedWorld.getEnergyChanges().clone();
		LinkedList<ObstructionDelta> oDeltas = (LinkedList<ObstructionDelta>) this.attachedWorld.getObstrucitonChanges().clone();
		
		for(int s = 0; s < pop.length; s++) {
			for(int m = 0; m < pop[s].length; m++) {
				this.population[s][m].recordTick(pop[s][m]);
			}
		}
		Tick<MapChange> t = new Tick<>();
		t.recordState(new MapChange(eDeltas,oDeltas));
		mapHistory.addLast(t);	
	}
	
	public void recordState() {
		if(!recordMode)
			throw new IllegalStateException("Cannot write to SimulationRecord, not in record mode");

		Robit[][] pop = this.attachedWorld.getPopulation();
		LinkedList<EnergyDelta> eDeltas = this.attachedWorld.getEnergyChanges();
		LinkedList<ObstructionDelta> oDeltas = this.attachedWorld.getObstrucitonChanges();
		for(int s = 0; s < pop.length; s++) {
			for(int m = 0; m < pop[s].length; m++) {
				this.population[s][m].recordState(pop[s][m]);
			}
		}
		mapHistory.getLast().recordState(new MapChange(eDeltas,oDeltas));

	}
	
	private boolean checkPopulationSizes(Robit[][] r) {
		if(r == null || r.length == 0 || r.length != this.population.length)
			return false;
		
		for(int s = 0; s < r.length; s++) 
			if(r[s].length != this.population[s].length)
				return false;
		
		return true;
	}
	
	public String getSpecies(int speciesIndex, int memberIndex) {
		return this.population[speciesIndex][memberIndex].getSpecies();
	}
	
	private void populateRobit(Robit r, RobitState p) {
		r.setHealth(p.getHealth(), this.SECURE_KEY);
		r.setEnergy(p.getEnergy(),this.SECURE_KEY);
		r.setAttackBuff(p.getAttackBuff(),this.SECURE_KEY);
		r.setDefenceBuff(p.getDefenceBuff(),this.SECURE_KEY);
		r.setSenseBuff(p.getSenseBuff(), this.SECURE_KEY);
		
		r.setLastAction(p.getActionTaken(),this.SECURE_KEY);
		r.setColor(p.getColor());
		r.setIsDead(p.getIsDead(), this.SECURE_KEY);
		r.setXpos(p.getXpos(),this.SECURE_KEY);
		r.setYpos(p.getYpos(),this.SECURE_KEY);
		r.setScore(p.getScore(), SECURE_KEY);
	}
	
	public void readyForSerialization() {
		this.attachedWorld = null;
	}
	
	public int getSimLength() {
		return this.simulationLength;
	}
	
	public int getWorldSize() {
		return this.worldSize;
	}
	
	public String getKey() {
		return this.SECURE_KEY;
	}
	
	public RobitPlaceholder[][] getPopulation(){
		return this.population;
	}
	
	public Obstruction[][] getStartingObstrucitons(){
		return this.startingObstrucitonMap;
	}
	
	public int[][] getStartingEnergy(){
		return this.startingEnergyMap;
	}
	
	
	
}
