package entities;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.BatchHint;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.control.BillboardControl.Alignment;
import com.jme3.scene.shape.Sphere;

import action.Action;
import display.ColorConverter;
import effects.ActionEffectManager;
import effects.HealthIndicator;
import effects.ShoutText;
import robits.Robit;

//doesnt extend Robit. This is because the worldData is made to be independant of the display data. This class will
//not be used by the World class so it is kept seperate from 
public class RobitEnt extends BasicEntity{
	private final String TEXTURE_LOCAITON;
	
	//TODO make robit fly height a static constant
	private int maxAnimTime;
	
	private Robit robit;
	private boolean robitIsDead;

	private Vector3f lastLocation;
	private Vector3f nextLocation;
	private long startMoveTime;
	private long endMoveTime;
	
	private Node billboard;
	private BillboardControl billboardControl;
	
	private ShoutText shoutText;
	private boolean showingText = false;
	private HealthIndicator healthIndicator;
	
	private ActionEffectManager fxManager;
	
	int lastTick = -1;
	
	AssetManager asset;
	
	public RobitEnt(AssetManager a, Mesh m, Robit robit, String textureLocation, Node node) {
		super(node, robit.getWorldSize());
		
		fxManager = new ActionEffectManager(a, node, robit);
		
		this.asset = a;
		
		this.TEXTURE_LOCAITON = textureLocation;
		
		this.robit = robit;
		this.robitIsDead = robit.getIsDead();
		
		this.lastLocation = new Vector3f(robit.getXpos(),0.5f,robit.getYpos());
		this.curLocation = new Vector3f(robit.getXpos(),0.5f,robit.getYpos());
		this.nextLocation = new Vector3f(robit.getXpos(),0.5f,robit.getYpos());
		
		if(m == null)
			m = new Sphere(10,10,0.3f);
		
		Geometry g = new Geometry("creatureModel",m);
		Material t = new Material(a, TEXTURE_LOCAITON);
		
		ColorRGBA c = ColorConverter.convertToColorRGBA(robit.getColor());
		t.setColor("Color", c);
		//c = ColorConverter.convertToColorRGBA(robit.getGlowColor());
		//t.setColor("GlowColor", c);
		
		g.setMaterial(t);
	
		this.material = t;
		this.geometry = g;
		this.mesh = m;
		
		this.isMoving = false;
		
		billboard = new Node("Billboard");
        billboardControl = new BillboardControl();
        billboardControl.setAlignment(Alignment.Screen);
        billboard.addControl(billboardControl);
        billboard.setShadowMode(ShadowMode.Off);
        billboard.setBatchHint(BatchHint.Never);
        billboard.setLocalTranslation(0, 1, 0);
        node.attachChild(billboard);
		
		healthIndicator = new HealthIndicator(a);
		billboard.attachChild(healthIndicator.getSpatial());
		
		shoutText = new ShoutText(a);
	}

	public boolean update(int maxAnimTime, int tickNum) {
		this.maxAnimTime = maxAnimTime;
		
		doMove();
		
		updateModel();

		animateAction(maxAnimTime, tickNum);
		
		return robit.getIsDead();
	}
	
	private void animateAction(int maxAnimTime, int tickNum) {
		if(tickNum != this.lastTick) {
			this.lastTick = tickNum;
			Action action = this.robit.getLastAction();
			fxManager.animateAction(action, System.currentTimeMillis(), maxAnimTime, this.robit.getAttacksBeforeContact());
		}
		fxManager.update();
	}
	
	private void updateModel() {
		//if(this.robitIsDead)
		
		String toSay = this.robit.getShoutText();
		if(toSay.equals("") && showingText) {
			billboard.detachChild(shoutText.getNode());
			showingText = false;
		}
		if(!toSay.equals("") && !showingText) {
			billboard.attachChild(shoutText.getNode());
			showingText = true;
		}
		
		if(showingText)
			this.shoutText.setText(this.robit.getShoutText());
		
		
		this.healthIndicator.setLevel(robit.getHealth()/(float)robit.getMaxHealth());
		
		if(this.robit.getIsDead()) {
			this.material.setColor("GlowColor",ColorRGBA.Black);
			this.material.setColor("Color",ColorRGBA.Gray);
		}
		else {
			ColorRGBA c = ColorConverter.convertToColorRGBA(robit.getColor());
			this.material.setColor("Color",c);
		}
	}
	
	private void doMove() {
		curLocation.set(node.getLocalTranslation());
		
		if((curLocation.getX() != (float)robit.getXpos() || curLocation.getZ() != (float)robit.getYpos() || robitIsDead != robit.getIsDead()) && !isMoving) {
			isMoving = true;
			
			lastLocation.set(curLocation);
	
			nextLocation.setX((float)robit.getXpos());
			nextLocation.setZ((float)robit.getYpos());
			if(robit.getIsDead())
				nextLocation.setY(0);
			else
				nextLocation.setY(.5f);
			
			startMoveTime = System.currentTimeMillis();
			endMoveTime = startMoveTime + maxAnimTime;
		}
		else if (curLocation.getX() == (float)robit.getXpos() && 
				curLocation.getY() == nextLocation.getY() && 
				curLocation.getZ() == (float)robit.getYpos() && isMoving) {
			isMoving = false;
			curLocation.set(nextLocation);
			lastLocation.set(nextLocation);
		}
		
		if(isMoving)
			scaleMove(startMoveTime, endMoveTime,lastLocation,nextLocation);
		
	}
	
	public boolean getIsDead() {
		return (this.robit.getIsDead() && curLocation.equals(nextLocation));
	}
	

	public Vector3f getLocation() {
		return this.curLocation;
	}
	
}
