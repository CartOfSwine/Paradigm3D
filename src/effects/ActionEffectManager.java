package effects;

import java.util.ArrayList;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import action.Action;
import action.CordModifier;
import robits.Robit;

/*TODO 
 * Secondly, use that number of attack effects in here to show the appropriate laser effect
 * 
 * Thirdly, adjust replay storage and the RobitPlaceholer to keep track of the number of attacks taken
 * 
 * Lastly, set up the RobitEnt object to utilize this class. Just set it to call update each frame and 
 * 	call animateAction whenever there is a new game tick
*/
public class ActionEffectManager {
	private AssetManager asset;
	private Node controlNode;
	private Robit robit;
	
	private ArrayList<BeamLaser> redLasers;
	private ArrayList<BeamLaser> yellowLasers;
	private Aura attackAura;
	private Aura defenceAura;
	private Aura senseAura;
	
	public ActionEffectManager(AssetManager asset, Node robitNode, Robit robit) {
		this.asset = asset;
		this.controlNode = robitNode;
		this.robit = robit;
		
		redLasers = new ArrayList<>();
		yellowLasers = new ArrayList<>();
		
		attackAura = new Aura(asset, ColorRGBA.Red, ColorRGBA.Black, 0, 0, 0, 200);
		defenceAura = new Aura(asset, ColorRGBA.Blue, ColorRGBA.Black, 0, 0, 0, 90);
		senseAura = new Aura(asset, ColorRGBA.Green, ColorRGBA.Black, 0, 0, 0, 100);
	}
	
	public void update() {
		updateLasers(redLasers);
		updateLasers(yellowLasers);
		
		setAttachment(attackAura, attackAura.update(robit.getAttackBuff()));
		setAttachment(defenceAura, defenceAura.update(robit.getDefenceBuff()));
		setAttachment(senseAura, senseAura.update(robit.getSenseBuff()));
	}
	
	public void animateAction(Action action, long startTime, long duration, int attacksBeforeContact) {
		if(action != null) {
			if(action.isAttack()) {
				resetLasers(redLasers, action.attackAOE.locations, ColorRGBA.Red, startTime, duration, action.singleTarget,attacksBeforeContact);
			}
			if(action.isEat()) {
				resetLasers(yellowLasers, action.eatAOE.locations, ColorRGBA.Yellow, startTime, duration,false,0);
			}
			if(action.isFocus()) {
				attackAura.reset(startTime, duration);
			}
			if(action.isDefence()) {
				defenceAura.reset(startTime, duration);
			}
			if(action.isSense()) {
				senseAura.reset(startTime, duration);
			}
		}
	}
	
	private void updateLasers(ArrayList<BeamLaser> lasers) {
		for(BeamLaser laser : lasers) {
			setAttachment(laser, laser.update());
		}
	}
	
	private void setAttachment(ActionEffect effect, boolean state) {
		if(state) {
			if(effect.getIsActive()) {
				this.controlNode.detachChild(effect.getNode());
				effect.setIsActive(false);
			}
		}
		else {
			if(!effect.getIsActive()) {
				this.controlNode.attachChild(effect.getNode());
				effect.setIsActive(true);
			}
		}
	}
	
	private void resetLasers(ArrayList<BeamLaser> lasers, CordModifier[] locations, ColorRGBA color, long startTime, long duration, boolean singleTarget, int numLasers) {
		int numLasersNeeded = locations.length;
		
		long laserDuration = duration;
		long timeStep = 0;
		if(singleTarget) {
			numLasersNeeded = numLasers;
			timeStep = duration/numLasersNeeded;
			laserDuration = timeStep;
		}
		
		int numLasersPresent = lasers.size();
		
		int i;
		for(i = 0; i < numLasersPresent; i++) {
			float destX = locations[i].xMod;
			float destZ = locations[i].yMod;
			
			Vector3f destination = new Vector3f(destX,-.5f,destZ);
			lasers.get(i).reset(destination, startTime, laserDuration);
			startTime += timeStep;
		}
		while(i < numLasersNeeded) {
			float destX = locations[i].xMod;
			float destZ = locations[i].yMod;
			
			Vector3f destination = new Vector3f(destX,-.5f,destZ);
			
			lasers.add(new BeamLaser(asset, color, 
					ColorRGBA.Black, 
					destination, startTime, laserDuration));
			i++;
			startTime += timeStep;
		}
	}
	
	
}
