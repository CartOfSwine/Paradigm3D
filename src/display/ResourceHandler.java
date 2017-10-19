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

public class ResourceHandler {
	public static TerrainQuad createResources(World sim, AssetManager assetManager, TerrainQuad resources, Node rootNode) {
		int[][] dataResource = sim.getResourceMap();
		int stageSize = sim.WORLD_SIZE;
		float[] resourceHeightMap = new float[dataResource.length * dataResource.length];
		for(int z = 0; z < dataResource.length;z++) 
			for (int x = 0; x < dataResource[0].length; x++)
				resourceHeightMap[z * dataResource[0].length + x] = (float)dataResource[z][x];
		
		Material resourceMat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
		resourceMat.setColor("Color", ColorRGBA.Yellow);
		resourceMat.setColor("GlowColor", ColorRGBA.Yellow);
		
		resources = new TerrainQuad("resource_map",65,stageSize+1,resourceHeightMap);
		resources.setMaterial(resourceMat);
		resources.setLocalTranslation(new Vector3f(stageSize/2f,-0.01f,stageSize/2f));
		resources.setLocalScale(1f, .02f, 1f);
		
		Node resNode = new Node();
		resNode.attachChild(resources);
		resNode.setLocalTranslation(0,0,0);
		
		rootNode.attachChild(resNode);
		return resources;
	}
	
	public static void updateResources(World sim, TerrainQuad resources) {
		int stageSize = sim.WORLD_SIZE;
		List<Vector2f> changeCords = new ArrayList<>();
		List<Float> changeVals = new ArrayList<>();
		int[][] dataResource = sim.getResourceMap();
		float[] hm = resources.getHeightMap();

		for(int z = 0; z < dataResource.length;z++) {
			for (int x = 0; x < dataResource[z].length; x++) {
				int d1c = z * (dataResource[0].length+1) + x;
				if(hm[d1c] != (float)dataResource[z][x]) {
					changeCords.add(new Vector2f((float)x- stageSize/2,(float)z-stageSize/2));
					changeVals.add((float)dataResource[z][x]);
				}
			}
		}
		resources.setHeight(changeCords, changeVals);
		resources.updateModelBound();
	}
}
