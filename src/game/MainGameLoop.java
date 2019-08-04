package game;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import render.DisplayManager;
import render.Loader;
import render.MasterRenderer;
import terrain.Terrain;
import obj.ModelData;
import obj.OBJFileLoader;
import textures.ModelTexture;

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
		
		//Entity entity = new Entity(texturedModel, new Vector3f(0.5f, 0.5f, -0.5f), 0, 0, 0, 1);
		//Entity entity1 = new Entity(texturedModel, new Vector3f(1.5f, 0.5f, -0.5f), 0, 0, 0, 1);
		
		List<Entity> allCubes = new ArrayList<Entity>();
		
		for(int i = 0; i < 16; i++) {
			for(int j = 0; j < 16; j++) {
				allCubes.add(new Entity(texturedModel, new Vector3f(i + 0.5f, 1.5f, -j - 0.5f), 0, 0, 0, 1));
			}
		}
		
		Terrain terrain1 = new Terrain(0, -1, loader, new ModelTexture(loader.loadTexture("dirt")));
		//Terrain terrain2 = new Terrain(-1, -1, loader, new ModelTexture(loader.loadTexture("image")));
		
		Camera camera = new Camera();
		
		MasterRenderer renderer = new MasterRenderer();
		
		while(!Display.isCloseRequested()) {
			//entity.IncreaseRotation(0, 0, 0);
			
			camera.Move(0.1f);

			for(Entity cube : allCubes) {
				renderer.ProcessEntity(cube);
			}
			
			renderer.ProcessTerrain(terrain1);
			//renderer.ProcessTerrain(terrain2);
			
			
			renderer.Render(light, camera);
			
			DisplayManager.UpdateDisplay();
		}
		
		renderer.CleanUp();
		loader.CleanUp();
		DisplayManager.CloseDisplay();
	}
}
