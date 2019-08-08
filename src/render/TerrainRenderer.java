package render;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import models.TexturedModel;
import shaders.TerrainShader;
import terrain.ChunkMesh;
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
	
	public void Render(List<ChunkMesh> terrains) {
		for(ChunkMesh terrain: terrains) {
			PrepareTerrain(terrain);
			LoadModelMatrix(terrain);
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, terrain.positions.length);
			UnbindTexturedModel();
		}
	}
	
	private void PrepareTerrain(ChunkMesh terrain) {
		Loader loader = new Loader();
		RawModel rawModel = loader.loadTerrainToVAO(terrain.positions, terrain.uvs, terrain.normals);
		GL30.glBindVertexArray(rawModel.GetVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		BindTextures(terrain, new TexturedModel(rawModel, new ModelTexture(loader.loadTexture("dirt"))));
		shader.LoadShine(1, 0);
		
	}
	
	private void BindTextures(ChunkMesh terrain, TexturedModel model) {
		RawModel rawModel = model.GetRawModel();
		GL30.glBindVertexArray(rawModel.GetVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		ModelTexture texture = model.GetTexture();
		
		MasterRenderer.DisableCulling();
		//if(texture.GetTransparencyState() == true) {
		//}
		//shader.LoadFakeLighting(texture.GetFakeLightingState());
		shader.LoadShine(texture.GetShineDamper(), texture.GetReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.GetTexture().GetID());
	}
	
	private void UnbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	
	private void LoadModelMatrix(ChunkMesh terrain) {
		Matrix4f transformationMatrix = Maths.CreateTransformationMatrix(
				new Vector3f(terrain.chunk.origin.x, terrain.chunk.origin.z, terrain.chunk.origin.z), 0, 0, 0, 1);
		shader.LoadTransformationMatrix(transformationMatrix);
	}
}
