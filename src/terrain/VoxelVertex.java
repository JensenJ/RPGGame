package terrain;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class VoxelVertex {

	//Data
	public Vector3f positions, normals;
	public Vector2f uvCoords;
	
	//Constructor
	public VoxelVertex(Vector3f positions, Vector2f uvCoords, Vector3f normals) {
		this.positions = positions;
		this.uvCoords = uvCoords;
		this.normals = normals;
	}
}
