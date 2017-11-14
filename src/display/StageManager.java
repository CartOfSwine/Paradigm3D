package display;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

public class StageManager {
	public static Geometry createStage(AssetManager assetManager, int stageSize, Node rootNode) {
		Quad tile = new Quad(stageSize, stageSize);
		tile.scaleTextureCoordinates(new Vector2f(stageSize,stageSize));
		Node stageNode = new Node();
		
		Sphere s = new Sphere(12,12,5f);
		Geometry sun = new Geometry("sun",s);
		Material sunMat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
		sunMat.setColor("Color", ColorRGBA.Yellow);
		sun.setMaterial(sunMat);
		sun.setLocalTranslation(new Vector3f(stageSize/2,5,stageSize * -2));
		stageNode.attachChild(sun);
		
		Geometry stage = new Geometry("stage",tile);
		Material stageMat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
		Texture t = assetManager.loadTexture("Textures/PurpleTile.jpg");
		
		t.setWrap(WrapMode.Repeat);
		stageMat.setTexture("ColorMap", t);
		
		stage.rotate(3*(float)Math.PI/2, 0, 0);
		stage.setLocalTranslation(new Vector3f(-0.5f,0,stageSize-.5f));
		stage.setMaterial(stageMat);
		stageNode.attachChild(stage);
		
		rootNode.attachChild(stageNode);
		return stage;
	}
}
