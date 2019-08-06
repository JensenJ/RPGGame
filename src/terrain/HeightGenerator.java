package terrain;

import java.util.Random;

public class HeightGenerator {

	private static final float AMPLITUDE = 10.0f;
	private static final float ROUGHNESS = 0.3f;
	private static final int OCTAVES = 4;
	
	private Random random = new Random();
	private int seed;
	
	public HeightGenerator(int seed) {
		this.seed = seed;
		random.setSeed(seed);
	}
	
	public float GenerateHeight(int x, int z) {
		float total = 0;
		float d = (float) Math.pow(2, OCTAVES-1);
		for(int i = 0; i < OCTAVES; i++) {
			float freq = (float) (Math.pow(2, i) / d);
			float amp = (float) Math.pow(ROUGHNESS, i) * AMPLITUDE;
			total += GetInterpolatedNoise(x * freq, z * freq) * amp;
		}
		
		return total;

	}
	
	private float GetInterpolatedNoise(float x, float z) {
		int intX = (int) x;
		int intZ = (int) z;
		float fracX = x - intX;
		float fracZ = z - intZ;
		
		float v1 = GetSmoothNoise(intX, intZ);
		float v2 = GetSmoothNoise(intX + 1, intZ);
		float v3 = GetSmoothNoise(intX, intZ + 1);
		float v4 = GetSmoothNoise(intX + 1, intZ + 1);
		float i1 = Interpolate(v1, v2, fracX);
		float i2 = Interpolate(v3, v4, fracX);
		return Interpolate(i1, i2, fracZ);
	}
	
	private float Interpolate(float a, float b, float blend) {
		double theta = blend * Math.PI;
		float f = (float) (1f - Math.cos(theta) * 0.5f);
		return a * (1f - f) + b * f;
	}
	
	private float GetSmoothNoise(int x, int z) {
		float corners = (GetNoise(x-1, z-1) + GetNoise(x+1, z-1) + GetNoise(x-1, z+1) + GetNoise(x+1, z+1)) / 16f;
		float sides = (GetNoise(x-1, z) + GetNoise(x+1, z) + GetNoise(x, z-1) + GetNoise(x, z+1)) / 8f;
		float center = GetNoise(x, z) / 4f;
		return corners + sides + center;
	}
	
	private float GetNoise(int x, int z) {
		random.setSeed(x * 49632 + z * 325176 + this.seed);
		return random.nextFloat() * 2f - 1f;
	}
	
}