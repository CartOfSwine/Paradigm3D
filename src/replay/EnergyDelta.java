package replay;

public class EnergyDelta extends Delta {
	private final int beforeValue;
	private final int afterValue;
	
	public EnergyDelta(int x, int y, int before, int after) {
		super(x, y);
		this.beforeValue = before;
		this.afterValue = after;
	}

	public int getBefore() {
		return this.beforeValue;
	}
	
	public int getAfter() {
		return this.afterValue;
	}
}
