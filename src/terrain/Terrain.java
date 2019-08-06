package terrain;

import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import render.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class Terrain {

	private static final int SIZE = 64;
	
	private int seed = 0;
	private int x;
	private int z;
	private RawModel model;
	private TerrainTexturePack texturePack;
	private TerrainTexture blendMap;
	
	public Terrain(int gridX, int gridZ, int seed, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap, String heightMap) {
		this.texturePack = texturePack;
		this.blendMap = blendMap;
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		this.seed = seed;
		this.model = generateTerrain(loader, heightMap);
		
	}
	
	private RawModel generateTerrain(Loader loader, String heightMap){
		
		HeightGenerator generator = new HeightGenerator(this.seed);
		
		int VERTEX_COUNT = 16;
		
		int count = VERTEX_COUNT * VERTEX_COUNT;
		//heights = new float[VERTEX_COUNT][VERTEX_COUNT];
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
				float height = GetHeight(j, i, generator);
				vertices[vertexPointer*3+1] = height;
				//heights[j][i] = height;
				vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
				Vector3f normal = CalculateNormal(j, i, generator);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}

	private Vector3f CalculateNormal(int x, int z, HeightGenerator generator) {
		float heightL = GetHeight(x-1, z, generator);
		float heightR = GetHeight(x+1, z, generator);
		float heightD = GetHeight(x, z-1, generator);
		float heightU = GetHeight(x, z+1, generator);
		Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD - heightU);
		normal.normalise();
		return normal;
	}
	
	private float GetHeight(int x, int z, HeightGenerator generator) {
		return generator.GenerateHeight(x, z);
	}
		
	
	public int GetX() {
		return x;
	}

	public int GetZ() {
		return z;
	}

	public RawModel GetModel() {
		return model;
	}

	public TerrainTexturePack GetTexturePack() {
		return texturePack;
	}

	public TerrainTexture GetBlendMap() {
		return blendMap;
	}

	
	
	
}
