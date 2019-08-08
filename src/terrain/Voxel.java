package terrain;

public class Voxel {

	public int x, y, z;
	
	//Different voxel types
	public static enum VOXELTYPE {
		DIRT, GRASS, SAND, WATER
	};
	
	public VOXELTYPE type;
	
	//Constructor
	public Voxel(int x, int y, int z, VOXELTYPE type) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
	}
}
