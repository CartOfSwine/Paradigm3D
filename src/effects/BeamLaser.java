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
	
	private Geometry laserCore;
	private ParticleEmitter laserParticles;
	
	public BeamLaser(AssetManager asset, ColorRGBA primaryColor,ColorRGBA secondaryColor, Vector3f dest, long startTime, long duration) {
		
		this.source = new Vector3f(0,0,0);
		this.destination = new Vector3f(dest);
		
		this.startTime = startTime;
		this.duration = duration;
		this.endTime = startTime + duration;	
		
		float distance = destination.distance(source);
		Cylinder lc = new Cylinder(2,4,.1f,distance);
		laserCore = new Geometry("LaserCore",lc);

		laserCore.setLocalTranslation(FastMath.interpolateLinear(.5f, this.source, this.destination));
		laserCore.lookAt(this.destination, Vector3f.UNIT_XYZ);		
		
		System.out.print(source + " ");
		System.out.print(dest + " ");
		System.out.println(laserCore.getLocalTranslation());
		
		
		Material coreMat = new Material(asset, "Common/MatDefs/Misc/Unshaded.j3md");
		coreMat.setColor("GlowColor", primaryColor);
		coreMat.setColor("Color", primaryColor);
		laserCore.setMaterial(coreMat);
		
		laserParticles = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 10);
		Material mat = new Material(asset, "Common/MatDefs/Misc/Particle.j3md");
		mat.setTexture("Texture", asset.loadTexture("Effects/Explosion/smoketrail.png"));
		laserParticles.setMaterial(mat);
		laserParticles.setImagesX(1);
		laserParticles.setImagesY(3);
		laserParticles.setEndColor(secondaryColor);
		laserParticles.setStartColor(primaryColor);
		laserParticles.setLocalTranslation(this.source);
		laserParticles.getParticleInfluencer().setInitialVelocity(new Vector3f(destination).mult(5));
		laserParticles.setStartSize(0.5f);
		laserParticles.setEndSize(0.2f);
		laserParticles.setLowLife(duration/1000f);
		laserParticles.setHighLife(duration/1000f);
		laserParticles.getParticleInfluencer().setVelocityVariation(.01f);
		this.controlNode = new Node();
	}
	
	public Node getNode() {
		return this.controlNode;
	}
	
	public boolean update() {
		long now = System.currentTimeMillis();
		
		if(!laserOn && now > startTime) {
			this.controlNode.attachChild(laserCore);
			this.controlNode.attachChild(laserParticles);
			//this.laserParticles.emitAllParticles();
			
			laserOn = true;
		}
		
		if(laserOn && now > endTime) {
			//this.controlNode.detachAllChildren();
			laserOn = false;
		}
		
		return readyForTermination(now);
	}
	
	
}
