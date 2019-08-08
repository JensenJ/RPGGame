package obj;

public class ModelData {

	//Model data
	private float[] vertices;
    private float[] textureCoords;
    private float[] normals;
    private int[] indices;
    private float furthestPoint;
 
    //Constructor
    public ModelData(float[] vertices, float[] textureCoords, float[] normals, int[] indices,
            float furthestPoint) {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
        this.furthestPoint = furthestPoint;
    }
 
    //Getters and setters
    public float[] GetVertices() {
        return vertices;
    }
 
    public float[] GetTextureCoords() {
        return textureCoords;
    }
 
    public float[] GetNormals() {
        return normals;
    }
 
    public int[] GetIndices() {
        return indices;
    }
 
    public float GetFurthestPoint() {
        return furthestPoint;
    }
}
