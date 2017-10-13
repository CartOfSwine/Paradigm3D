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
	private int maxAnimTime;
	
	private Robit robit;
	private boolean robitIsDead;

	private Vector3f lastLocation;
	private Vector3f nextLocation;
	private long startMoveTime;
	private long endMoveTime;
	
	public RobitEnt(AssetManager a, Mesh m, Robit robit, String textureLocation, Node node) {
		super(node, robit.getWorldSize());
		
		this.TEXTURE_LOCAITON = textureLocation;
		
		this.robit = robit;
		this.robitIsDead = robit.getIsDead();
		
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

	public boolean update(int maxAnimTime) {
		this.maxAnimTime = maxAnimTime;
		
		doMove();
		
		updateModel();
		
		robitIsDead = robit.getIsDead();
		return robitIsDead;
	}
	
	private void updateModel() {
		if(this.robitIsDead)
		
		if(this.robit.getIsDead()) {
			this.material.setColor("GlowColor",ColorRGBA.Black);
			this.material.setColor("Color",ColorRGBA.Gray);
		}
		else {
			this.material.setColor("GlowColor",robit.getGlowColor());
		}
	}
	
	private void doMove() {
		curLocation.set(node.getLocalTranslation());
		
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
			scaleMove(startMoveTime, endMoveTime,lastLocation,nextLocation);
		}
	}
	
	public boolean getIsDead() {
		return (robitIsDead && curLocation.equals(nextLocation));
	}
	

	public Vector3f getLocation() {
		return this.curLocation;
	}
	
}
