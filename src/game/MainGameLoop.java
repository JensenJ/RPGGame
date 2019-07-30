package game;

import org.lwjgl.opengl.Display;

import render.DisplayManager;
import render.Loader;
import render.RawModel;
import render.Renderer;

public class MainGameLoop {

	public static void main(String[] args) {
		DisplayManager.CreateDisplay();
		Loader loader = new Loader();
		Renderer renderer = new Renderer();
		
		float[] vertices = {
				-0.5f, 0.5f, 0f,
				-0.5f, -0.5f, 0f,
				0.5f, -0.5f, 0f,
				
				0.5f, -0.5f, 0f,
				0.5f, 0.5f, 0f,
				-0.5f, 0.5f, 0f
		};
		
		RawModel model = loader.loadToVAO(vertices);
		
		while(!Display.isCloseRequested()) {
			renderer.Prepare();
			//Game logic
			//Render
			renderer.Render(model);
			
			
			DisplayManager.UpdateDisplay();
		}
		
		loader.CleanUp();
		DisplayManager.CloseDisplay();
	}
}
