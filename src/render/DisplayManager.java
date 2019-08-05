package render;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {

	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	public static final int FPS = 120;
	public static final String TITLE = "Voxel RPG";
	
	private static long lastFrameTime;
	private static float deltaTime;
	
	public static void CreateDisplay() {
		
		ContextAttribs attribs = new ContextAttribs(3,2).withForwardCompatible(true).withProfileCore(true);
		
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat(), attribs);
			Display.setTitle(TITLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		lastFrameTime = GetCurrentTime();
	}
	
	public static void UpdateDisplay() {
		Display.sync(FPS);
		Display.update();
		long currentFrameTime = GetCurrentTime();
		deltaTime = (currentFrameTime - lastFrameTime) / 1000.0f;
		lastFrameTime = currentFrameTime;
	}
	
	public static float GetFrameTimeSeconds() {
		return deltaTime;
	}
	
	public static void CloseDisplay() {
		Display.destroy();
	}
	
	private static long GetCurrentTime() {
		return Sys.getTime()*1000/Sys.getTimerResolution();
	}
}
