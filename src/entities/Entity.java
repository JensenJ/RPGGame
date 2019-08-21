package entities;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import render.DisplayManager;

public class Entity {

	//Basic Entity data
	private TexturedModel model;
	private Vector3f position;
	private float rotX, rotY, rotZ;
	private float scale;
	private boolean shouldDrawInArrays = false;
	private boolean isVisible = false;
	
	private float originalScale;
	
	//Constructor
	public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, boolean arrayDrawing) {
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = 0;
		this.shouldDrawInArrays = arrayDrawing;
		this.isVisible = false;
		this.originalScale = scale;
	}
	
	//Position Increase
	public void IncreasePosition(float x, float y, float z) {
		this.position.x+=x;
		this.position.y+=y;
		this.position.z+=z;
	}
	
	//Rotation Increase
	public void IncreaseRotation(float x, float y, float z) {
		this.rotX+=x;
		this.rotY+=y;
		this.rotZ+=z;
	}

	public void IncreaseScale(float scale) {
		this.scale+=scale;
	}
	
	public boolean Spawn() {
		//Set visible
		SetVisibility(true);
		//Scale object until it is >= the original scale
		if(this.scale < this.originalScale) {
			IncreaseScale(0.5f * DisplayManager.GetFrameTimeSeconds());
			return false;
		}else {
			//Set final settings
			SetScale(originalScale);
			return true;
		}
	}
	
	public boolean Despawn() {
		//Scale object down until scale < 0
		if(this.scale > 0) {
			IncreaseScale(-0.5f * DisplayManager.GetFrameTimeSeconds());
			return false;
		}else {
			//When scale is < 0, set final settings, removes strange scaling issues when rescaling back up
			SetScale(0);
			SetVisibility(false);
			return true;
		}
	}
	
	//Getters and Setters
	public TexturedModel GetModel() {
		return model;
	}

	public void SetModel(TexturedModel model) {
		this.model = model;
	}

	public Vector3f GetPosition() {
		return position;
	}

	public void SetPosition(Vector3f position) {
		this.position = position;
	}

	public float GetRotX() {
		return rotX;
	}

	public void SetRotX(float rotX) {
		this.rotX = rotX;
	}

	public float GetRotY() {
		return rotY;
	}

	public void SetRotY(float rotY) {
		this.rotY = rotY;
	}

	public float GetRotZ() {
		return rotZ;
	}

	public void SetRotZ(float rotZ) {
		this.rotZ = rotZ;
	}

	public float GetScale() {
		return scale;
	}

	public void SetScale(float scale) {
		this.scale = scale;
	}

	public float GetOriginalScale() {
		return originalScale;
	}
	
	public void SetOriginalScale(float originalScale) {
		this.originalScale = originalScale;
	}
	
	public boolean GetShouldDrawInArrays() {
		return shouldDrawInArrays;
	}

	public void SetShouldDrawInArrays(boolean shouldDrawInArrays) {
		this.shouldDrawInArrays = shouldDrawInArrays;
	}
	
	public boolean GetVisibility() {
		return isVisible;
	}
	
	public void SetVisibility(boolean visibility) {
		this.isVisible = visibility;
	}
	
}
