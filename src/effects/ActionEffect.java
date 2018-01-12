package effects;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public abstract class ActionEffect {
	protected Node controlNode;
	protected long startTime;
	protected long duration;
	protected long endTime;
	protected Vector3f source;
	protected Vector3f destination;
	
	protected boolean isActive = false;
	
	AssetManager asset;
	
	public ActionEffect(AssetManager asset, long startTime, long duration, Vector3f source, Vector3f destination, Node controlNode){
		this.controlNode = controlNode;
		this.asset = asset;
		this.startTime = startTime;
		this.duration = duration;
		this.endTime = duration + startTime;
		this.source = new Vector3f(source);
		this.destination = new Vector3f(destination);
	}
	
	public abstract Node getNode();
	
	protected boolean readyForTermination(long now) {
		return now > endTime;
	}
	
	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public boolean getIsActive() {
		return this.isActive;
	}

}	
	
