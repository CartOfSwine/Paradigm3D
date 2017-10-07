package display;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

import entities.*;
//import all robits so we end up with all the competing AIs
import robits.*;
import worldData.World;


public class Paradigm extends SimpleApplication {
	private World sim1;
	
	//MUST BE A POWER OF 2!!!!!!!
	private final int stageSize = 128;
	
	private final static int STEP_TIME = 50;
	
	private static boolean simRunning = true;
	private static boolean simOver = false;
	
	private FilterPostProcessor fpp;
	private BloomFilter bloom;
	
	private RobitEnt[][] robits;
	
	private Node resNode;
	private TerrainQuad resources;
	private Material resourceMat;
	
	public static void main(String args[]) {
		
		Paradigm app = new Paradigm();
        app.start(); // start the game
        
        //TODO refer to this from a nonstatic context
        //TODO make STEP_TIME a nonstatic member of paradigm and set it in the constructor
        
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
	
	public Paradigm() {
		//set up two mind objects for the opposing players
		MindTemplate[] contenders = new MindTemplate[] {
				new ExampleGrazer(),
				new ExampleHunter()
		};
		//define how many of each creature type will spawn
		int[] populations = new int[] {
				100,
				50
		};
        
        //create the simulation with the players
	    sim1 = new World(this.stageSize,0,contenders);
	    //initialize the sim with 10 creatures for each player
	    sim1.initialize(populations);
	    
	}
	
	public void simUpdate() {
		if(!simOver){
			sim1.tick();
	    }
	}
	
	public void simRender() {

		for(int i = 0; i < robits.length; i++) {
			for(int j = 0; j < robits[i].length; j++) {
				robits[i][j].update(STEP_TIME);
			}
		}
		
		List<Vector2f> changeCords = new ArrayList<>();
		List<Float> changeVals = new ArrayList<>();
		
		int[][] dataResource = sim1.getResourceMap();
		float[] hm = resources.getHeightMap();

		for(int z = 0; z < dataResource.length;z++) {
			for (int x = 0; x < dataResource[z].length; x++) {
				int d1c = z * (dataResource[0].length+1) + x;
				if(hm[d1c] != (float)dataResource[z][x]) {
					changeCords.add(new Vector2f((float)x- stageSize/2,(float)z-stageSize/2));
					changeVals.add((float)dataResource[z][x]);
					//changeCords.add(new Vector2f(1f,1f));
					//changeVals.add(200f);
				}
			}
		}
		//changeCords.add(new Vector2f(2f,2f));
		//changeVals.add(500f);
		//resources.setLocked(false);
		resources.setHeight(changeCords, changeVals);
		resources.updateModelBound();
		
		hm = resources.getHeightMap();
		//resNode.updateModelBound();
		
		/*rootNode.detachChild(resources);
		int[][] dataResource = sim1.getResourceMap();
		float[] resourceHeightMap = new float[dataResource.length * dataResource.length];
		for(int i = 0; i < dataResource.length;i++) 
			for (int j = 0; j < dataResource[i].length; j++)
				resourceHeightMap[i * dataResource[i].length + j] = (float)dataResource[i][j];
		resources = new TerrainQuad("resource_map",65,stageSize+1,resourceHeightMap);
		resources.setMaterial(resourceMat);
		resources.setLocalTranslation(new Vector3f(stageSize/2-.5f,-0.1f,stageSize/2-.5f));
		resources.setLocalScale(1f, .05f, 1f);
		rootNode.attachChild(resources);*/
	}
	
	@Override
    public void simpleInitApp() {
		flyCam.setMoveSpeed(40); 
		//flyCam.setLocation(new Vector3f(0,0,0));
		GL11.glEnable(GL11.GL_CULL_FACE);
		
		//Create the stage
		Quad tile = new Quad(stageSize, stageSize); // tile shape
		tile.scaleTextureCoordinates(new Vector2f(stageSize,stageSize));
		
		Geometry stage = new Geometry("stage",tile);
		Material stageMat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
		Texture t = assetManager.loadTexture("Textures/metal-tiles.jpg");
		
		t.setWrap(WrapMode.Repeat);
		stageMat.setTexture("ColorMap", t);
		
		stage.rotate(3*(float)Math.PI/2, 0, 0);
		stage.setLocalTranslation(new Vector3f(0,0,stageSize));
		stage.setMaterial(stageMat);
		
		
		
		//generate robits
		Mesh robitShape = new Sphere(10,10,0.5f); //reuse the Mesh object
		Robit[][] robitData = sim1.getPopulation();
		robits = new RobitEnt[robitData.length][];
		
		for(int i = 0; i < robitData.length; i++) {
			robits[i] = new RobitEnt[robitData[i].length];
			for(int j = 0; j < robitData[i].length; j++) {
				Node n = new Node();
				robits[i][j] = new RobitEnt(
						assetManager,
						robitShape,
						robitData[i][j],
						"Common/MatDefs/Misc/Unshaded.j3md", n);
				n.attachChild(robits[i][j].getGeometry());
			}
		}
		
		//create resource map
		int[][] dataResource = sim1.getResourceMap();
		float[] resourceHeightMap = new float[dataResource.length * dataResource.length];
		for(int z = 0; z < dataResource.length;z++) 
			for (int x = 0; x < dataResource[0].length; x++)
				resourceHeightMap[z * dataResource[0].length + x] = (float)dataResource[z][x];
		resourceMat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
		resourceMat.setColor("Color", ColorRGBA.Yellow);
		resourceMat.setColor("GlowColor", ColorRGBA.Yellow);
		
		resources = new TerrainQuad("resource_map",stageSize+1,stageSize+1,resourceHeightMap);
		resources.setMaterial(resourceMat);
		resources.setLocalTranslation(new Vector3f(stageSize/2f,-0.01f,stageSize/2f));
		resources.setLocalScale(1f, .01f, 1f);
		
		resNode = new Node();
		resNode.attachChild(resources);
		
		//create the sun
		DirectionalLight sun = new DirectionalLight();
        sun.setDirection(toStageCords(0,0,0));
        
        
        //create bloom effect
        fpp = new FilterPostProcessor(assetManager);
        bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        bloom.setDownSamplingFactor(2.0f);
        fpp.addFilter(bloom);
        
        
        //rig up everything
        rootNode.attachChild(stage);
        for(int i = 0; i < robits.length; i++) {
			for(int j = 0; j < robits[i].length; j++) {
				rootNode.attachChild(robits[i][j].getNode());
			}
		}
        rootNode.attachChild(resNode);
        
        rootNode.addLight(sun);
        
        
        viewPort.addProcessor(fpp);
    }
	
	private Vector3f toStageCords(float x, float y, float z) {
		return new Vector3f(
				x - this.stageSize/2,
				y - this.stageSize,
				z -  this.stageSize/2);
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
}
