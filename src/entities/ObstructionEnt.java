package entities;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;

import worldData.Obstruction;

public class ObstructionEnt extends BasicEntity{

	private final String TEXTURE_LOCAITON;
	
	private Obstruction obstruction;
	private boolean isDestroyed;
	
	public ObstructionEnt(AssetManager a, Mesh m, Obstruction o, String textureLocation, Node node, int worldSize) {
		super(node,worldSize);
		
		this.TEXTURE_LOCAITON = textureLocation;
		this.obstruction = o;
		this.isDestroyed = o.getHP() > 0;
				
		Geometry g = new Geometry("obstructionModel",m);
		Material t = new Material(a, TEXTURE_LOCAITON);
		t.setColor("Color",ColorRGBA.Black);
		g.setMaterial(t);
		
		this.material = t;
		this.geometry = g;
		this.mesh = m;
		
		curLocation = new Vector3f(o.getxPos(),0f,o.getyPos());
		
		this.isMoving = false;
		node.attachChild(g);
	}
 
 	
 	public boolean update() {
 		return updateModel();
 	}
 
 	private boolean updateModel() {
		//TODO check if we are dead, if so change to rubble model and return false isntead
 		
 		isDestroyed = this.obstruction.getHP() >0;
 		return isDestroyed;
	}

	public Obstruction getObstruction() {
		return this.obstruction;
	}
	
}
