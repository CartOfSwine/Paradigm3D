package replay;

public abstract class Delta {
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
