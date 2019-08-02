package shaders;

import org.lwjgl.util.vector.Matrix4f;

public class StaticShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/shaders/vertexShader.txt";
	private static final String FRAGMENT_FILE = "src/shaders/fragmentShader.txt";
	
	private int location_transformationMatrix;
	
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
	}
	
	public void LoadTransformationMatrix(Matrix4f matrix) {
		super.LoadMatrix(location_transformationMatrix, matrix);
	}

}
