package worldData;

public class Prefab implements Comparable<Prefab>{
	//0 = empty, 1 = pedestal, 2 = wall, 3 = pillar

	public final int[][] rMap;
	public final Obstruction[][] oMap;
	@SuppressWarnings("unused")
	private final int[][] oMapEncoded;
	public final int sizeCategory;
	public final int maxX;
	public final int maxY;
	
	public Prefab(int[][] rMap,int[][]oMapEncoded){
		this.rMap = rMap;
		this.oMapEncoded = oMapEncoded;
		this.oMap = new Obstruction[rMap.length][rMap[0].length];
		decodeOMap(oMapEncoded, oMap);
		
		maxY = oMap.length;
		int maxLen = maxY;
		
		int maxXtemp = 0;
		for(int i = 0; i < oMap.length; i++) {
			if(oMap[i].length > maxXtemp)maxXtemp = oMap[i].length;
		}
		
		maxX = maxXtemp;
		if(maxX > maxLen) maxLen = maxX;
		
		if(maxY == 18)
			System.out.println("here boi");
		
		sizeCategory = calcSizeCat((double)maxLen);
	}
	
	private int calcSizeCat(double d) {
		if(d <= 4) return 0;
		return 1 + calcSizeCat(Math.ceil(d/2.0));
	}
	
	private Obstruction[][] decodeOMap(int[][] oMapE, Obstruction[][] oMap) {
		for(int y = 0; y < oMapE.length; y++) {
			for(int x = 0; x < oMapE[y].length;x++) {
				switch(oMapE[y][x]) {
				case 0:
					oMap[y][x] = new Obstruction(x,y);
					break;
				case 1:
					oMap[y][x] = new Obstruction(ObstructionType.PEDESTAL,x,y);
					break;
				case 2:
					oMap[y][x] = new Obstruction(ObstructionType.WALL,x,y);
					break;
				case 3:
					oMap[y][x] = new Obstruction(ObstructionType.PILLAR,x,y);
					break;	
				case 4:
					oMap[y][x] = new Obstruction(ObstructionType.FENCE,x,y);
					break;	
				default:
					oMap[y][x] = new Obstruction(x,y);
					break;
				}
			}
		}
		
		return null;
	}

	public int getSizeCategory() {
		return this.sizeCategory;
	}
	
	@Override
	public int compareTo(Prefab that) {
		return this.sizeCategory - that.getSizeCategory();
	}
}
