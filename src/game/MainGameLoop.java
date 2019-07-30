package game;

import org.lwjgl.opengl.Display;

import render.DisplayManager;

public class MainGameLoop {

	public static void main(String[] args) {
		DisplayManager.CreateDisplay();
		
		while(!Display.isCloseRequested()) {
			//Game logic
			//Render
			
			DisplayManager.UpdateDisplay();
		}
		
		DisplayManager.CloseDisplay();
	}
}
