package models;

import textures.ModelTexture;

public class TexturedModel {

	//Model data
	private RawModel rawModel;
	private ModelTexture texture;
	
	//Constructor
	public TexturedModel(RawModel model, ModelTexture texture) {
		this.rawModel = model;
		this.texture = texture;
	}
	
	//Getters and setters
	public RawModel GetRawModel() {
		return rawModel;
	}
	
	public ModelTexture GetTexture() {
		return texture;
	}
}
