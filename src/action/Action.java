package action;

public enum Action{ //NEEDS TO BE BALANCED EVENTUALLY
   
   ATTACK_U(BAT.ATTACK,10,15,AOE.UP,AOE.NONE),
   ATTACK_R(BAT.ATTACK,10,15,AOE.RIGHT,AOE.NONE),
   ATTACK_D(BAT.ATTACK,10,15,AOE.DOWN,AOE.NONE),
   ATTACK_L(BAT.ATTACK,10,15,AOE.LEFT,AOE.NONE),
   
   DEFEND(BAT.DEFEND,10,50,AOE.NONE,AOE.NONE),
   FOCUS(BAT.FOCUS,10,50,AOE.NONE,AOE.NONE),
   
   EAT_U(BAT.EAT,8,30,AOE.NONE,AOE.UP),
   EAT_R(BAT.EAT,8,30,AOE.NONE,AOE.RIGHT),
   EAT_D(BAT.EAT,8,30,AOE.NONE,AOE.DOWN),
   EAT_L(BAT.EAT,8,30,AOE.NONE,AOE.LEFT),
   EAT_S(BAT.EAT,8,30,AOE.NONE,AOE.SELF), //note:does not eat oneself, but the square one is standing on
   
   GRAZE(8,0,0,10,0,0,0,0,AOE.NONE,AOE.SURROUNDING,false),
   
   OBSERVE(BAT.OBSERVE,50,20,AOE.NONE,AOE.NONE),           //the high cost is because of the performance cost. lots of additional scan area
   
   MOVE_U(BAT.MOVE,5,0,AOE.NONE,AOE.NONE),
   MOVE_R(BAT.MOVE,5,1,AOE.NONE,AOE.NONE),
   MOVE_D(BAT.MOVE,5,2,AOE.NONE,AOE.NONE),
   MOVE_L(BAT.MOVE,5,3,AOE.NONE,AOE.NONE);
   
   
   //might as well make these public. they are final so they cant get messed with
   public final int baseCost;   //the base food cost to preform the action
   
   public final int attack;  
   public final int defence;
   public final int focus;
   public final int eat;
   public final int sense;      //the temporary sensory increase. Percentage based
   
   public final int yChange;    //the change in y cordinates resulting from this action
   public final int xChange;    //the change in x cordinates resulting from this action
   
   public final AOE attackAOE;
   public final AOE eatAOE;
   
   public final boolean singleTarget;  //in the event that the action will cover muiltiple squares, this determines if they are all effected, or only the first one is
                                       //for an example, an attack with a wide AOE but singleTarget=true will only be able to hit one creature regardless of how
                                       //many are in the effective area. with singleTarget = false it will hit every creature, not just the first
   Action(BAT t, int cost,int num, AOE aAOE, AOE eAOE){
	   this(cost,
			   t.getAttack(num),
			   t.getDefend(num),
			   t.getFocus(num),
			   t.getEat(num),
			   t.getXmod(num),
			   t.getYmod(num),
			   t.getSense(num),
			   aAOE, eAOE,false);
   }
   
   
   Action(int baseCost, int attack, int defence, int focus, int eat, int xChange, int yChange, int sense, AOE attackAOE, AOE eatAOE,boolean singleTarget){
      this.baseCost = baseCost;   
      this.attack = attack;
      this.defence = defence;
      this.focus = focus;
      this.eat = eat;
      this.yChange = yChange;
      this.xChange = xChange;
      this.sense = sense;
      this.attackAOE = attackAOE;
      this.eatAOE = eatAOE;
      this.singleTarget = singleTarget;
      
   }
   
   public static Action getEatAction(int dir) throws IllegalArgumentException {
      switch (dir){
         case 0:
            return Action.EAT_U;
         case 1:
            return Action.EAT_R;
         case 2:
            return Action.EAT_D;
         case 3:
            return Action.EAT_L;
         case 4:
            return Action.EAT_S;
      }
      
      throw new IllegalArgumentException("bad direction passed to getEatAction");
   }
   
   public static Action getAttackAction(int dir) throws IllegalArgumentException {
      switch (dir){
         case 0:
            return Action.ATTACK_U;
         case 1:
            return Action.ATTACK_R;
         case 2:
            return Action.ATTACK_D;
         case 3:
            return Action.ATTACK_L;
      }
      
      throw new IllegalArgumentException("bad direction passed to getAttackAction");
   }
   
   public static Action getMoveAction(int dir) throws IllegalArgumentException {
      switch (dir){
         case 0:
            return Action.MOVE_U;
         case 1:
            return Action.MOVE_R;
         case 2:
            return Action.MOVE_D;
         case 3:
            return Action.MOVE_L;
      } 
      
      throw new IllegalArgumentException("bad direction passed to getMoveAction");
   }
   
   public boolean isMovement(){return (this.yChange != 0 || this.xChange != 0);}
   
   public boolean isAttack(){return this.attack != 0;}
   
   public boolean isDefence(){return this.defence != 0;}
   
   public boolean isEat(){return this.eat != 0;}
   
   public boolean isSense(){return this.sense != 0;}
   
   public boolean isFocus() {return this.focus != 0;}
}


//stands for BasicActionType. shortened for legibility of action constructors
enum BAT{
	ATTACK,
	MOVE,
	DEFEND,
	EAT,
	OBSERVE,
	FOCUS,
	SENSE;
	
	public int getAttack(int num) {
		if(this == ATTACK) 
			return num;
		return 0;
	}
	
	public int getSense(int num) {
		if(this == SENSE)
			return num;
		return 0;
	}
	
	public int getDefend(int num) {
		if(this == DEFEND)
			return num;
		return 0;
	}
	
	public int getEat(int num) {
		if(this == EAT)
			return num;
		return 0;
	}
	
	public int getObserve(int num) {
		if(this == OBSERVE)
			return num;
		return 0;
	}
	
	public int getFocus(int num) {
		if(this == FOCUS)
			return num;
		return 0;
	}
	
	public int getXmod(int dir) {
		if(this != MOVE)return 0;
		if(dir % 2 == 0)return 0;
		return (dir-2) * -1;
	}
	public int getYmod(int dir) {
		if(this != MOVE)
			return 0;
		if(dir % 2 == 1)return 0;
		return (dir-1);
	}
}