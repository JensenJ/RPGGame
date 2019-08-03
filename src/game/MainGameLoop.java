package game;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
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
		RawModel model = loader.loadToVAO(modelData.GetVertices(), modelData.GetTextureCoords(), modelData.GetIndices());
		
		ModelTexture texture = new ModelTexture(loader.loadTexture("image"));
		TexturedModel texturedModel = new TexturedModel(model, texture);
		
		Entity entity = new Entity(texturedModel, new Vector3f(0, 0, -5), 0, 0, 0, 1);
		
		Camera camera = new Camera();
		
		while(!Display.isCloseRequested()) {
			entity.IncreaseRotation(1, 0.1f, 0);
			camera.Move();
			renderer.Prepare();
			shader.Start();
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
