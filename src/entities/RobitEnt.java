package entities;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

import robits.Robit;

//doesnt extend Robit. This is because the worldData is made to be independant of the display data. This class will
//not be used by the World class so it is kept seperate from 
public class RobitEnt extends BasicEntity{
	private final String TEXTURE_LOCAITON;
	
	
	//TODO make robit fly height a static constant
	@SuppressWarnings("unused")
	private int maxAnimTime;
	
	private Robit robit;
	private boolean robitIsDead;
	
	private int worldSize;
	private Vector3f lastLocation;
	private Vector3f curLocation;
	private Vector3f nextLocation;
	private long startMoveTime;
	private long endMoveTime;
	private boolean isMoving;
	
	public RobitEnt(AssetManager a, Mesh m, Robit robit, String textureLocation, Node node) {
		super(node);
		
		this.TEXTURE_LOCAITON = textureLocation;
		
		this.robit = robit;
		this.robitIsDead = robit.getIsDead();
		this.worldSize = robit.getWorldSize();
		
		this.lastLocation = new Vector3f(robit.getXpos(),0.5f,robit.getYpos());
		this.curLocation = new Vector3f(robit.getXpos(),0.5f,robit.getYpos());
		this.nextLocation = new Vector3f(robit.getXpos(),0.5f,robit.getYpos());
		
		if(m == null)
			m = new Sphere(10,10,0.5f);
		
		Geometry g = new Geometry("creatureModel",m);
		Material t = new Material(a, TEXTURE_LOCAITON);
		t.setColor("Color", robit.getColor());
		t.setColor("GlowColor", robit.getGlowColor());
		g.setMaterial(t);
	
		this.material = t;
		this.geometry = g;
		this.mesh = m;
		
		this.isMoving = false;
	}

	public void update(int maxAnimTime) {
		this.maxAnimTime = maxAnimTime;
		
		curLocation.set(node.getLocalTranslation());
		
		//will only trigger before move as started
		if((curLocation.getX() != (float)robit.getXpos() || curLocation.getZ() != (float)robit.getYpos() || robitIsDead != robit.getIsDead()) && !isMoving) {
			isMoving = true;
			
			lastLocation.set(curLocation);
	
			nextLocation.setX((float)robit.getXpos());
			nextLocation.setZ((float)robit.getYpos());
			if(robit.getIsDead())
				nextLocation.setY(0);
			else
				nextLocation.setY(.5f);
			
			startMoveTime = System.currentTimeMillis();
			endMoveTime = System.currentTimeMillis() + maxAnimTime;
		}
		else if (curLocation.getX() == (float)robit.getXpos() && 
				curLocation.getY() == nextLocation.getY() && 
				curLocation.getZ() == (float)robit.getYpos() && isMoving) {
			isMoving = false;
			curLocation.set(nextLocation);
			lastLocation.set(nextLocation);
		}
		
		if(isMoving) {
			long now = System.currentTimeMillis();
			
			float dx = nextLocation.getX() - lastLocation.getX();
			float dy = nextLocation.getY() - lastLocation.getY();
			float dz = nextLocation.getZ() - lastLocation.getZ();
			
			long top = now - startMoveTime;
			long bottom = endMoveTime- startMoveTime;
			float timeScale = top/(float)bottom;

			if(timeScale >= 1) {
				isMoving = false;
				curLocation.set(nextLocation);
				lastLocation.set(nextLocation);
			}
			else {
				curLocation.setX(lastLocation.getX() + dx * timeScale);
				curLocation.setY(lastLocation.getY() + dy * timeScale);
				curLocation.setZ(lastLocation.getZ() + dz * timeScale);
				
			}
			
			//so we dont have random robits whipping across the map
			if(Math.abs(dx) >= worldSize*4/5 || Math.abs(dy) >= worldSize*4/5 || Math.abs(dz) >= worldSize*4/5)
				curLocation.set(nextLocation);
			
		}

		if(this.robit.getIsDead()) {
			this.material.setColor("GlowColor",ColorRGBA.Black);
			this.material.setColor("Color",ColorRGBA.Black);
		}
		else {
			this.material.setColor("GlowColor",robit.getGlowColor());
		}
		
		node.setLocalTranslation(curLocation);
		
	}
	public Node getNode() {
		return this.node;
	}
	public Vector3f getLocation() {
		return this.curLocation;
	}
	
}
