package worldData;

import java.awt.Color;
import java.io.Serializable;

public enum ObstructionType implements Serializable{
	EMPTY(1,Color.WHITE,0,false),
	PEDESTAL(0.8, Color.BLACK,50,false),
	WALL(0.3, Color.BLACK,100,true),
	PILLAR(0.5, Color.GRAY,100,true),
	CORPSE(0.8, Color.BLACK,100,false),
	FENCE(0.4,Color.GRAY,100,true);
	
	public final double DEFENCE;
	public final Color COLOR;
	public final int MAXHP;
	public final boolean BLOCKS_AOE;

	ObstructionType(double defence, Color color, int maxHP,boolean blocksAOE){
		this.DEFENCE = defence;
		this.COLOR = color;
		this.MAXHP = maxHP;
		this.BLOCKS_AOE = blocksAOE;
	}
}
