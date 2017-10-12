package worldData;

import com.jme3.math.ColorRGBA;

public enum ObstructionType{
	EMPTY(1,ColorRGBA.White,0,false),
	PEDESTAL(0.8, ColorRGBA.Black,50,false),
	WALL(0.3, ColorRGBA.Black,100,true),
	PILLAR(0.5, ColorRGBA.Gray,100,true),
	CORPSE(0.8, ColorRGBA.Black,100,false),
	FENCE(0.4,ColorRGBA.Gray,100,true);
	
	public final double DEFENCE;
	public final ColorRGBA COLOR;
	public final int MAXHP;
	public final boolean BLOCKS_AOE;

	ObstructionType(double defence, ColorRGBA color, int maxHP,boolean blocksAOE){
		this.DEFENCE = defence;
		this.COLOR = color;
		this.MAXHP = maxHP;
		this.BLOCKS_AOE = blocksAOE;
	}
}
