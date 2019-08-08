package entities;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;

public class Entity {

	private TexturedModel model;
	private Vector3f position;
	private float rotX, rotY, rotZ;
	private float scale;
	
	private boolean ShouldDrawInArrays = false;
	
	public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, boolean arrayDrawing) {
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.ShouldDrawInArrays = arrayDrawing;
	}
	
	public void IncreasePosition(float x, float y, float z) {
		this.position.x+=x;
		this.position.y+=y;
		this.position.z+=z;
	}
	
	public void IncreaseRotation(float x, float y, float z) {
		this.rotX+=x;
		this.rotY+=y;
		this.rotZ+=z;
	}

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

	public boolean GetShouldDrawInArrays() {
		return ShouldDrawInArrays;
	}

	public void SetShouldDrawInArrays(boolean shouldDrawInArrays) {
		ShouldDrawInArrays = shouldDrawInArrays;
	}
	
	
}
