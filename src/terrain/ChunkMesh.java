package terrain;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import models.VoxelModel;

public class ChunkMesh {

	private List<Vertex> vertices;
	
	private List<Float> positionList;
	private List<Float> uvList;
	private List<Float> normalList;
	
	public float[] positions, uvs, normals;
	
	public Chunk chunk;
	
	public ChunkMesh(Chunk chunk) {
		this.chunk = chunk;
		vertices = new ArrayList<Vertex>();
		positionList = new ArrayList<Float>();
		uvList = new ArrayList<Float>();
		normalList = new ArrayList<Float>();
		
		BuildMesh();
		PopulateLists();
	}
	
	public void Update(Chunk chunk) {
		this.chunk = chunk;
		
		BuildMesh();
		PopulateLists();
	}
	
	private void BuildMesh() {
		for(int i = 0; i < chunk.voxels.size(); i++) {
			Voxel voxeli = chunk.voxels.get(i);
			
			boolean px = false, nx = false, py = false, ny = false, pz = false, nz = false;
			
			for(int j = 0; j < chunk.voxels.size(); j++) {
				Voxel voxelj = chunk.voxels.get(j);
				
				if(((voxeli.x + 1) == (voxelj.x)) && ((voxeli.y) == (voxelj.y)) && ((voxeli.z) == (voxelj.z))) {
					px = true;
				}
				if(((voxeli.x - 1) == (voxelj.x)) && ((voxeli.y) == (voxelj.y)) && ((voxeli.z) == (voxelj.z))) {
					nx = true;
				}
				if(((voxeli.x) == (voxelj.x)) && ((voxeli.y + 1) == (voxelj.y)) && ((voxeli.z) == (voxelj.z))) {
					py = true;
				}
				if(((voxeli.x) == (voxelj.x)) && ((voxeli.y - 1) == (voxelj.y)) && ((voxeli.z) == (voxelj.z))) {
					ny = true;
				}
				if(((voxeli.x) == (voxelj.x)) && ((voxeli.y) == (voxelj.y)) && ((voxeli.z + 1) == (voxelj.z))) {
					pz = true;
				}
				if(((voxeli.x) == (voxelj.x)) && ((voxeli.y) == (voxelj.y)) && ((voxeli.z - 1) == (voxelj.z))) {
					nz = true;
				}
			}
			
			if(!px) {
				for(int k = 0; k < 6; k++) {
					vertices.add(new Vertex(new Vector3f(
							VoxelModel.PX_POS[k].x + voxeli.x,
							VoxelModel.PX_POS[k].y + voxeli.y,
							VoxelModel.PX_POS[k].z + voxeli.z),
							VoxelModel.UV[k],
							VoxelModel.NORMALS[k]));
				}
			}
			if(!nx) {
				for(int k = 0; k < 6; k++) {
					vertices.add(new Vertex(new Vector3f(
							VoxelModel.NX_POS[k].x + voxeli.x,
							VoxelModel.NX_POS[k].y + voxeli.y,
							VoxelModel.NX_POS[k].z + voxeli.z),
							VoxelModel.UV[k],
							VoxelModel.NORMALS[k]));
				}
			}
			if(!py) {
				for(int k = 0; k < 6; k++) {
					vertices.add(new Vertex(new Vector3f(
							VoxelModel.PY_POS[k].x + voxeli.x,
							VoxelModel.PY_POS[k].y + voxeli.y,
							VoxelModel.PY_POS[k].z + voxeli.z),
							VoxelModel.UV[k],
							VoxelModel.NORMALS[k]));
				}
			}
			if(!ny) {
				for(int k = 0; k < 6; k++) {
					vertices.add(new Vertex(new Vector3f(
							VoxelModel.NY_POS[k].x + voxeli.x,
							VoxelModel.NY_POS[k].y + voxeli.y,
							VoxelModel.NY_POS[k].z + voxeli.z),
							VoxelModel.UV[k],
							VoxelModel.NORMALS[k]));
				}
			}
			if(!pz) {
				for(int k = 0; k < 6; k++) {
					vertices.add(new Vertex(new Vector3f(
							VoxelModel.PZ_POS[k].x + voxeli.x,
							VoxelModel.PZ_POS[k].y + voxeli.y,
							VoxelModel.PZ_POS[k].z + voxeli.z),
							VoxelModel.UV[k],
							VoxelModel.NORMALS[k]));
				}
			}
			if(!nz) {
				for(int k = 0; k < 6; k++) {
					vertices.add(new Vertex(new Vector3f(
							VoxelModel.NZ_POS[k].x + voxeli.x,
							VoxelModel.NZ_POS[k].y + voxeli.y,
							VoxelModel.NZ_POS[k].z + voxeli.z),
							VoxelModel.UV[k],
							VoxelModel.NORMALS[k]));
				}
			}
		}
	}
	
	private void PopulateLists() {
		for(int i = 0; i < vertices.size(); i++) {
			
			positionList.add(vertices.get(i).positions.x);
			positionList.add(vertices.get(i).positions.y);
			positionList.add(vertices.get(i).positions.z);
			
			uvList.add(vertices.get(i).uvCoords.x);
			uvList.add(vertices.get(i).uvCoords.y);
			
			normalList.add(vertices.get(i).normals.x);
			normalList.add(vertices.get(i).normals.y);
			normalList.add(vertices.get(i).normals.z);
			
		}
		
		positions = new float[positionList.size()];
		uvs = new float[uvList.size()];
		normals = new float[normalList.size()];
		
		for(int i = 0; i < positionList.size(); i++) {
			positions[i] = positionList.get(i);
		}
		
		for(int i = 0; i < uvList.size(); i++) {
			uvs[i] = uvList.get(i);
		}
		
		for(int i = 0; i < normalList.size(); i++) {
			normals[i] = normalList.get(i);
		}
		
		positionList.clear();
		uvList.clear();
		normalList.clear();
	}
}