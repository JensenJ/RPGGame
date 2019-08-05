package game;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Light;
import entities.Player;
import models.RawModel;
import models.TexturedModel;
import render.DisplayManager;
import render.Loader;
import render.MasterRenderer;
import terrain.Terrain;
import obj.ModelData;
import obj.OBJFileLoader;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class MainGameLoop {

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
		TerrainTexture background = new TerrainTexture(loader.loadTexture("grass"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("sand"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("water"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(background, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		
		List<Terrain> terrains = new ArrayList<Terrain>();
		
		
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				terrains.add(new Terrain(i, -j -1, loader, texturePack, blendMap));
			}
		}
		
		//Terrain terrain1 = new Terrain(0, -1, loader, texturePack, blendMap);
		//Terrain terrain2 = new Terrain(-1, -1, loader, texturePack, blendMap);
		
		Player player = new Player(texturedModel, new Vector3f(0, 0, 0), 0, 0, 0, 1);
		Camera camera = new Camera(player);
		
		MasterRenderer renderer = new MasterRenderer();
		
		while(!Display.isCloseRequested()) {
			//entity.IncreaseRotation(0, 0, 0);
			
			camera.Move();
			player.Move();
			renderer.ProcessEntity(player);
			
			for(Terrain terrain:terrains) {
				renderer.ProcessTerrain(terrain);
			}
			
			
			renderer.Render(light, camera);
			
			DisplayManager.UpdateDisplay();
		}
		
		renderer.CleanUp();
		loader.CleanUp();
		DisplayManager.CloseDisplay();
	}
}
