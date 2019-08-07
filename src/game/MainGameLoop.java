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
import obj.ModelData;
import obj.OBJFileLoader;
import textures.ModelTexture;

public class MainGameLoop {

	static Vector3f playerPos = new Vector3f(0, 0, 0);
	static final int RENDER_DISTANCE = 5 * 16;
	static boolean isRunning = true;
	
	public static void main(String[] args) {
		DisplayManager.CreateDisplay();
		Loader loader = new Loader();
		
		ModelData modelData = OBJFileLoader.LoadOBJ("TestCube");
		RawModel model = loader.loadToVAO(
				modelData.GetVertices(), 
				modelData.GetTextureCoords(), 
				modelData.GetNormals(), 
				modelData.GetIndices());
		
		TexturedModel texturedModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("dirt")));
		ModelTexture texture = texturedModel.GetTexture();
		texture.SetShineDamper(5);
		texture.SetReflectivity(0.1f);
		
		//texture.SetTransparencyState(true);
		//texture.SetFakeLightingState(true);
		
		Light light = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(1, 1, 1));
		
		//Entity entity1 = new Entity(texturedModel, new Vector3f(0.5f, 0.5f, -0.5f), 0, 0, 0, 1);
		//Entity entity2 = new Entity(texturedModel, new Vector3f(1.5f, 0.5f, -0.5f), 0, 0, 0, 1);
		
		//Terrain Texturing
//		TerrainTexture background = new TerrainTexture(loader.loadTexture("grass"));
//		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
//		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("sand"));
//		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("water"));
//		
//		TerrainTexturePack texturePack = new TerrainTexturePack(background, rTexture, gTexture, bTexture);
//		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
//		
//		List<Terrain> terrains = new ArrayList<Terrain>();
//		
//		Random random = new Random();
//		int seed = random.nextInt(1000000000);
		
//		for(int i = 0; i < 8; i++) {
//			for(int j = 0; j < 8; j++) {
//				terrains.add(new Terrain(i, j, seed, loader, texturePack, blendMap));
//			}
//		}
		
		//Terrain terrain1 = new Terrain(0, -1, loader, texturePack, blendMap);
		//Terrain terrain2 = new Terrain(-1, -1, loader, texturePack, blendMap);
		
		Player player = new Player(texturedModel, new Vector3f(0, 0, 0), 0, 180, 0, 1);
		Camera camera = new Camera(player);
		
		MasterRenderer renderer = new MasterRenderer();
		
		//TERRAIN
		List<Chunk> terrainChunks = Collections.synchronizedList(new ArrayList<Chunk>());
		List<Vector3f> usedPositions = Collections.synchronizedList(new ArrayList<Vector3f>());
		
		new Thread(new Runnable() {
			
			public void run() {
				while(isRunning) {
					for(int x = (int) (playerPos.x - RENDER_DISTANCE) / 16; x < (playerPos.x + RENDER_DISTANCE) / 16; x++) {
						for(int z = (int) (playerPos.z - RENDER_DISTANCE) / 16; z < (playerPos.z + RENDER_DISTANCE) / 16; z++) {
							if(!usedPositions.contains(new Vector3f(x * 16, 0 * 16, z * 16))) {
								
								List<Entity> voxels = new ArrayList<Entity>();
								
								for(int i = 0; i < 16; i++) {
									for(int j = 0; j < 16; j++) {
										voxels.add(new Entity(texturedModel, new Vector3f((x * 16) + i, 0, (z * 16) + j), 0, 0, 0, 1));
									}
								}
								
								terrainChunks.add(new Chunk(voxels, new Vector3f(x * 16, 0 * 16, z * 16)));
								
								usedPositions.add(new Vector3f(x * 16, 0, z * 16));
							}
						}
					}
				}
			}
			
		}).start();
		
		while(!Display.isCloseRequested()) {
			
			playerPos = player.GetPosition();
			
			camera.Move();
			player.Move();
			renderer.ProcessEntity(player);
			
			for(int i = 0; i < terrainChunks.size(); i++) {
				
				Vector3f origin = terrainChunks.get(i).GetOrigin();
				
				int distX = (int) (playerPos.x - origin.x);
				int distZ = (int) (playerPos.z - origin.z);
				
				if(distX < 0) {
					distX = -distX;
				}
				
				if(distZ < 0) {
					distZ = -distZ;
				}
				
				if((distX <= RENDER_DISTANCE) && (distZ <= RENDER_DISTANCE)) {
					for(int j = 0; j < terrainChunks.get(i).GetVoxels().size(); j++) {
						renderer.ProcessEntity(terrainChunks.get(i).GetVoxels().get(j));
					}
				}
				
				
			}
			
			
			renderer.Render(light, camera);
			
			DisplayManager.UpdateDisplay();
		}
		
		isRunning = false;
		renderer.CleanUp();
		loader.CleanUp();
		DisplayManager.CloseDisplay();
	}
}
