package entities;

import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;

public abstract class BasicEntity {
	protected Geometry geometry;
	protected Mesh mesh;
	protected Material material;
	protected Node node;
	
	public BasicEntity(Node node) {
		this.node = node;
		this.geometry = null;
		this.mesh = null;
		this.material = null;
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
}
