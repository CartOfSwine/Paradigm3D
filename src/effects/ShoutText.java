package effects;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;

public class ShoutText {
	private AssetManager asset;
	private Node node;
	private BitmapText text;
	private Geometry backroundGeo;
	 
	public ShoutText(AssetManager asset) {
		this.asset = asset;
	}
	
	 public Node getNode() {
		 if(node == null) {
			 node = new Node();
			 
			 text = new BitmapText(asset.loadFont("Interface/Fonts/ShoutText.fnt"), false );
			 text.setSize( .25f );
			 text.setText("");
			 float textWidth = text.getLineWidth() + 20;
			 float textOffset = textWidth / 2;
			 text.setBox( new Rectangle(-textOffset,0f, textWidth, text.getHeight()) );
			 text.setColor( new ColorRGBA( 1, 1, 1, 1 ) );
			 text.setAlignment( BitmapFont.Align.Center );
			 text.setQueueBucket( Bucket.Transparent );
			 text.setLocalTranslation(new Vector3f(0,.495f,0));
			 
			 Quad backround = new Quad(2f,.25f);
			 backroundGeo = new Geometry("backdrop",backround);
			 Material stageMat = new Material(asset,"Common/MatDefs/Misc/Unshaded.j3md");
			 Texture t = asset.loadTexture("Textures/ShoutBox.jpg");
			 stageMat.setTexture("ColorMap", t);
			 backroundGeo.setMaterial(stageMat);
			 backroundGeo.setLocalTranslation(new Vector3f(-1f,.2f,-.1f));
			 
			 node.attachChild(backroundGeo);
			 node.attachChild(text);
		 }
		 return this.node;
	 }
	 
	 public void setText(String t) {
		 if(t != null) {
			 if(!text.getText().equals(t)) {
				 text.setText(t);
				 float textWidth = text.getLineWidth() + 20;
				 float textOffset = textWidth / 2;
				 text.setBox( new Rectangle(-textOffset,0f, textWidth, text.getHeight()));
				 
				 node.detachChild(backroundGeo);
				 Quad backround = new Quad(0.15f * t.length() + .125f,.3f);
				 backroundGeo = new Geometry("backdrop",backround);
				 Material stageMat = new Material(asset,"Common/MatDefs/Misc/Unshaded.j3md");
				 Texture tex = asset.loadTexture("Textures/ShoutBox.jpg");
				 stageMat.setTexture("ColorMap", tex);
				 backroundGeo.setMaterial(stageMat);
				 backroundGeo.setLocalTranslation(new Vector3f(-0.15f * t.length()/2 - .05f,.2f,-.1f));
				 node.attachChild(backroundGeo);
			 }
		 }
	 }
}
