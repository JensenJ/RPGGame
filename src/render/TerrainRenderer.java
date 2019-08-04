package render;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import shaders.TerrainShader;
import terrain.Terrain;
import textures.ModelTexture;
import utilities.Maths;

public class TerrainRenderer {

	private TerrainShader shader;
	
	public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.Start();
		shader.LoadProjectionMatrix(projectionMatrix);
		shader.Stop();
	}
	
	public void Render(List<Terrain> terrains) {
		for(Terrain terrain: terrains) {
			PrepareTerrain(terrain);
			LoadModelMatrix(terrain);
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.GetModel().GetVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			UnbindTexturedModel();
		}
	}
	
	private void PrepareTerrain(Terrain terrain) {
		RawModel rawModel = terrain.GetModel();
		GL30.glBindVertexArray(rawModel.GetVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		ModelTexture texture = terrain.GetTexture();
		shader.LoadShine(texture.GetShineDamper(), texture.GetReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.GetID());
	}
	
	private void UnbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	
	private void LoadModelMatrix(Terrain terrain) {
		Matrix4f transformationMatrix = Maths.CreateTransformationMatrix(
				new Vector3f(terrain.GetX(), 0, terrain.GetZ()), 0, 0, 0, 1);
		shader.LoadTransformationMatrix(transformationMatrix);
	}
}
