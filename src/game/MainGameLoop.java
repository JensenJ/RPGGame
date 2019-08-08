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

	private static Vector3f playerPos = new Vector3f(0, 0, 0);
	private static final int CHUNK_SIZE = 16;
	private static final int RENDER_DISTANCE = 5 * CHUNK_SIZE;
	private static boolean isRunning = true;
	
	public static void main(String[] args) {
		DisplayManager.CreateDisplay();
		Loader loader = new Loader();
		
		ModelData modelData = OBJFileLoader.LoadOBJ("TestCube");
		
		RawModel playerModel = loader.loadToVAO(
				modelData.GetVertices(),
				modelData.GetTextureCoords(),
				modelData.GetNormals(),
				modelData.GetIndices());
		
		TexturedModel texturedModel = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("dirt")));
		ModelTexture texture = texturedModel.GetTexture();
		texture.SetShineDamper(5);
		texture.SetReflectivity(0.1f);
		
		//texture.SetTransparencyState(true);
		//texture.SetFakeLightingState(true);
		
		Light light = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(1, 1, 1));
	
		Player player = new Player(texturedModel, new Vector3f(0, 0, 0), 0, 180, 0, 1);
		Camera camera = new Camera(player);
		
		MasterRenderer renderer = new MasterRenderer();
		
		List<ChunkMesh> terrainChunks = Collections.synchronizedList(new ArrayList<ChunkMesh>());
		List<Vector3f> usedPositions = Collections.synchronizedList(new ArrayList<Vector3f>());
		
		HeightGenerator chunkHeightGenerator = new HeightGenerator();
		
		new Thread(new Runnable() {
			
			public void run() {
				while(isRunning) {
					for(int x = (int) (playerPos.x - RENDER_DISTANCE) / CHUNK_SIZE; x < (playerPos.x + RENDER_DISTANCE) / CHUNK_SIZE; x++) {
						for(int z = (int) (playerPos.z - RENDER_DISTANCE) / CHUNK_SIZE; z < (playerPos.z + RENDER_DISTANCE) / CHUNK_SIZE; z++) {
							if(!usedPositions.contains(new Vector3f(x * CHUNK_SIZE, 0 * CHUNK_SIZE, z * CHUNK_SIZE))) {
								
								List<Voxel> voxels = new ArrayList<Voxel>();
								
								for(int i = 0; i < CHUNK_SIZE; i++) {
									for(int j = 0; j < CHUNK_SIZE; j++) {
										voxels.add(new Voxel(i, (int)chunkHeightGenerator.generateHeight(i + (x * CHUNK_SIZE), j + (z * CHUNK_SIZE)), j, Voxel.VOXELTYPE.DIRT));
									}
								}
								
								Chunk chunk = new Chunk(voxels, new Vector3f(x * CHUNK_SIZE, 0 * CHUNK_SIZE, z * CHUNK_SIZE));
								terrainChunks.add(new ChunkMesh(chunk));
								
								usedPositions.add(chunk.origin);
								voxels.clear();
							}
						}
					}
				}
			}
		}).start();
		
		List<Entity> terrainEntities = new ArrayList<Entity>();
		
		// MAIN LOOP
		int index = 0;
		while(!Display.isCloseRequested()) {
			
			if(index < terrainChunks.size()) {
				RawModel chunkModel = loader.loadTerrainToVAO(terrainChunks.get(index).positions, terrainChunks.get(index).uvs, terrainChunks.get(index).normals);
				TexturedModel texturedChunkModel = new TexturedModel(chunkModel, new ModelTexture(loader.loadTexture("dirt")));
				Entity chunkEntity = new Entity(texturedChunkModel, terrainChunks.get(index).chunk.origin, 0, 0, 0, 1);
				terrainEntities.add(chunkEntity);
				
				terrainChunks.get(index).positions = null;
				terrainChunks.get(index).normals = null;
				terrainChunks.get(index).uvs = null;
				
				index++;
			}
			
			playerPos = player.GetPosition();
			
			camera.Move();
			player.Move();
			renderer.ProcessEntity(player);
			
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
			
			
			renderer.Render(light, camera);
			
			DisplayManager.UpdateDisplay();
		}
		
		isRunning = false;
		terrainChunks.clear();
		usedPositions.clear();
		renderer.CleanUp();
		loader.CleanUp();
		DisplayManager.CloseDisplay();
	}
}
