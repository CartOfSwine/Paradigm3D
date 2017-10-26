package entities;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;

public abstract class BasicEntity {
	protected Geometry geometry;
	protected Mesh mesh;
	protected Material material;
	protected Node node;
	protected Vector3f curLocation;
	protected boolean isMoving;
	protected final int WORLD_SIZE;
	
	public BasicEntity(Node node, int worldSize) {
		this.node = node;
		this.geometry = null;
		this.mesh = null;
		this.material = null;
		this.WORLD_SIZE = worldSize;
	}
	
	public void scaleMove(long lastTime, long nextTime, Vector3f lastLocation, Vector3f nextLocation) {
		long now = System.currentTimeMillis();
		
		float dx = nextLocation.getX() - lastLocation.getX();
		float dy = nextLocation.getY() - lastLocation.getY();
		float dz = nextLocation.getZ() - lastLocation.getZ();
		
		long top = now - lastTime;
		long bottom = nextTime- lastTime;
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
		if(Math.abs(dx) >= WORLD_SIZE*4/5 || Math.abs(dy) >= WORLD_SIZE*4/5 || Math.abs(dz) >= WORLD_SIZE*4/5)
			curLocation.set(nextLocation);
		
		node.setLocalTranslation(curLocation);
	}
	
	public void detach(Node rootNode) {
		this.node.detachAllChildren();
		rootNode.detachChild(this.node);
	}
	
	public Geometry getGeometry() {
		return geometry;
	}

	public void setModel(Geometry g) {
		this.geometry = g;
	}

	public Mesh getMesh() {
		return mesh;
	}

	public void setModelShape(Mesh m) {
		this.mesh = m;
	}

	public Material getTexture() {
		return material;
	}

	public void setTexture(Material m) {
		this.material = m;
	}
	
	public Node getNode() {
		return this.node;
	}
	
	public Vector3f getCurLocation() {
		return this.curLocation;
	}
}
