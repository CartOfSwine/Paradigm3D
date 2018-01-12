package replay;

import java.awt.Color;
import java.io.Serializable;

import action.Action;
import robits.Robit;

public class RobitState implements Serializable{
	private static final long serialVersionUID = 1L;
	//A snapshot of a single creature after a single action in a tick
	private int health;
	private int energy;
	private int attackBuff;
	private int defenceBuff;
	private int senseBuff;
	
	private Action actionTaken;
	
	private char[] shoutText;
	
	//dont want to store actual color objects
	private int colorR;
	private int colorG;
	private int colorB;
	private int colorA;
	
	private boolean isDead;
	
	private int xPos;
	private int yPos;
	
	private int score;
	
	public RobitState(Robit source) {
		this.health = source.getHealth();
		this.energy = source.getEnergy();
		this.attackBuff = source.getAttackBuff();
		this.defenceBuff = source.getDefenceBuff();
		this.senseBuff = source.getSenseBuff();
		this.actionTaken = source.getLastAction();

		Color c = source.getColor();
		this.colorR = c.getRed();
		this.colorG = c.getGreen();
		this.colorB = c.getBlue();
		this.colorA = c.getAlpha();
		
		this.isDead = source.getIsDead();
		this.xPos = source.getXpos();
		this.yPos = source.getYpos();
		
		this.score = source.getScore();
		
		this.shoutText = source.getShoutText().toCharArray();
	}
	
	public int getHealth() {return health;}
	public int getEnergy() {return energy;}
	public int getAttackBuff() {return attackBuff;}
	public int getDefenceBuff() {return defenceBuff;}
	public int getSenseBuff() {return senseBuff;}
	public Action getActionTaken() {return actionTaken;}
	public Color getColor() {return new Color(colorR,colorG,colorB,colorA);}
	public boolean getIsDead() {return isDead;}
	public int getXpos() {return xPos;}
	public int getYpos() {return yPos;}
	public int getScore() {return this.score;}
	public String getShoutText() {return String.valueOf(this.shoutText);}
}