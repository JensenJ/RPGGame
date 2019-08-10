package terrain;

import java.util.Random;

public class HeightGenerator {

	//Generator settings
	public static float AMPLITUDE = 80f;
    public static int OCTAVES = 7;
    public static float ROUGHNESS = 0.3f;
    public static float FREQUENCY = 0.5f;
 
    private Random random = new Random();
    private int seed;
    private int xOffset = 0;
    private int zOffset = 0;
 
    public HeightGenerator() {
        this.seed = 0;
    }
     
    //only works with POSITIVE gridX and gridZ values!
    public HeightGenerator(int gridX, int gridZ, int vertexCount, int seed) {
        this.seed = seed;
        xOffset = gridX * (vertexCount-1);
        zOffset = gridZ * (vertexCount-1);
    }
 
    //Generates height
    public float GenerateHeight(int x, int z) {
    	
    	//Flips negative to positive
    	
    	x = x < 0 ? -x : x;
    	z = z < 0 ? -z : z;
    	
    	//Noise Calculation
        float total = 0;
        float d = (float) Math.pow(2, OCTAVES-1);
        for(int i=0;i<OCTAVES;i++){
            float freq = (float) (Math.pow(2, i) / d);
            float amp = (float) Math.pow(ROUGHNESS, i) * AMPLITUDE;
            total += GetInterpolatedNoise((x+xOffset)*freq * FREQUENCY, (z + zOffset)*freq * FREQUENCY) * amp;
        }
        
        //Return rounded result
        return (float) (int)total;
        
    }
     
    //Interpolate noise
    private float GetInterpolatedNoise(float x, float z){
        int intX = (int) x;
        int intZ = (int) z;
        float fracX = x - intX;
        float fracZ = z - intZ;
         
        //Get smoothnoise
        float v1 = GetSmoothNoise(intX, intZ);
        float v2 = GetSmoothNoise(intX + 1, intZ);
        float v3 = GetSmoothNoise(intX, intZ + 1);
        float v4 = GetSmoothNoise(intX + 1, intZ + 1);
        //Interpolate smoothed noise
        float i1 = Interpolate(v1, v2, fracX);
        float i2 = Interpolate(v3, v4, fracX);
        return Interpolate(i1, i2, fracZ);
    }
    
    //Interpolate function, blends noise
    private float Interpolate(float a, float b, float blend){
        double theta = blend * Math.PI;
        float f = (float)(1f - Math.cos(theta)) * 0.5f;
        return a * (1f - f) + b * f;
    }
 
    //Smooth noise from noise function
    private float GetSmoothNoise(int x, int z) {
        float corners = (GetNoise(x - 1, z - 1) + GetNoise(x + 1, z - 1) + GetNoise(x - 1, z + 1)
                + GetNoise(x + 1, z + 1)) / 16f;
        float sides = (GetNoise(x - 1, z) + GetNoise(x + 1, z) + GetNoise(x, z - 1)
                + GetNoise(x, z + 1)) / 8f;
        float center = GetNoise(x, z) / 4f;
        return corners + sides + center;
    }
 
    //Generate noise
    private float GetNoise(int x, int z) {
        random.setSeed(x * 49632 + z * 325176 + seed);
        return random.nextFloat() * 2f - 1f;
    }
}
