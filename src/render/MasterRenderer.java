package render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import shaders.StaticShader;

public class MasterRenderer {

	//Renderer settings
	private static final float FOV = 70.0f;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000.0f;
	private static final Vector3f SKY_COLOUR = new Vector3f(0.5f, 0.8f, 0.8f);
	private static final float FOG_DENSITY = 0.0035f;
	private static final float FOG_GRADIENT = 5.0f;
	
	//References and lists
	private Matrix4f projectionMatrix;
	private StaticShader shader = new StaticShader();
	private EntityRenderer renderer;
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	
	//Constructor
	public MasterRenderer() {
		EnableCulling();
		CreateProjectionMatrix();
		renderer = new EntityRenderer(shader, projectionMatrix);
	}
	
	//Enable culling
	public static void EnableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	//Disable culling
	public static void DisableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	//Main render methods
	public void Render(Light sun, Camera camera) {
		Prepare();
		
		//Shader loading
		shader.Start();
		shader.LoadSkySettings(SKY_COLOUR, FOG_DENSITY, FOG_GRADIENT);
		shader.LoadLight(sun);
		shader.LoadViewMatrix(camera);
		renderer.Render(entities);
		shader.Stop();
		
		//Clear entity list
		entities.clear();
	}
	
	
	//Process entity
	public void ProcessEntity(Entity entity) {
		TexturedModel entityModel = entity.GetModel();
		//Batch rendering
		List<Entity> batch = entities.get(entityModel);
		if(batch != null) {
			batch.add(entity);
		}else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}
	
	//Prepare renderer
	public void Prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(SKY_COLOUR.x, SKY_COLOUR.y, SKY_COLOUR.z, 1);
		
	}
	
	//Creating projection matrix
	private void CreateProjectionMatrix() {
		//Calculations
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float yScale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2.0f))) * aspectRatio);
		float xScale = yScale / aspectRatio;
		float frustumLength = FAR_PLANE - NEAR_PLANE;
		
		//Projection matrix configuration
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = xScale;
		projectionMatrix.m11 = yScale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustumLength);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustumLength);
		projectionMatrix.m33 = 0;
	}
	
	//Cleanup
	public void CleanUp() {
		shader.CleanUp();
	}
}
