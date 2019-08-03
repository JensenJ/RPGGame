package entities;

import org.lwjgl.util.vector.Vector3f;

public class Light {

	private Vector3f position;
	private Vector3f colour;
	
	public Light(Vector3f position, Vector3f colour) {
		this.position = position;
		this.colour = colour;
	}

	public Vector3f GetPosition() {
		return position;
	}

	public void SetPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f GetColour() {
		return colour;
	}

	public void SetColour(Vector3f colour) {
		this.colour = colour;
	}
	
	
}
