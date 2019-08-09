package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	//Camera Clamping
	private static final float MAX_ZOOM = 70.0f;
	private static final float MIN_ZOOM = 10.0f;
	
	private static final float MAX_PITCH = 70.0f;
	private static final float MIN_PITCH = 25.0f;
	
	//Positional data
	private float distanceFromPlayer = 50;
	private float angleAroundPlayer = 0;
	
	private Vector3f position = new Vector3f(0, 1, 0);
	private float pitch, yaw, roll;
	
	private Player player;
	
	//Constructor
	public Camera(Player player) {
		this.player = player;
	}
	
	//Move Camera method
	public void Move() {
		
		//AngleAroundPlayer
		if(Keyboard.isKeyDown(Keyboard.KEY_LMENU)){
			if(Mouse.isGrabbed() == true) {
				Mouse.setGrabbed(false);
			}
			CalculateAngleAroundPlayer();
			if(Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_S) 
					|| Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_D)){
				player.IncreaseRotation(0, angleAroundPlayer, 0);
				angleAroundPlayer = 0;
			}
			//Linearly return to back of player
		}else if(!Keyboard.isKeyDown(Keyboard.KEY_LMENU)){
			angleAroundPlayer /= 1.2f;
			if(angleAroundPlayer >= -0.5f && angleAroundPlayer <= 0.5f)
				angleAroundPlayer = 0;
			if(Mouse.isGrabbed() == false) {				
				Mouse.setGrabbed(true);
			}
		}
		
		CalculateZoom();
		CalculatePitch();
		
		//Calculating Camera Position
		float horizontalDist = CalculateHorizontalDistance();
		float verticalDist = CalculateVerticalDistance();
		CalculateCameraPosition(horizontalDist, verticalDist);
		this.yaw = 180 - (player.GetRotY() + angleAroundPlayer);
	}

	//Camera position
	private void CalculateCameraPosition(float horizontalDistance, float verticalDistance) {
		float theta = player.GetRotY() + angleAroundPlayer;
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.GetPosition().x - offsetX;
		position.y = player.GetPosition().y + verticalDistance;
		position.z = player.GetPosition().z - offsetZ;
	}
	
	//Horizontal distance
	private float CalculateHorizontalDistance() {
		float horizontalDistance = (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
		if(horizontalDistance < 0)
			horizontalDistance = 0;
		return horizontalDistance;
	}
	
	//Vertical distance
	private float CalculateVerticalDistance() {
		float verticalDistance = (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
		if(verticalDistance < 0)
			verticalDistance = 0;
		return verticalDistance;
	}
	
	//Calculate zoom
	private void CalculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.025f;
		distanceFromPlayer -= zoomLevel;
		
		if(distanceFromPlayer > MAX_ZOOM) {
			distanceFromPlayer = MAX_ZOOM;
		}
		
		if(distanceFromPlayer < MIN_ZOOM) {
			distanceFromPlayer = MIN_ZOOM;
		}
	}
	
	//Calculate pitch
	private void CalculatePitch() {
		float pitchChange = Mouse.getDY() * 0.1f;
		pitch -= pitchChange;
		if(pitch < MIN_PITCH)
			pitch = MIN_PITCH;
		else if(pitch > MAX_PITCH)
			pitch = MAX_PITCH;
	}
	
	//Calculate angle around player
	private void CalculateAngleAroundPlayer() {		
		float angleChange = Mouse.getDX() * 0.3f;
		angleAroundPlayer -= angleChange;
	}
	
	//Getters and setters
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
