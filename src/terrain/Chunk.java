package terrain;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;

public class Chunk {

	private List<Entity> voxels;
	private Vector3f origin;
	
	public Chunk(List<Entity> voxels, Vector3f origin) {
		this.voxels = voxels;
		this.origin = origin;
	}

	public List<Entity> GetVoxels() {
		return voxels;
	}

	public void SetVoxels(List<Entity> voxels) {
		this.voxels = voxels;
	}

	public Vector3f GetOrigin() {
		return origin;
	}

}
