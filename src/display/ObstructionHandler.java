package display;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

import entities.ObstructionEnt;
import worldData.Obstruction;
import worldData.ObstructionType;
import worldData.World;

public class ObstructionHandler {
	public static ObstructionEnt[][] populateObstructions(World sim, AssetManager assetManager, ObstructionEnt[][] obstructions, Node rootNode) {
		int stageSize = sim.WORLD_SIZE;
		
		Obstruction[][] oMap = sim.getObstructionMap();
		obstructions = new ObstructionEnt[oMap.length][];
		for(int z = 0; z < oMap.length; z++) {
			obstructions[z] = new ObstructionEnt[oMap[0].length];
			for(int x = 0; x < oMap[0].length; x++) {
				makeNew(assetManager,stageSize,oMap[z][x],obstructions[z][x],rootNode);
			}
		}
		
		return obstructions;
	}
	
	public static void updateObstructions(World sim, AssetManager assetManager, ObstructionEnt[][] obstructions, Node rootNode) {
		Obstruction[][] oMap = sim.getObstructionMap();
		
		
		for(int z = 0; z < oMap.length; z++) {
			for (int x = 0; x < oMap[0].length; x++) {
				//if there is a new obstruction at that location
				if(obstructions[z][x] == null && oMap[z][x].getType() != ObstructionType.EMPTY) {
					obstructions[z][x] = makeNew(assetManager, sim.WORLD_SIZE,oMap[z][x],obstructions[z][x],rootNode);
				}
				//there is allready one there but its not what we have
				else if(obstructions[z][x] != null && oMap[z][x].getType() != obstructions[z][x].getObstruction().getType()) {
					obstructions[z][x] = makeNew(assetManager, sim.WORLD_SIZE,oMap[z][x],obstructions[z][x],rootNode);
				}
				//there is an entitiy but not but the slot is empty
				else if(obstructions[z][x] != null && oMap[z][x].isEmpty()) {
					obstructions[z][x] = null;
				}
			}
		}
	}
	
	private static ObstructionEnt makeNew(AssetManager assetManager, final int stageSize, Obstruction od,ObstructionEnt oe, Node rootNode) {
		if(od.isEmpty()) {
			oe = null;
		}
		else {
			Mesh bigCube = new Box(1,1,1);
			Mesh smallCube = new Box(0.25f,0.25f,0.25f);
			Mesh bigSphere = new Sphere(10,10,.5f);
			Mesh selected = null;
			
			Node n = new Node();
			if(od.getType() == ObstructionType.WALL) 
				selected = smallCube;
			else if(od.getType() == ObstructionType.PEDESTAL) 
				selected = bigCube;
			else if(od.getType() == ObstructionType.CORPSE) {
				selected = bigSphere;
			}
			
			oe = new ObstructionEnt(
					assetManager,
					selected,
					od,
					"Common/MatDefs/Misc/Unshaded.j3md",
					n, stageSize);
			n.setLocalTranslation(oe.getCurLocation());
			
			rootNode.attachChild(oe.getNode());
			
		}
		return oe;
	}
}
