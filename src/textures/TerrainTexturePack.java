package textures;

public class TerrainTexturePack {

	private TerrainTexture backgroundTexture;
	private TerrainTexture rTexture;
	private TerrainTexture gTexture;
	private TerrainTexture bTexture;
	
	public TerrainTexturePack(TerrainTexture backgroundTexture, TerrainTexture rTexture, 
			TerrainTexture gTexture, TerrainTexture bTexture) {
		super();
		this.backgroundTexture = backgroundTexture;
		this.rTexture = rTexture;
		this.gTexture = gTexture;
		this.bTexture = bTexture;
	}

	public TerrainTexture GetBackgroundTexture() {
		return backgroundTexture;
	}

	public TerrainTexture GetRTexture() {
		return rTexture;
	}

	public TerrainTexture GetGTexture() {
		return gTexture;
	}

	public TerrainTexture GetBTexture() {
		return bTexture;
	}
	
	
}

