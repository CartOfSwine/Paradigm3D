package effects;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import com.jme3.effect.ParticleMesh;

public class BeamLaser extends ActionEffect{
	private boolean laserOn = false;
	private boolean particlesOn =false;
	
	private Geometry laserCore;
	private ParticleEmitter laserParticles;
	
	public BeamLaser(AssetManager asset, ColorRGBA primaryColor,ColorRGBA secondaryColor,  Vector3f dest, long startTime, long duration) {
		super(asset, startTime, duration, new Vector3f(0,0,0), dest, new Node());
		
		float distance = destination.distance(source);
		Cylinder lc = new Cylinder(2,4,.1f,1);
		laserCore = new Geometry("LaserCore",lc);
		laserCore.scale(1,1,distance);
		
		laserCore.setLocalTranslation(FastMath.interpolateLinear(.5f, this.source, this.destination));
		laserCore.lookAt(this.source, Vector3f.UNIT_XYZ);		
		
		Material coreMat = new Material(asset, "Common/MatDefs/Misc/Unshaded.j3md");
		coreMat.setColor("GlowColor", primaryColor);
		coreMat.setColor("Color", primaryColor);
		laserCore.setMaterial(coreMat);
		
		laserParticles = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
		Material mat = new Material(asset, "Common/MatDefs/Misc/Particle.j3md");
		mat.setTexture("Texture", asset.loadTexture("Effects/Explosion/smoketrail.png"));
		laserParticles.setMaterial(mat);
		laserParticles.setImagesX(1);
		laserParticles.setImagesY(3);
		laserParticles.setEndColor(secondaryColor);
		laserParticles.setStartColor(primaryColor);
		laserParticles.getParticleInfluencer().setInitialVelocity(new Vector3f(destination).mult(source.distance(dest)/10));
		laserParticles.setStartSize(0.5f);
		laserParticles.setEndSize(0.2f);
		laserParticles.setLowLife(duration/4000f);
		laserParticles.setHighLife(duration/4000f);
		laserParticles.getParticleInfluencer().setVelocityVariation(.1f);
	}
	
	public Node getNode() {
		return this.controlNode;
	}
	
	public void reset(Vector3f destination, long startTime, long duration) {
		this.destination = new Vector3f(destination);
		this.startTime = startTime;
		this.duration = duration;
		this.endTime = startTime + duration;
		
		float distance = destination.distance(source);
		laserCore.scale(1,1,1/laserCore.getLocalScale().z);
		laserCore.scale(1,1,distance);
		
		laserCore.setLocalTranslation(FastMath.interpolateLinear(0, this.source, this.destination));
		laserCore.lookAt(this.destination, Vector3f.UNIT_XYZ);
		
		laserParticles.getParticleInfluencer().setInitialVelocity(new Vector3f(destination).mult(source.distance(destination)/10));
		laserParticles.setLowLife(duration/1000f);
		laserParticles.setHighLife(duration/1000f);
	}
	
	
	
	public boolean update() {
		long now = System.currentTimeMillis();
		
		if(!laserOn && !particlesOn && now > startTime  && now < endTime) {
			this.controlNode.attachChild(laserParticles);
			this.controlNode.attachChild(laserCore);
			particlesOn = true;
			laserOn = true;
		}
		
		if(laserOn && particlesOn && now > endTime) {
			laserOn = false;
			particlesOn = false;
			this.controlNode.detachAllChildren();
		}
		
		return readyForTermination(now);
	}
	
	
}
