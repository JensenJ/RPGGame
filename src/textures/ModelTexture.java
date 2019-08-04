package textures;

public class ModelTexture {

	private int textureID;
	
	private float shineDamper = 1;
	private float reflectivity = 0;
	
	private boolean hasTransparency = false;
	private boolean hasFakeLighting = false;
	
	public boolean GetTransparencyState() {
		return hasTransparency;
	}
	public void SetTransparencyState(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
	}
	public boolean GetFakeLightingState() {
		return hasFakeLighting;
	}
	public void SetFakeLightingState(boolean hasFakeLighting) {
		this.hasFakeLighting = hasFakeLighting;
	}
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
