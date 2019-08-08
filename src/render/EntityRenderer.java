package render;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import shaders.StaticShader;
import textures.ModelTexture;
import utilities.Maths;

public class EntityRenderer {

	private StaticShader shader;
	
	//Constructor
	public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.Start();
		shader.LoadProjectionMatrix(projectionMatrix);
		shader.Stop();
	}
	
	//Main render method
	public void Render(Map<TexturedModel, List<Entity>> entities) {
		//For each texturedModel in entity HashMap
		for(TexturedModel model:entities.keySet()) {
			//Prepares models for texturing
			PrepareTexturedModel(model);
			//Get all entities with the same model
			List<Entity> batch = entities.get(model);
			for(Entity entity:batch) {
				//Prepare instance
				PrepareInstance(entity);
				
				//Drawing method
				if(entity.GetShouldDrawInArrays()) {	
					GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.GetRawModel().GetVertexCount()/3);
				}else {
					GL11.glDrawElements(GL11.GL_TRIANGLES, model.GetRawModel().GetVertexCount(), GL11.GL_UNSIGNED_INT, 0);	
				}
			}
			//Unbinding textured model
			UnbindTexturedModel();
		}
	}
	
	//Prepared textured model
	private void PrepareTexturedModel(TexturedModel model) {
		//Enable vertex attrib arrays
		RawModel rawModel = model.GetRawModel();
		GL30.glBindVertexArray(rawModel.GetVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		//Transparency and lighting
		ModelTexture texture = model.GetTexture();
		if(texture.GetTransparencyState() == true) {
			MasterRenderer.DisableCulling();
		}
		shader.LoadFakeLighting(texture.GetFakeLightingState());
		shader.LoadShine(texture.GetShineDamper(), texture.GetReflectivity());
		//Bind textures
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.GetTexture().GetID());
	}
	
	//Unbind textures
	private void UnbindTexturedModel() {
		//Disable vertex attrib arrays
		MasterRenderer.EnableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	
	//Prepare instance and creating transformation matrix
	private void PrepareInstance(Entity entity) {
		Matrix4f transformationMatrix = Maths.CreateTransformationMatrix(
				entity.GetPosition(), 
				entity.GetRotX(), 
				entity.GetRotY(), 
				entity.GetRotZ(), 
				entity.GetScale());
		shader.LoadTransformationMatrix(transformationMatrix);
	}
	

}
