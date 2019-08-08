package models;

public class RawModel {

	//Model data
	private int vaoID;
	private int vertexCount;
	
	//Constructor
	public RawModel(int vaoID, int vertexCount) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}
	
	//Getters and setters
	public int GetVaoID() {
		return vaoID;
	}
	
	public int GetVertexCount() {
		return vertexCount;
	}
	
}
