package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import render.DisplayManager;

public class Player extends Entity {

	private static final float RUN_SPEED = 15;
	private static final float SPRINT_SPEED = 25;
	private static final float TURN_SPEED = 1.6f;
	private static final float GRAVITY = -50;
	private static final float JUMP_POWER = 20;
	
	private static final float TERRAIN_HEIGHT = 0;
	
	private float currentForwardSpeed = 0;
	private float currentSideSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	
	private float playerSpeed = RUN_SPEED;
	
	private boolean isInAir = false;
	
	private float yForward = 0;
	private float yRight = 90;
	
	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale, false);
	}
	
	public void Move() {
		CheckInputs();
		
		super.IncreaseRotation(0, currentTurnSpeed * DisplayManager.GetFrameTimeSeconds(), 0);
		
		float forwardDistance = currentForwardSpeed * DisplayManager.GetFrameTimeSeconds();
		float fx = (float) (forwardDistance * Math.sin(Math.toRadians(yForward)));
		float fz = (float) (forwardDistance * Math.cos(Math.toRadians(yForward)));
		super.IncreasePosition(fx, 0, fz);
		
		float sideDistance = currentSideSpeed * DisplayManager.GetFrameTimeSeconds();
		float sx = (float) (sideDistance * Math.sin(Math.toRadians(yRight)));
		float sz = (float) (sideDistance * Math.cos(Math.toRadians(yRight)));
		super.IncreasePosition(sx, 0, sz);
		
		upwardsSpeed += GRAVITY * DisplayManager.GetFrameTimeSeconds();
		super.IncreasePosition(0, upwardsSpeed * DisplayManager.GetFrameTimeSeconds(), 0);
		if(super.GetPosition().y < TERRAIN_HEIGHT) {
			upwardsSpeed = 0;
			isInAir = false;
			super.GetPosition().y = TERRAIN_HEIGHT;
		}
	}
	
	private void Jump() {
		if(!isInAir) {
			this.upwardsSpeed = JUMP_POWER;
			isInAir = true;
		}
	}

	private void CheckInputs() {
		
		this.currentForwardSpeed = 0;
		this.currentSideSpeed = 0;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			playerSpeed = SPRINT_SPEED;
		}else {
			playerSpeed = RUN_SPEED;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			yForward = super.GetRotY();
			this.currentForwardSpeed = playerSpeed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			yForward = super.GetRotY();
			this.currentForwardSpeed = -playerSpeed;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
			yRight = super.GetRotY() + 90;
			this.currentSideSpeed = playerSpeed;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			yRight = super.GetRotY() + 90;
			this.currentSideSpeed = -playerSpeed;
		}
		
		this.currentTurnSpeed = Mouse.getDX() * (-TURN_SPEED * 10.0f);
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			Jump();
		}
	}
	
	public boolean isPlayerMoving() {
		if(currentForwardSpeed > 0 || currentSideSpeed > 0) {
			return true;
		}else {
			return false;
		}
	}
}
