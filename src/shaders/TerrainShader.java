package shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Light;
import utilities.Maths;

public class TerrainShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/shaders/terrainVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/shaders/terrainFragmentShader.txt";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition;
	private int location_lightColour;
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_fogDensity;
	private int location_fogGradient;
	private int location_skyColour;
	private int location_backgroundTexture;
	private int location_rTexture;
	private int location_gTexture;
	private int location_bTexture;
	private int location_blendMap;

	
	public TerrainShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void BindAttributes() {
		super.BindAttribute(0, "position");
		super.BindAttribute(1, "textureCoords");
		super.BindAttribute(2, "normal");
	}

	@Override
	protected void GetAllUniformLocations() {
		location_transformationMatrix = super.GetUniformLocation("transformationMatrix");
		location_projectionMatrix = super.GetUniformLocation("projectionMatrix");
		location_viewMatrix = super.GetUniformLocation("viewMatrix");
		location_lightPosition = super.GetUniformLocation("lightPosition");
		location_lightColour = super.GetUniformLocation("lightColour");
		location_shineDamper = super.GetUniformLocation("shineDamper");
		location_reflectivity = super.GetUniformLocation("reflectivity");
		location_fogDensity = super.GetUniformLocation("fogDensity");
		location_fogGradient = super.GetUniformLocation("fogGradient");
		location_skyColour = super.GetUniformLocation("skyColour");
		location_backgroundTexture = super.GetUniformLocation("backgroundTexture");
		location_rTexture = super.GetUniformLocation("rTexture");
		location_gTexture = super.GetUniformLocation("gTexture");
		location_bTexture = super.GetUniformLocation("bTexture");
		location_blendMap = super.GetUniformLocation("blendMap");
		
	}
	
	public void ConnectTextureUnits() {
		super.LoadInt(location_backgroundTexture, 0);
		super.LoadInt(location_rTexture, 1);
		super.LoadInt(location_gTexture, 2);
		super.LoadInt(location_bTexture, 3);
		super.LoadInt(location_blendMap, 4);
	}
	
	public void LoadSkySettings(Vector3f skyColour, float density, float gradient) {
		super.LoadVector(location_skyColour, skyColour);
		super.LoadFloat(location_fogDensity, density);
		super.LoadFloat(location_fogGradient, gradient);
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
	
	public void LoadLight(Light light) {
		super.LoadVector(location_lightPosition, light.GetPosition());
		super.LoadVector(location_lightColour, light.GetColour());
	}
	
	public void LoadShine(float damper, float reflectivity) {
		super.LoadFloat(location_shineDamper, damper);
		super.LoadFloat(location_reflectivity, reflectivity);
	}
}
