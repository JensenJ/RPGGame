package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	private Vector3f position = new Vector3f(0, 0, 0);
	private float pitch, yaw, roll;
	
	public void Move() {
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			position.z -= 0.02f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			position.z += 0.02f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
			position.x -= 0.02f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
			position.x += 0.02f;
		}
	}

	public Vector3f GetPosition() {
		return position;
	}

	public float GetPitch() {
		return pitch;
	}

	public float GetYaw() {
		return yaw;
	}

	public float GetRoll() {
		return roll;
	}
	
	
}
