package game;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import render.DisplayManager;
import render.Loader;
import obj.ModelData;
import obj.OBJFileLoader;
import render.Renderer;
import shaders.StaticShader;
import textures.ModelTexture;

public class MainGameLoop {

	public static void main(String[] args) {
		DisplayManager.CreateDisplay();
		Loader loader = new Loader();
		StaticShader shader = new StaticShader();
		
		Renderer renderer = new Renderer(shader);
		
		
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
		
		Camera camera = new Camera();
		
		while(!Display.isCloseRequested()) {
			entity.IncreaseRotation(0, 1, 0);
			camera.Move();
			renderer.Prepare();
			shader.Start();
			shader.LoadLight(light);
			shader.LoadViewMatrix(camera);
			renderer.Render(entity, shader);
			shader.Stop();
			DisplayManager.UpdateDisplay();
		}
		
		shader.CleanUp();
		loader.CleanUp();
		DisplayManager.CloseDisplay();
	}
}
