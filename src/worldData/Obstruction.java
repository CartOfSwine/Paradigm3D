package worldData;

public class Obstruction {
	private int curHP;
	private ObstructionType type;
	private int xPos;
	private int yPos;
	
	public Obstruction(int x, int y){
		this.type = ObstructionType.EMPTY;
		this.curHP = this.type.MAXHP;
		this.xPos = x;
		this.yPos = y;
	}

	public Obstruction(ObstructionType type,int x, int y){
		this.type = type;
		this.curHP = type.MAXHP;
		this.xPos = x;
		this.yPos = y;
	}

	public boolean isEmpty(){
		return this.type == ObstructionType.EMPTY;
	}

	public boolean damage(int attk){
		this.curHP -= (int)attk * this.type.DEFENCE;
		if(this.curHP <= 0){
			this.curHP = 0;
			this.type = ObstructionType.EMPTY;
		}
		return this.type.BLOCKS_AOE;
	}
	
	public ObstructionType getType() {
		return this.type;
	}
	
	public int getHP() {
		return this.curHP;
	}
	
	public int getxPos() {
		return xPos;
	}

	public int getyPos() {
		return yPos;
	}
}
