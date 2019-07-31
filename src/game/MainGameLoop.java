package game;

import org.lwjgl.opengl.Display;

import render.DisplayManager;
import render.Loader;
import render.RawModel;
import render.Renderer;
import shaders.StaticShader;

public class MainGameLoop {

	public static void main(String[] args) {
		DisplayManager.CreateDisplay();
		Loader loader = new Loader();
		Renderer renderer = new Renderer();
		
		StaticShader shader = new StaticShader();
		
		float[] vertices = {
				-0.5f, 0.5f, 0f,
				-0.5f, -0.5f, 0f,
				0.5f, -0.5f, 0f,
				0.5f, 0.5f, 0f,
		};
		
		int[] indices = {
				0, 1, 3,
				3, 1, 2
		};
		
		RawModel model = loader.loadToVAO(vertices, indices);
		
		while(!Display.isCloseRequested()) {
			
			renderer.Prepare();
			shader.Start();
			renderer.Render(model);
			shader.Stop();
			DisplayManager.UpdateDisplay();
		}
		
		shader.CleanUp();
		loader.CleanUp();
		DisplayManager.CloseDisplay();
	}
}
