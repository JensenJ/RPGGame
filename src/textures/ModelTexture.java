package textures;

public class ModelTexture {

	private int textureID;
	
	private float shineDamper = 1;
	private float reflectivity = 0;
	
	public ModelTexture(int id) {
		this.textureID = id;
	}
	public int GetID() {
		return this.textureID;
	}
	public float GetShineDamper() {
		return shineDamper;
	}
	public void SetShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}
	public float GetReflectivity() {
		return reflectivity;
	}
	public void SetReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}
	
}
