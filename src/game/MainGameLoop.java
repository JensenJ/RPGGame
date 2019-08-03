package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
		
		TexturedModel texturedModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("image")));
		ModelTexture texture = texturedModel.GetTexture();
		texture.SetShineDamper(10);
		texture.SetReflectivity(1);
		
		Entity entity = new Entity(texturedModel, new Vector3f(0, 0, -25), 0, 0, 0, 1);
		Light light = new Light(new Vector3f(0, 0, -20), new Vector3f(1, 1, 1));
		
		List<Entity> allCubes = new ArrayList<Entity>();
		Random random = new Random();
		
		for(int i = 0; i < 200; i++) {
			float x = random.nextFloat() * 100 - 50;
			float y = random.nextFloat() * 100 - 50;
			float z = random.nextFloat() * -300;
			allCubes.add(new Entity(texturedModel, new Vector3f(x, y, z), random.nextFloat() * 180, random.nextFloat() * 180, 0, 1));
		}
		
		Camera camera = new Camera();
		
		MasterRenderer renderer = new MasterRenderer();
		
		while(!Display.isCloseRequested()) {
			entity.IncreaseRotation(0, 1, 0);
			camera.Move();

			for(Entity cube : allCubes) {
				
				renderer.ProcessEntity(cube);
			}
			
			renderer.Render(light, camera);
			
			DisplayManager.UpdateDisplay();
		}
		
		renderer.CleanUp();
		loader.CleanUp();
		DisplayManager.CloseDisplay();
	}
}
