package replay;

import java.io.Serializable;

import worldData.Obstruction;
import worldData.ObstructionType;

public class ObstructionDelta extends Delta implements Serializable{
	private static final long serialVersionUID = 1L;
	private final ObstructionType beforeType;
	private final ObstructionType afterType;
	
	private final int beforeHP;
	private final int afterHP;
	
	public ObstructionDelta(int x, int y, ObstructionType beforeType, ObstructionType afterType,
							int beforeHP, int afterHP) {
		super(x, y);
		
		this.beforeType = beforeType;
		this.afterType = afterType;
		this.beforeHP = beforeHP;
		this.afterHP = afterHP;
	}

	public boolean typeChanged() {
		return this.beforeType != this.afterType;
	}
	
	public int getBeforeHP() {
		return this.beforeHP;
	}
	
	public int getAfterHP() {
		return this.afterHP;
	}
	
	public Obstruction getBefore() {
		return new Obstruction(beforeType,xPos,yPos,beforeHP);
	}
	public Obstruction getAfter() {
		return new Obstruction(afterType,xPos,yPos,afterHP);
	}
}
