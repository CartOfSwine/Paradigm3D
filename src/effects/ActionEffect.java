package effects;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public abstract class ActionEffect {
	protected Node controlNode;
	protected long startTime;
	@SuppressWarnings("unused")
	protected long duration;
	protected long endTime;
	protected Vector3f source;
	protected Vector3f destination;
	
	
	protected boolean readyForTermination(long now) {
		return now > endTime;
	}

}	
	
