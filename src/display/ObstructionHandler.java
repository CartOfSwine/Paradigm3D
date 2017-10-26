package display;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;

import entities.ObstructionEnt;
import worldData.Obstruction;
import worldData.ObstructionType;
import worldData.World;

public class ObstructionHandler {
	public static ObstructionEnt[][] populateObstructions(World sim, AssetManager assetManager, Node rootNode) {
		int stageSize = sim.WORLD_SIZE;
		ObstructionEnt[][] obstructions;
		Obstruction[][] oMap = sim.getObstructionMap();
		obstructions = new ObstructionEnt[oMap.length][];
		for(int z = 0; z < oMap.length; z++) {
			obstructions[z] = new ObstructionEnt[oMap[z].length];
			for(int x = 0; x < oMap[z].length; x++) {
				obstructions[z][x] = makeNew(assetManager,stageSize,oMap[z][x],obstructions[z][x],rootNode);
			}
		}
		
		return obstructions;
	}
	
	public static void updateObstructions(World sim, AssetManager assetManager, ObstructionEnt[][] obstructions, Node rootNode) {
		Obstruction[][] oMap = sim.getObstructionMap();
		
		
		for(int z = 0; z < oMap.length; z++) {
			for (int x = 0; x < oMap[0].length; x++) {
				if(obstructions[z][x] != null) {
					if(obstructions[z][x].update()) {
						obstructions[z][x].detach(rootNode);
						obstructions[z][x] = null;
					}
				}
				
				//if there is a new obstruction at that location
				if(obstructions[z][x] == null && oMap[z][x].getType() != ObstructionType.EMPTY) {
					obstructions[z][x] = makeNew(assetManager, sim.WORLD_SIZE,oMap[z][x],obstructions[z][x],rootNode);
				}
				//there is an entitiy but the slot is empty
				else if(obstructions[z][x] != null && oMap[z][x].isEmpty()) {
					obstructions[z][x].detach(rootNode);
					obstructions[z][x] = null;
				}
				//there is allready one there but its not what we have
				else if(obstructions[z][x] != null && oMap[z][x].getType() != obstructions[z][x].getObstruction().getType()) {
					obstructions[z][x] = makeNew(assetManager, sim.WORLD_SIZE,oMap[z][x],obstructions[z][x],rootNode);
				}
				
				
			}
		}
	}
	
	private static ObstructionEnt makeNew(AssetManager assetManager, final int stageSize, Obstruction od,ObstructionEnt oe, Node rootNode) {
		if(od.isEmpty()) {
			oe = null;
		}
		else {
			Mesh selected = null;
			
			Node n = new Node();
			if(od.getType() == ObstructionType.WALL) { 
				selected = new Box(.5f,.5f,.5f);
			}
			else if(od.getType() == ObstructionType.PEDESTAL) 
				selected = new Box(0.25f,0.25f,0.25f);
			else if(od.getType() == ObstructionType.CORPSE) 
				selected = new Sphere(10,10,.5f);
			else if(od.getType() == ObstructionType.PILLAR) 
				selected = new Cylinder(10,10,0.3f,1f,true);
			else if(od.getType() == ObstructionType.FENCE) 
				selected = new Box(.5f,.5f,.5f);
			
			
			oe = new ObstructionEnt(
					assetManager,
					selected,
					od,
					"Common/MatDefs/Misc/Unshaded.j3md",
					n, stageSize);
			oe.getGeometry().setLocalTranslation(0f,.5f,0f);
			
			n.setLocalTranslation(oe.getCurLocation());
			
			rootNode.attachChild(oe.getNode());
			
		}
		return oe;
	}
}
