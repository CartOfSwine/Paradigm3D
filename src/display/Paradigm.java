package display;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.scene.Geometry;

import org.lwjgl.opengl.GL11;
import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.terrain.geomipmap.TerrainQuad;
import entities.*;
import playerMinds.*;
import replay.SimulationRecord;
import worldData.World;


public class Paradigm extends SimpleApplication {
	private World sim1;
	
	//MUST BE A POWER OF 2!!!!!!!
	private final static int stageSize = 64;
	
	private final static int STEP_TIME = 500;
	
	private static boolean simRunning = true;
	private static boolean simOver = false;
	
	private FilterPostProcessor fpp;
	private BloomFilter bloom;
	
	private RobitEnt[][] robits;
	private ObstructionEnt[][] obstructions;
	
	@SuppressWarnings("unused")
	private Geometry stage;
	private TerrainQuad resources;

	
	public static void main(String args[]) {
		MindTemplate[] contenders = new MindTemplate[] {
				new ExampleGrazer(),
				new ExampleHunter(),
				new Destroyer(),
				new SightTest(),
				new TestRekr()
		};
		//define how many of each creature type will spawn
		int[] populations = new int[] {
				50,
				30,
				30,
				0,
				0
		};
		
		World preRecord = new World(stageSize,0,contenders, "Password");
		preRecord.initialize(populations);
		//for (int tick = 0; tick < 500; tick++) {
		//	preRecord.tick();
		//}
		//SimulationRecord s = preRecord.getSimulationRecord();
		//s.setRecordingMode(false);
		
		Paradigm app = new Paradigm(preRecord);
        app.start(); // start the game
        
        try {
			Thread.sleep(2000); //give graphics a chance to get ready
		} catch (InterruptedException e) {return;}
        
        while(!simOver) {
        	while(simRunning) {
        		app.simUpdate();

        		try {
					Thread.sleep(STEP_TIME);
				} catch (InterruptedException e) {return;}
        		
        	}
        	try {
				Thread.sleep(20);
			} catch (InterruptedException e) {return;}
        }
	}
	
	public Paradigm(SimulationRecord r) {
		sim1 = new World(r);
	}
	
	
	public Paradigm(World w) {
		//set up two mind objects for the opposing players
		
        
        //create the simulation with the players
	    sim1 = w;
	    //initialize the sim with 10 creatures for each player

	    
	}
	
	public void simUpdate() {
		if(!simOver){
			sim1.tick();
	    }
	}
	
	public void simRender() {

		RobitHandler.updateRobits(robits, STEP_TIME, rootNode,sim1.getStepNum());
		
		ObstructionHandler.updateObstructions(sim1,assetManager, obstructions, rootNode);
		
		EnergyHandler.updateEnergys(sim1, resources);
		
	}
	
	@Override
    public void simpleInitApp() {
		if(!checkStageSize((float)stageSize))
			throw new IllegalStateException("Please Change stageSize to a power of 2");
		
		flyCam.setMoveSpeed(40); 

		GL11.glEnable(GL11.GL_CULL_FACE);
		
		//Create the stage
		stage = StageManager.createStage(assetManager, stageSize,rootNode);
		
		//create robit entities
		robits = RobitHandler.populateRobits(sim1, assetManager, robits,rootNode);
		
		//create obstruction entites
		obstructions = ObstructionHandler.populateObstructions(sim1, assetManager, rootNode);
		
		//create resource map
		resources = EnergyHandler.createEnergys(sim1, assetManager, resources, rootNode);
		
		//create the sun
		DirectionalLight sun = new DirectionalLight();
        Vector3f lightDir = new Vector3f(-0.12f, -0.3729129f, 0.74847335f);
        sun.setDirection(lightDir);
        sun.setColor(ColorRGBA.White.clone().multLocal(2));
        addLight(sun);
        
        //create bloom effect
        fpp = new FilterPostProcessor(assetManager);
        bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        bloom.setDownSamplingFactor(.75f);
        bloom.setBlurScale(1.5f);
        //fpp.addFilter(bloom);
        //Vector3f lightPos = lightDir.multLocal(-3000);
        //LightScatteringFilter filter = new LightScatteringFilter(lightPos);
        //fpp.addFilter(filter);
        //fpp.addFilter(bloom);
        //viewPort.addProcessor(fpp);
        
        
        
        //rig up everything

        viewPort.addProcessor(fpp);
    }
	
	private void addLight(DirectionalLight sun) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    public void simpleUpdate(float tpf) {
        simRender();
    }
	
	@Override
	public void destroy(){
		simRunning = false;
		simOver = true;
		super.destroy();
	}
	
	private boolean checkStageSize(float size) {
		if(size < 2) return false;
		if(size == 2) return true;
		return checkStageSize(size/2);
	}
}
