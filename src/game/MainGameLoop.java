package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
	
	//Main method
	public static void main(String[] args) {
		//Creates window and loader
		DisplayManager.CreateDisplay();
		Loader loader = new Loader();
		
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
		Light sun = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(1, 1, 1));
	
		//Player and Camera
		Player player = new Player(texturedModel, new Vector3f(0, 0, 0), 0, 180, 0, 1);
		Camera camera = new Camera(player);
		
		//Renderer
		MasterRenderer renderer = new MasterRenderer();
		
		//Terrain Generation, synchronised lists as multiple threads access data
		List<ChunkMesh> terrainChunks = Collections.synchronizedList(new ArrayList<ChunkMesh>());
		List<Vector3f> usedPositions = Collections.synchronizedList(new ArrayList<Vector3f>());
		
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
								
								//Creates voxels within area of CHUNK_SIZE and sets correct height and voxel type.
								for(int i = 0; i < CHUNK_SIZE; i++) {
									for(int j = 0; j < CHUNK_SIZE; j++) {
										voxels.add(new Voxel(i, (int)chunkHeightGenerator.GenerateHeight(i + (x * CHUNK_SIZE), j + (z * CHUNK_SIZE)), j, Voxel.VOXELTYPE.DIRT));
									}
								}
								
								//Creates new chunk from voxel data and adds it to terrainChunks list, also to usedPositions for performance management
								Chunk chunk = new Chunk(voxels, new Vector3f(x * CHUNK_SIZE, 0 * CHUNK_SIZE, z * CHUNK_SIZE));
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
		
		//Chunk entities
		List<Entity> terrainEntities = new ArrayList<Entity>();
		
		// MAIN LOOP
		//index for chunks
		int index = 0;
		while(!Display.isCloseRequested()) {
			
			//Creating Entity data from terrain chunk data
			if(index < terrainChunks.size()) {
				RawModel chunkModel = loader.LoadTerrainToVAO(terrainChunks.get(index).positions, terrainChunks.get(index).uvs, terrainChunks.get(index).normals);
				TexturedModel texturedChunkModel = new TexturedModel(chunkModel, new ModelTexture(loader.LoadTexture("dirt")));
				ModelTexture chunkTexture = texturedChunkModel.GetTexture();
				//Prevents terrain rendering strangely
				chunkTexture.SetTransparencyState(true);	
				Entity chunkEntity = new Entity(texturedChunkModel, terrainChunks.get(index).chunk.origin, 0, 0, 0, 1, true);
				terrainEntities.add(chunkEntity);
				
				//clearing arrays (important for performance reasons)
				terrainChunks.get(index).positions = null;
				terrainChunks.get(index).normals = null;
				terrainChunks.get(index).uvs = null;
				
				index++;
			}
			
			
			//Player and Camera functions
			playerPos = player.GetPosition();
			camera.Move();
			player.Move();
			renderer.ProcessEntity(player);
			
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
		isRunning = false;
		terrainChunks.clear();
		usedPositions.clear();
		renderer.CleanUp();
		loader.CleanUp();
		DisplayManager.CloseDisplay();
	}
}
