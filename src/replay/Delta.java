package replay;

import java.io.Serializable;

public abstract class Delta implements Serializable{
	private static final long serialVersionUID = 1L;
	protected final int xPos;
	protected final int yPos;
	
	public Delta(int x, int y) {
		this.xPos = x;
		this.yPos = y;
	}
	
	public int getXpos() {
		return this.xPos;
	}
	
	public int getYpos() {
		return this.yPos;
	}
}
