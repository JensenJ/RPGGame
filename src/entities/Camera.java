package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	private float distanceFromPlayer = 50;
	private float angleAroundPlayer = 0;
	
	private Vector3f position = new Vector3f(0, 1, 0);
	private float pitch, yaw, roll;
	
	private Player player;
	
	public Camera(Player player) {
		this.player = player;
	}
	
	public void Move() {
		CalculateZoom();
		CalculatePitch();
		
		if(Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
			CalculateAngleAroundPlayer();
		}else {
			angleAroundPlayer = 0;
		}
		
		float horizontalDist = CalculateHorizontalDistance();
		float verticalDist = CalculateVerticalDistance();
		CalculateCameraPosition(horizontalDist, verticalDist);
		this.yaw = 180 - (player.GetRotY() + angleAroundPlayer);
	}

	private void CalculateCameraPosition(float horizontalDistance, float verticalDistance) {
		float theta = player.GetRotY() + angleAroundPlayer;
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.GetPosition().x - offsetX;
		position.y = player.GetPosition().y + verticalDistance;
		position.z = player.GetPosition().z - offsetZ;
	}
	
	private float CalculateHorizontalDistance() {
		float horizontalDistance = (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
		if(horizontalDistance < 0)
			horizontalDistance = 0;
		return horizontalDistance;
	}
	
	
	private float CalculateVerticalDistance() {
		float verticalDistance = (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
		if(verticalDistance < 0)
			verticalDistance = 0;
		return verticalDistance;
	}
	
	private void CalculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.025f;
		distanceFromPlayer -= zoomLevel;
	}
	
	private void CalculatePitch() {
		float pitchChange = Mouse.getDY() * 0.1f;
		pitch -= pitchChange;
		if(pitch < 25)
			pitch = 25;
		else if(pitch > 70)
			pitch = 70;
	}
	
	private void CalculateAngleAroundPlayer() {		
		float angleChange = Mouse.getDX() * 0.3f;
		angleAroundPlayer -= angleChange;
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
