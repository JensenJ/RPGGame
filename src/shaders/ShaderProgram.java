package shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public abstract class ShaderProgram {
	
	//IDs
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

	//Constructor
	public ShaderProgram(String vertexFile, String fragmentFile) {
		//Loading shader with file paths
		vertexShaderID = LoadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		fragmentShaderID = LoadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		programID = GL20.glCreateProgram();
		
		//Attaching and linking shaders
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		BindAttributes();
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		
		//Get uniform locations in all child programs
		GetAllUniformLocations();
	}
	
	protected abstract void GetAllUniformLocations();
	
	//Getting uniform location
	protected int GetUniformLocation(String uniformName) {
		return GL20.glGetUniformLocation(programID, uniformName);
	}
	
	//Start program
	public void Start() {
		GL20.glUseProgram(programID);
	}
	
	//Stop program
	public void Stop() {
		GL20.glUseProgram(0);
	}
	
	//Clean up program
	public void CleanUp() {
		Stop();
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		GL20.glDeleteProgram(programID);
	}
	
	protected abstract void BindAttributes();
	
	//Bind attributes
	protected void BindAttribute(int attribute, String variableName) {
		GL20.glBindAttribLocation(programID, attribute, variableName);
	}
	
	//Loading uniforms to shader
	
	protected void LoadFloat(int location, float value) {
		GL20.glUniform1f(location, value);
	}
	
	protected void LoadInt(int location, int value) {
		GL20.glUniform1i(location, value);
	}
	
	protected void LoadVector(int location, Vector3f vector) {
		GL20.glUniform3f(location, vector.x, vector.y, vector.z);
	}
	
	protected void LoadBoolean(int location, boolean value) {
		GL20.glUniform1f(location, value ? 1 : 0);
	}
	
	protected void LoadMatrix(int location, Matrix4f matrix) {
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(location, false, matrixBuffer);
	}
	
	private static int LoadShader(String file, int type) {
		StringBuilder shaderSource = new StringBuilder();
		//Loading shader file
		try {
			//Reading lines
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine())!= null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		}catch (IOException e) {
			//Error catching
			System.err.println("Could not read file!");
			e.printStackTrace();
			System.exit(-1);
		}
		//Creating shader
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.err.println("Could not compile shader");
			System.exit(-1);
		}
		return shaderID;
	}
}
