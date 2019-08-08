package terrain;

public class Voxel {

	public int x, y, z;
	
	public static enum VOXELTYPE {
		DIRT, GRASS, SAND, WATER
	};
	
	public VOXELTYPE type;
	
	public Voxel(int x, int y, int z, VOXELTYPE type) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
	}
}
