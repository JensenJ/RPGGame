package game;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.RawModel;
import models.TexturedModel;
import models.VoxelModel;
import render.DisplayManager;
import render.Loader;
import render.MasterRenderer;
import terrain.Chunk;
import terrain.ChunkMesh;
import terrain.Voxel;
import obj.ModelData;
import obj.OBJFileLoader;
import textures.ModelTexture;

public class MainGameLoop {

	static Vector3f playerPos = new Vector3f(0, 0, 0);
	static final int RENDER_DISTANCE = 8 * 16;
	static boolean isRunning = true;
	
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
		
		//Terrain
		
		RawModel voxel = loader.loadToVAO(
				VoxelModel.vertices, 
				VoxelModel.uv, 
				VoxelModel.normals, 
				VoxelModel.indices);
		
		TexturedModel texturedChunkModel = new TexturedModel(voxel, new ModelTexture(loader.loadTexture("dirt")));
		ModelTexture chunkTexture = texturedChunkModel.GetTexture();
		
		List<Voxel> voxels = new ArrayList<Voxel>();
		
		for(int x = 0; x < 16; x++) {
			for(int y = 0; y < 16; y++) {
				for(int z = 0; z < 16; z++) {
					voxels.add(new Voxel(x, y, z, Voxel.VOXELTYPE.DIRT));
				}
			}
		}
		
		Chunk chunk = new Chunk(voxels, new Vector3f(0, 0, 0));
		ChunkMesh mesh = new ChunkMesh(chunk);
		
		// MAIN LOOP
		while(!Display.isCloseRequested()) {
			
			playerPos = player.GetPosition();
			
			camera.Move();
			player.Move();
			renderer.ProcessEntity(player);
			renderer.ProcessTerrain(mesh);
			
//			for(int i = 0; i < terrainChunks.size(); i++) {
//				
//				Vector3f origin = terrainChunks.get(i).GetOrigin();
//				
//				int distX = (int) (playerPos.x - origin.x);
//				int distZ = (int) (playerPos.z - origin.z);
//				
//				if(distX < 0) {
//					distX = -distX;
//				}
//				
//				if(distZ < 0) {
//					distZ = -distZ;
//				}
//				
//				if((distX <= RENDER_DISTANCE) && (distZ <= RENDER_DISTANCE)) {
//					for(int j = 0; j < terrainChunks.get(i).GetVoxels().size(); j++) {
//						renderer.ProcessEntity(terrainChunks.get(i).GetVoxels().get(j));
//					}
//				}
//				
//				
//			}
			
			
			renderer.Render(light, camera);
			
			DisplayManager.UpdateDisplay();
		}
		
		isRunning = false;
		renderer.CleanUp();
		loader.CleanUp();
		DisplayManager.CloseDisplay();
	}
}
