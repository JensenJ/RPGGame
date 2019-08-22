package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.ParticleEmitter;
import entities.ParticleEmitter.PARTICLETYPE;
import entities.Player;
import models.RawModel;
import models.TexturedModel;
import render.DisplayManager;
import render.Loader;
import render.MasterRenderer;
import terrain.Chunk;
import terrain.ChunkMesh;
import terrain.HeightGenerator;
import terrain.Voxel;
import obj.ModelData;
import obj.OBJFileLoader;
import textures.ModelTexture;

public class MainGameLoop {

	//Game settings
	private static Vector3f playerPos = new Vector3f(0, 0, 0);
	private static final int CHUNK_SIZE = 16;
	private static final int RENDER_DISTANCE = 16 * CHUNK_SIZE;
	
	//back end variables
	private static boolean isRunning = true;
	private static List<ChunkMesh> terrainChunks;
	private static List<Vector3f> usedPositions;
	
	private static List<Entity> terrainEntities;
	private static List<Entity> entities;
	private static List<ParticleEmitter> particleEmitters;
	
	//References
	private static Loader loader;
	private static Light sun;
	private static Player player;
	private static Camera camera;
	private static MasterRenderer renderer;
	
	//Temporary test variables
	private static Entity testEntity;
	private static ParticleEmitter testEmitter;
	
	public static void Initialise() {
		
		//Creates window and loader
		DisplayManager.CreateDisplay();
		loader = new Loader();
		
		//Create mouse object
		try {
			Mouse.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		//Locks mouse to window
		Mouse.setGrabbed(true);
		
		//Loads test cube model data
		ModelData modelData = OBJFileLoader.LoadOBJ("TestCube");
		
		//Loads player model from test cube
		RawModel playerModel = loader.LoadToVAO(
				modelData.GetVertices(),
				modelData.GetTextureCoords(),
				modelData.GetNormals(),
				modelData.GetIndices());
		
		//Creates textured model and sets texture settings
		TexturedModel texturedModel = new TexturedModel(playerModel, new ModelTexture(loader.LoadTexture("dirt")));
		ModelTexture texture = texturedModel.GetTexture();
		texture.SetShineDamper(10f);
		texture.SetReflectivity(0.1f);
		
		//Initialises sun
		sun = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(1, 1, 1));
		
		//Player and Camera
		player = new Player(texturedModel, new Vector3f(0, 0, 0), 0, 180, 0, 1);
		player.Spawn();
		camera = new Camera(player);
		
		//Entities
		testEntity = new Entity(texturedModel, new Vector3f(0, 15, 0), 0, 45, 0, 1, false);
		testEmitter = new ParticleEmitter(testEntity, 7, 6, 2, 0.5f, 0.5f, 2f, PARTICLETYPE.RISING, texturedModel);
		//Renderer
		renderer = new MasterRenderer();
	}
	
	//Main method
	public static void main(String[] args) {
		
		Initialise();
		ChunkLoading();
		
		//Chunk entities
		terrainEntities = new ArrayList<Entity>();
		entities = new ArrayList<Entity>();
		particleEmitters = new ArrayList<ParticleEmitter>();
		
		entities.add(player);
		
		testEmitter.SetActiveState(true);
		testEmitter.InitParticles();
		
		particleEmitters.add(testEmitter);
		
		// MAIN LOOP
		//index for chunks
		int chunkIndex = 0;
		while(!Display.isCloseRequested()) {	
			camera.Move();
			player.Move();
			
			Collisions();
			
			testEmitter.UpdateParticles();
			
			//Creating Entity data from terrain chunk data
			if(chunkIndex < terrainChunks.size()) {
				RawModel chunkModel = loader.LoadTerrainToVAO(
						terrainChunks.get(chunkIndex).positions, 
						terrainChunks.get(chunkIndex).uvs, 
						terrainChunks.get(chunkIndex).normals);
				
				TexturedModel texturedChunkModel = new TexturedModel(chunkModel, new ModelTexture(loader.LoadTexture("dirt")));
				ModelTexture chunkTexture = texturedChunkModel.GetTexture();
				
				//Prevents terrain rendering strangely
				chunkTexture.SetTransparencyState(true);
				
				Entity chunkEntity = new Entity(texturedChunkModel, terrainChunks.get(chunkIndex).chunk.origin, 0, 0, 0, 1, true);
				terrainEntities.add(chunkEntity);

				//clearing arrays (important for performance reasons)
				terrainChunks.get(chunkIndex).positions = null;
				terrainChunks.get(chunkIndex).normals = null;
				terrainChunks.get(chunkIndex).uvs = null;
				
				chunkIndex++;
			}
			
			Render();
			
		}
		
		//Close game and clear arrays
		Mouse.destroy();
		isRunning = false;
		terrainChunks.clear();
		usedPositions.clear();
		terrainEntities.clear();
		entities.clear();
		particleEmitters.clear();
		renderer.CleanUp();
		loader.CleanUp();
		DisplayManager.CloseDisplay();
	}
	
	public static void Collisions() {
		//Player and Camera functions
		playerPos = player.GetPosition();
		
		Chunk currentChunk;
		
		//For each chunk
		for(int i = 0; i < terrainChunks.size(); i++) {
			currentChunk = terrainChunks.get(i).chunk;
			
			//Get chunk origin
			Vector3f origin = currentChunk.origin;
			
	    	//If player is standing in this chunk
			if((playerPos.x >= origin.x && playerPos.x <= origin.x + CHUNK_SIZE) && (playerPos.z >= origin.z && playerPos.z <= origin.z + CHUNK_SIZE)) {
				
				//Calculate player position
				Vector3f playerChunkPos = new Vector3f(playerPos.x % CHUNK_SIZE, 0, playerPos.x % CHUNK_SIZE);
				
				int x = (int) playerChunkPos.x;
				int z = (int) playerChunkPos.z;
				
				//Prevents negative position
		    	x = x < 0 ? -x : x;
		    	z = z < 0 ? -z : z;
				
		    	//System.out.println("X: " + x + " Z:" + z);
				//System.out.println(origin);
				
				//Negative chunks
				if(origin.x < 0 && origin.z < 0) {
					x = 15-x;
					z = 15-z;
				}else if(origin.x < 0) {
					z = 15-z;
				}else if(origin.z < 0) {
					x = 15-x;
				}
				
				player.SetTerrainHeight(currentChunk.heights[x][z] + 1);
				//System.out.println("New Height: " + currentChunk.heights[x][z] + 1);
				
				break;
			}
		}
	}
	
	public static void Render() {
		
		//Rendering terrainEntities if within range
		for(int i = 0; i < terrainEntities.size(); i++) {
			
			Vector3f origin = terrainEntities.get(i).GetPosition();
			
			int distX = (int) (playerPos.x - origin.x);
			int distZ = (int) (playerPos.z - origin.z);
			
			//Prevents negative position
	    	distX = distX < 0 ? -distX : distX;
	    	distZ = distZ < 0 ? -distZ : distZ;
			
	    	//Spawns terrain entity if within render distance
			if((distX <= RENDER_DISTANCE) && (distZ <= RENDER_DISTANCE)) {
				terrainEntities.get(i).Spawn();
				if(terrainEntities.get(i).GetVisibility()) {
					renderer.ProcessEntity(terrainEntities.get(i));
				}
			//Despawns terrain entity if out of render distance
			}else if ((distX <= RENDER_DISTANCE + (2 * CHUNK_SIZE)) && (distZ <= RENDER_DISTANCE + (2 * CHUNK_SIZE))) { 
				terrainEntities.get(i).Despawn();
				if(terrainEntities.get(i).GetVisibility()) {
					renderer.ProcessEntity(terrainEntities.get(i));
				}
			}
		}
		
		//Rendering entities if within range
		for(int i = 0; i < entities.size(); i++) {
			
			Vector3f origin = entities.get(i).GetPosition();
			
			int distX = (int) (playerPos.x - origin.x);
			int distZ = (int) (playerPos.z - origin.z);
			
			//Prevents negative position
	    	distX = distX < 0 ? -distX : distX;
	    	distZ = distZ < 0 ? -distZ : distZ;
			
	    	//Spawns entity if within render distance
			if((distX <= RENDER_DISTANCE) && (distZ <= RENDER_DISTANCE)) {
				entities.get(i).Spawn();
				if(entities.get(i).GetVisibility()) {
					renderer.ProcessEntity(entities.get(i));
				}
			//Despawns entity if out of render distance
			}else if ((distX <= RENDER_DISTANCE + (2 * CHUNK_SIZE)) && (distZ <= RENDER_DISTANCE + (2 * CHUNK_SIZE))) { 
				entities.get(i).Despawn();
				if(entities.get(i).GetVisibility()) {
					renderer.ProcessEntity(entities.get(i));
				}
			}
		}
		
		//Rendering particles and emitters if within range and active
		for(int i = 0; i < particleEmitters.size(); i++) {
			
			Vector3f origin = particleEmitters.get(i).GetPosition();
			
			int distX = (int) (playerPos.x - origin.x);
			int distZ = (int) (playerPos.z - origin.z);
			
			//Prevents negative position
	    	distX = distX < 0 ? -distX : distX;
	    	distZ = distZ < 0 ? -distZ : distZ;
			
	    	List<Entity> particles = particleEmitters.get(i).GetParticles();
	    	//Spawns particle if within render distance
			if((distX <= RENDER_DISTANCE) && (distZ <= RENDER_DISTANCE)) {
				particleEmitters.get(i).SetActiveState(true);
				particleEmitters.get(i).Spawn();
				if(particleEmitters.get(i).GetVisibility()) {
					renderer.ProcessEntity(particleEmitters.get(i));
					for(Entity particle : particles) {
						if(particle.GetVisibility()) {
							renderer.ProcessEntity(particle);
						}
					}
				}
			//Despawns particle if out of render distance
			}else if ((distX <= RENDER_DISTANCE + (2 * CHUNK_SIZE)) && (distZ <= RENDER_DISTANCE + (2 * CHUNK_SIZE))) {
				particleEmitters.get(i).SetActiveState(false);
				particleEmitters.get(i).Despawn();
				if(particleEmitters.get(i).GetVisibility()) {
					renderer.ProcessEntity(particleEmitters.get(i));
					for(Entity particle : particles) {
						if(particle.GetVisibility()) {
							renderer.ProcessEntity(particle);
						}
					}
				}
			}
		}
		
		//Main render method
		renderer.Render(sun, camera);
		
		//Update display
		DisplayManager.UpdateDisplay();
	}
	
	public static void ChunkLoading() {
		//Terrain Generation, synchronised lists as multiple threads access data
		terrainChunks = Collections.synchronizedList(new ArrayList<ChunkMesh>());
		usedPositions = Collections.synchronizedList(new ArrayList<Vector3f>());
		
		HeightGenerator chunkHeightGenerator = new HeightGenerator();
		
		//Multi-threaded method for generating terrain and good performance
		new Thread(new Runnable() {
			
			public void run() {
				while(isRunning) {
					//Generate terrain within RENDER_DISTANCE
					for(int x = (int) (playerPos.x - RENDER_DISTANCE) / CHUNK_SIZE; x < (playerPos.x + RENDER_DISTANCE) / CHUNK_SIZE; x++) {
						for(int z = (int) (playerPos.z - RENDER_DISTANCE) / CHUNK_SIZE; z < (playerPos.z + RENDER_DISTANCE) / CHUNK_SIZE; z++) {
							//Checks this chunk is already not being drawn
							if(!usedPositions.contains(new Vector3f(x * CHUNK_SIZE, 0 * CHUNK_SIZE, z * CHUNK_SIZE))) {
								
								//Voxel list
								List<Voxel> voxels = new ArrayList<Voxel>();
								
								float[][] heights = new float[CHUNK_SIZE][CHUNK_SIZE];
								
								//Creates voxels within area of CHUNK_SIZE and sets correct height and voxel type.
								for(int i = 0; i < CHUNK_SIZE; i++) {
									for(int j = 0; j < CHUNK_SIZE; j++) {
										heights[j][i] = chunkHeightGenerator.GenerateHeight(i + (x * CHUNK_SIZE), j + (z * CHUNK_SIZE));
										voxels.add(new Voxel(i, (int) Math.floor(heights[j][i]), j, Voxel.VOXELTYPE.DIRT));
									}
								}
								
								//Creates new chunk from voxel data and adds it to terrainChunks list, also to usedPositions for performance management
								Chunk chunk = new Chunk(voxels, new Vector3f(x * CHUNK_SIZE, 0 * CHUNK_SIZE, z * CHUNK_SIZE), heights);
								terrainChunks.add(new ChunkMesh(chunk));
								usedPositions.add(chunk.origin);
								
								//Clears voxel list for better RAM usage
								voxels.clear();
							}
						}
					}
				}
			}
		}).start();
	}
}