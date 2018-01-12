package effects;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;


public class Aura extends ActionEffect{
	private ParticleEmitter auraParticles;
	private boolean particlesOn = false;
	
	private static final float MIN_SIZE = 0.1f;
	private static final float MAX_SIZE = 1.0f;
	
	private int minBuff;
	private int maxBuff;
	
	private int lastStrength = 0;
	
	public Aura(AssetManager asset, ColorRGBA primaryColor,ColorRGBA secondaryColor, long startTime, 
			long duration, int minBuff, int maxBuff) {
		super(asset, startTime, duration, new Vector3f(0,0,0), new Vector3f(), new Node());
		
		this.minBuff = minBuff;
		this.maxBuff = maxBuff;
		
		auraParticles = new ParticleEmitter("Emitter",ParticleMesh.Type.Triangle,10);
		Material mat = new Material(asset, "Common/MatDefs/Misc/Particle.j3md");
		mat.setTexture("Texture", asset.loadTexture("Effects/Explosion/smoketrail.png"));
		auraParticles.setMaterial(mat);
		auraParticles.setImagesX(1);
		auraParticles.setImagesY(3);
		auraParticles.setEndColor(secondaryColor);
		auraParticles.setStartColor(primaryColor);
		auraParticles.getParticleInfluencer().setInitialVelocity(new Vector3f(0,5f,0));
		auraParticles.setLocalTranslation(this.source);
		auraParticles.setStartSize(MIN_SIZE);
		auraParticles.setEndSize(MIN_SIZE);
		auraParticles.setLowLife(duration); //the duration in miliseconds
		auraParticles.setHighLife(duration);
		auraParticles.getParticleInfluencer().setVelocityVariation(10f);
	}
	
	public Node getNode() {
		return this.controlNode;
	}
	
	public void reset(long startTime, long duration) {
		this.startTime = startTime;
		this.duration = duration;
		this.endTime = startTime + duration;
	}
	
	public boolean update(int strength) {
		if(lastStrength != strength) {
			lastStrength = strength;
			float size = (strength - minBuff)/((float)maxBuff - (float)minBuff) * (MAX_SIZE - MIN_SIZE) + MIN_SIZE;
			//System.out.println(size);
			auraParticles.setStartSize(size);
			auraParticles.setEndSize(size);
			
		}
		auraParticles.emitParticles(5);
		System.out.println(auraParticles.getNumVisibleParticles());
		
		boolean killMe = false;
		if(strength <= 0 && particlesOn) {
			this.controlNode.detachAllChildren();
			particlesOn = false;
			killMe = true;
		}
		if(strength > 0 && !particlesOn) {
			this.controlNode.attachChild(auraParticles);
			particlesOn = true;
		}
		
		return killMe;
	}
}
