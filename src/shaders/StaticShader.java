package shaders;

import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import utilities.Maths;

public class StaticShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/shaders/vertexShader.txt";
	private static final String FRAGMENT_FILE = "src/shaders/fragmentShader.txt";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	
	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void BindAttributes() {
		super.BindAttribute(0, "position");
		super.BindAttribute(1, "textureCoords");
	}

	@Override
	protected void GetAllUniformLocations() {
		location_transformationMatrix = super.GetUniformLocation("transformationMatrix");
		location_projectionMatrix = super.GetUniformLocation("projectionMatrix");
		location_viewMatrix = super.GetUniformLocation("viewMatrix");
	}
	
	public void LoadTransformationMatrix(Matrix4f matrix) {
		super.LoadMatrix(location_transformationMatrix, matrix);
	}
	
	public void LoadProjectionMatrix(Matrix4f matrix) {
		super.LoadMatrix(location_projectionMatrix, matrix);
	}
	
	public void LoadViewMatrix(Camera camera) {
		Matrix4f matrix = Maths.CreateViewMatrix(camera);
		super.LoadMatrix(location_viewMatrix, matrix);
	}

}
