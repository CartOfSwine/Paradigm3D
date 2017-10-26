package robits;

import worldData.World;

public class SightTarget implements Comparable<SightTarget>{
	private SightTargetType targetType;
	private int xPos;
	private int yPos;
	private Robit sourceRobit;
	private int activity;
	private int angle;
	private GameObject target;
	
	public SightTarget(SightTargetType sightTargetType, Robit sourceRobit, int xPos, int yPos, GameObject target) {
		this.targetType = sightTargetType;
		this.xPos = xPos;
		this.yPos = yPos;
		this.sourceRobit = sourceRobit;
		this.target = target;
	}
	
	//returns true if it is out of range and needs to be removed from list
	public boolean update(int maxSenseRange, World sim) {
		//first check line of sight
		if(SensorSuite.checkLOS(sourceRobit.getXpos(), sourceRobit.getYpos(), this.xPos, this.yPos, sim))
			return true;
		
		//cool, update our position
		if(target.isRobit()) {
			this.xPos = target.getRobit().getXpos();
			this.yPos = target.getRobit().getYpos();
		}
			
		//figure out distance
		int dx = sourceRobit.getXpos() - this.xPos;
		int dy = sourceRobit.getYpos() - this.yPos;
		double distance = Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2));
		
		//if they  are a robit and arent an ally factor in stealth
		if(target.isRobit() && targetType != SightTargetType.ALLY) {
			distance += (target.getRobit().getStealth()-100)/10;
		}
		
		//calculate activity and angle
		activity = SensorSuite.calcActivity(maxSenseRange, distance);
		angle = SensorSuite.calcAngle(dy,dx);
		
		return this.activity <= 0;
	}
	
	@Override
	public int compareTo(SightTarget that) {
		return this.activity - that.activity;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if(o == null)
			return false;
		if(o instanceof SightTarget) {
			SightTarget that = (SightTarget)o;

			return this.target.equals(that.target);	
		}
		
		return false;
	}
	
	public int getAngle() {
		return angle;
	}

	public SightTargetType getTargetType() {
		return targetType;
	}

	public int getxPos() {
		return xPos;
	}

	public int getyPos() {
		return yPos;
	}


	public int getActivity() {
		return activity;
	}

}
