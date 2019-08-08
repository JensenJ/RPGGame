package terrain;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

public class Chunk {

	public List<Voxel> voxels;
	public Vector3f origin;
	
	public Chunk(List<Voxel> voxels, Vector3f origin) {
		this.voxels = voxels;
		this.origin = origin;
	}
}
