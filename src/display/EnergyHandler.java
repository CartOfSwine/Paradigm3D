package display;

import java.util.ArrayList;
import java.util.List;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;

import worldData.World;

public class EnergyHandler {
	public static TerrainQuad createEnergys(World sim, AssetManager assetManager, TerrainQuad energys, Node rootNode) {
		int[][] dataEnergy = sim.getEnergyMap();
		int stageSize = sim.WORLD_SIZE;
		float[] energyHeightMap = new float[dataEnergy.length * dataEnergy.length];
		for(int z = 0; z < dataEnergy.length;z++) 
			for (int x = 0; x < dataEnergy[0].length; x++)
				energyHeightMap[z * dataEnergy[0].length + x] = (float)dataEnergy[z][x];
		
		Material energyMat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
		energyMat.setColor("Color", ColorRGBA.Yellow);
		energyMat.setColor("GlowColor", ColorRGBA.Yellow);
		
		energys = new TerrainQuad("energy_map",65,stageSize+1,energyHeightMap);
		energys.setMaterial(energyMat);
		energys.setLocalTranslation(new Vector3f(stageSize/2f,-0.01f,stageSize/2f));
		energys.setLocalScale(1f, .02f, 1f);
		
		Node resNode = new Node();
		resNode.attachChild(energys);
		resNode.setLocalTranslation(0,0,0);
		
		rootNode.attachChild(resNode);
		return energys;
	}
	
	public static void updateEnergys(World sim, TerrainQuad energys) {
		int stageSize = sim.WORLD_SIZE;
		List<Vector2f> changeCords = new ArrayList<>();
		List<Float> changeVals = new ArrayList<>();
		int[][] dataEnergy = sim.getEnergyMap();
		float[] hm = energys.getHeightMap();

		for(int z = 0; z < dataEnergy.length;z++) {
			for (int x = 0; x < dataEnergy[z].length; x++) {
				int d1c = z * (dataEnergy[0].length+1) + x;
				if(hm[d1c] != (float)dataEnergy[z][x]) {
					changeCords.add(new Vector2f((float)x- stageSize/2,(float)z-stageSize/2));
					changeVals.add((float)dataEnergy[z][x]);
				}
			}
		}
		energys.setHeight(changeCords, changeVals);
		energys.updateModelBound();
	}
}
