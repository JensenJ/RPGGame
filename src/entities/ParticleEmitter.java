package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import render.DisplayManager;

public class ParticleEmitter extends Entity {

	private int particleDensity;
	private float particleTravelDist;
	private float particleRadius;
	private float particleScale;
	private float particleSpawnHeight;
	private float particleSpeed;
	
	private List<Entity> particles;
	
	private boolean isActive;
	TexturedModel particleModel;
	
	//Different particle effect types
	public static enum PARTICLETYPE {
		RISING, SWIRL, PULSE
	};
	
	public PARTICLETYPE type;
	
	//Constructor
	public ParticleEmitter(Entity entity, int particleDensity, float particleTravelDist, float particleRadius, float particleScale, 
			float particleSpawnHeight, float particleSpeed, PARTICLETYPE particleType, TexturedModel particleModel) {
		super(entity.GetModel(), entity.GetPosition(), entity.GetRotX(), entity.GetRotY(), entity.GetRotZ(), entity.GetOriginalScale(), entity.GetShouldDrawInArrays());
		this.particleDensity = particleDensity;
		this.particleTravelDist = particleTravelDist;
		this.particleRadius = particleRadius;
		this.particleScale = particleScale;
		this.particleSpawnHeight = particleSpawnHeight;
		this.particleSpeed = particleSpeed;
		this.type = particleType;
		this.particleModel = particleModel;
		this.isActive = false;
		
	}
	
	
	//Toggles particle state
	public void ToggleParticlesActive() {
		SetActiveState(GetActiveState() == true ? false : true);
	}
	
	//Returns a list of entities to render
	public List<Entity> InitParticles() {
		particles = new ArrayList<Entity>();
		
		//Set spawn point for every particle and add to particle list
		if(type == PARTICLETYPE.RISING) {
			for(int i = 0; i < particleDensity; i++) {
				Vector3f particlePosition = GenerateRisingParticlePosition();
				Entity particle = new Entity(particleModel, particlePosition, 0, 0, 0, particleScale, false);
				particles.add(particle);
			}
		}else if(type == PARTICLETYPE.SWIRL) {
			
		}else if(type == PARTICLETYPE.PULSE) {
			for(int i = 0; i < particleDensity; i++) {
				Vector3f particlePosition = GeneratePulseParticlePosition();
				Entity particle = new Entity(particleModel, particlePosition, 0, 0, 0, particleScale, false);
				particles.add(particle);
			}
		}
		
		//Return particle list
		return particles;
	}
	
	//Updates particle data, according to particle pattern type
	public void UpdateParticles() {
		//If particles are active
		if(isActive == true) {
			//Different particle animations
			if(type == PARTICLETYPE.RISING) {
				//For each particle
				for(Entity particle : this.particles) {
					//Increase position in y axis
					particle.IncreasePosition(0, particleSpeed * DisplayManager.GetFrameTimeSeconds(), 0);
					Vector3f particlePos = particle.GetPosition();
					Vector3f objectPos = super.GetPosition();
					//If particle is beyond the travel distance, despawn it
					if(particlePos.y - (objectPos.y - particleSpawnHeight) > particleTravelDist) {
						particle.Despawn(0.5f);
						//Once particle is no longer visable, reset its position
						if(particle.GetVisibility() == false) {
							Vector3f position = GenerateRisingParticlePosition();
							particle.SetPosition(position);
						}
					}else {
						//Spawn / respawn particle after despawning
						particle.Spawn(0.5f);
					}
				}
			}else if(type == PARTICLETYPE.SWIRL) {
				//Swirl code
			}else if(type == PARTICLETYPE.PULSE) {
				for(int i = 0; i < particles.size(); i++) {
					Entity particle = particles.get(i);
					float angle = (360 / particleDensity) * i;
					
					float forwardDistance = particleSpeed * DisplayManager.GetFrameTimeSeconds();
					float fx = (float) (forwardDistance * Math.sin(Math.toRadians(angle)));
					float fz = (float) (forwardDistance * Math.cos(Math.toRadians(angle)));
					particle.IncreasePosition(fx, 0, fz);
					
					//particle.IncreasePosition(particleSpeed * DisplayManager.GetFrameTimeSeconds(), 0, 0);
					Vector3f particlePos = particle.GetPosition();
					Vector3f objectPos = super.GetPosition();
					
					float xObject = objectPos.x;
					float zObject = objectPos.z;
					
					//Particle pos
					float x = particlePos.x;
					float z = particlePos.z;
					
					//Prevents negatives
					x = x < 0 ? -x : x;
					z = z < 0 ? -z : z;
					
					float distanceTravelled = (x - xObject) + (z - zObject);
					if(distanceTravelled > particleTravelDist) {
						particle.Despawn(1.0f);
						if(particle.GetVisibility() == false) {
							Vector3f position = GeneratePulseParticlePosition();
							particle.SetPosition(position);
						}
					}else {
						particle.Spawn(1.0f);
					}
				}
			}
		}else {
			for(Entity particle: particles) {
				particle.Despawn(1.0f);
			}
		}
	}
	
	//Randomly generates a particles position for the rising particle type
	private Vector3f GenerateRisingParticlePosition() {
		Random random = new Random();
		Vector3f position = super.GetPosition();
		float x = particleRadius * random.nextFloat();
		float y = particleRadius * random.nextFloat();
		float z = particleRadius * random.nextFloat();
		
		//Negative values
		boolean negX = random.nextBoolean();
		boolean negZ = random.nextBoolean();
		
		if(negX) {
			x = -x;
		}
		
		if(negZ) {
			z = -z;
		}
		
		//Create final position and return it
		Vector3f particlePosition = new Vector3f(position.x + x, position.y + particleSpawnHeight + y, position.z + z);
		return particlePosition;
	}
	
	private Vector3f GeneratePulseParticlePosition() {
		Vector3f position = super.GetPosition();
		Vector3f particlePosition = new Vector3f(position.x, position.y + particleSpawnHeight, position.z);
		return particlePosition;
	}
	
	public void CleanUp() {
		particles.clear();
	}
	
	//Getters and setters
	public int GetParticleDensity() {
		return particleDensity;
	}

	public void SetParticleDensity(int particleDensity) {
		this.particleDensity = particleDensity;
	}

	public float GetParticleTravelDist() {
		return particleTravelDist;
	}

	public void SetParticleTravelDist(float particleTravelDist) {
		this.particleTravelDist = particleTravelDist;
	}

	public float GetParticleRadius() {
		return particleRadius;
	}

	public void SetParticleRadius(float particleRadius) {
		this.particleRadius = particleRadius;
	}
	
	public float GetParticleScale() {
		return particleScale;
	}

	public void SetParticleScale(float particleScale) {
		this.particleScale = particleScale;
	}
	
	public float GetParticleSpawnHeight() {
		return particleSpawnHeight;
	}

	public void SetParticleSpawnHeight(float particleSpawnHeight) {
		this.particleSpawnHeight = particleSpawnHeight;
	}
	
	public float GetParticleSpeed() {
		return particleSpeed;
	}
	
	public void SetParticleSpeed(float particleSpeed) {
		this.particleSpeed = particleSpeed;
	}

	public PARTICLETYPE GetType() {
		return type;
	}

	public void SetType(PARTICLETYPE type) {
		this.type = type;
	}

	public boolean GetActiveState() {
		return isActive;
	}
	
	public void SetActiveState(boolean active) {
		this.isActive = active;
	}


	public List<Entity> GetParticles() {
		return particles;
	}


	public void SetParticles(List<Entity> particles) {
		this.particles = particles;
	}
	
	
}
