package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
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
	
	//References
	private static Loader loader;
	private static Light sun;
	private static Player player;
	private static Camera camera;
	private static MasterRenderer renderer;
	
	//Temporary test variables
	private static Entity testEntity;
	
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
		testEntity = new Entity(texturedModel, new Vector3f(0, 20, 0), 0, 45, 0, 1, false);
		
		//Renderer
		renderer = new MasterRenderer();
	}
	
	//Main method
	public static void main(String[] args) {
		
		Initialise();
		ChunkLoading();
		
		//Chunk entities
		List<Entity> terrainEntities = new ArrayList<Entity>();
		List<Entity> entities = new ArrayList<Entity>();
		
		entities.add(player);
		entities.add(testEntity);
		
		
		// MAIN LOOP
		//index for chunks
		int indexEntity = 0;
		while(!Display.isCloseRequested()) {
			
			//COLLISION SYSTEM
			//Work out what chunk the player is in
			//Get heights[x][z] of chunk then update in player.setTerrainHeight()
			
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
			
			
			camera.Move();
			player.Move();
			
			for(Entity entity : entities) {
				if(entity.GetVisibility()) {
					renderer.ProcessEntity(entity);
				}
			}
			
			//Cannot see entity until it has spawned, because it does not exist in the entity list
			if(Keyboard.isKeyDown(Keyboard.KEY_1)) {	
				testEntity.Spawn();
				
			}
			
			if(Keyboard.isKeyDown(Keyboard.KEY_2)) {
				testEntity.Despawn();
				
			}
			
			//Creating Entity data from terrain chunk data
			if(indexEntity < terrainChunks.size()) {
				RawModel chunkModel = loader.LoadTerrainToVAO(
						terrainChunks.get(indexEntity).positions, 
						terrainChunks.get(indexEntity).uvs, 
						terrainChunks.get(indexEntity).normals);
				
				TexturedModel texturedChunkModel = new TexturedModel(chunkModel, new ModelTexture(loader.LoadTexture("dirt")));
				ModelTexture chunkTexture = texturedChunkModel.GetTexture();
				
				//Prevents terrain rendering strangely
				chunkTexture.SetTransparencyState(true);
				
				Entity chunkEntity = new Entity(texturedChunkModel, terrainChunks.get(indexEntity).chunk.origin, 0, 0, 0, 1, true);
				terrainEntities.add(chunkEntity);

				//clearing arrays (important for performance reasons)
				terrainChunks.get(indexEntity).positions = null;
				terrainChunks.get(indexEntity).normals = null;
				terrainChunks.get(indexEntity).uvs = null;
				
				indexEntity++;
			}
			
			//Rendering terrainEntities if within range
			for(int i = 0; i < terrainEntities.size(); i++) {
				
				Vector3f origin = terrainEntities.get(i).GetPosition();
				
				int distX = (int) (playerPos.x - origin.x);
				int distZ = (int) (playerPos.z - origin.z);
				
				if(distX < 0) {
					distX = -distX;
				}
				
				if(distZ < 0) {
					distZ = -distZ;
				}
				
				if((distX <= RENDER_DISTANCE) && (distZ <= RENDER_DISTANCE)) {
					renderer.ProcessEntity(terrainEntities.get(i));
				}
			}
			
			//Main render method
			renderer.Render(sun, camera);
			
			//Update display
			DisplayManager.UpdateDisplay();
		}
		
		//Close game and clear arrays
		Mouse.destroy();
		isRunning = false;
		terrainChunks.clear();
		usedPositions.clear();
		renderer.CleanUp();
		loader.CleanUp();
		DisplayManager.CloseDisplay();
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