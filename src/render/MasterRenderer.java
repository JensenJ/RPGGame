package render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import shaders.StaticShader;

public class MasterRenderer {

	private StaticShader shader = new StaticShader();
	private Renderer renderer = new Renderer(shader);
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	
	public void Render(Light sun, Camera camera) {
		renderer.Prepare();
		shader.Start();
		shader.LoadLight(sun);
		shader.LoadViewMatrix(camera);
		renderer.Render(entities);
		shader.Stop();
		entities.clear();
	}
	
	public void ProcessEntity(Entity entity) {
		TexturedModel entityModel = entity.GetModel();
		List<Entity> batch = entities.get(entityModel);
		if(batch != null) {
			batch.add(entity);
		}else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}
	
	public void CleanUp() {
		shader.CleanUp();
	}
}
