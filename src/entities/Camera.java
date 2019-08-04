package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	private Vector3f position = new Vector3f(0, 1, 0);
	private float pitch, yaw, roll;
	
	public void Move(float moveSpeed) {
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			position.z -= moveSpeed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			position.z += moveSpeed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
			position.x += moveSpeed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
			position.x -= moveSpeed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			position.y += moveSpeed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			position.y -= moveSpeed;
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
