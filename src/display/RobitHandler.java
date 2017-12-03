package display;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

import entities.RobitEnt;
import robits.Robit;
import worldData.World;

public class RobitHandler {
	public static RobitEnt[][] populateRobits(World sim, AssetManager assetManager, RobitEnt[][] robits, Node rootNode) {
		Mesh robitShape = new Sphere(10,10,0.5f); //reuse the Mesh object
		Robit[][] robitData = sim.getPopulation();
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
				rootNode.attachChild(n);
			}
		}
		return robits;
	}
	
	public static void updateRobits(RobitEnt[][] robits, final int STEP_TIME, Node rootNode) {
		for(int i = 0; i < robits.length; i++) {
			for(int j = 0; j < robits[i].length; j++) {
				if(robits[i][j].update(STEP_TIME)) 
					rootNode.detachChild(robits[i][j].getNode());
			}
		}
	}
}
