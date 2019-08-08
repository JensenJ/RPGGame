package obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
 
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
 
public class OBJFileLoader {
    
    private static final String RES_LOC = "res/";
 
    //Loads OBJ File format and returns ModelData
    public static ModelData LoadOBJ(String objFileName) {
    	//Reading file
        FileReader isr = null;
        File objFile = new File(RES_LOC + objFileName + ".obj");
        try {
            isr = new FileReader(objFile);
        } catch (FileNotFoundException e) {
            System.err.println("File not found in res; don't use any extention");
        }
        BufferedReader reader = new BufferedReader(isr);
        String line;
        //Initialising modeldata
        List<Vertex> vertices = new ArrayList<Vertex>();
        List<Vector2f> textures = new ArrayList<Vector2f>();
        List<Vector3f> normals = new ArrayList<Vector3f>();
        List<Integer> indices = new ArrayList<Integer>(); 
        try {
            while (true) {
            	//Read through everyline
                line = reader.readLine();
                if (line.startsWith("v ")) { //Vertex data
                    String[] currentLine = line.split(" ");
                    //Create vertex and add to list
                    Vector3f vertex = new Vector3f((float) Float.valueOf(currentLine[1]),
                            (float) Float.valueOf(currentLine[2]),
                            (float) Float.valueOf(currentLine[3]));
                    Vertex newVertex = new Vertex(vertices.size(), vertex);
                    vertices.add(newVertex);
 
                } else if (line.startsWith("vt ")) { //Texture coord data
                    String[] currentLine = line.split(" ");
                    //Create textureCoord and add to list
                    Vector2f texture = new Vector2f((float) Float.valueOf(currentLine[1]),
                            (float) Float.valueOf(currentLine[2]));
                    textures.add(texture);
                } else if (line.startsWith("vn ")) { //Normal data
                    String[] currentLine = line.split(" ");
                    //Create normal and add to list
                    Vector3f normal = new Vector3f((float) Float.valueOf(currentLine[1]),
                            (float) Float.valueOf(currentLine[2]),
                            (float) Float.valueOf(currentLine[3]));
                    normals.add(normal);
                } else if (line.startsWith("f ")) { //Face data (managed below)
                    break;
                }
            }
            while (line != null && line.startsWith("f ")) {
            	//Split line into three parts where a space is present
                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");
                //Process vertices for all three vertices
                ProcessVertex(vertex1, vertices, indices);
                ProcessVertex(vertex2, vertices, indices);
                ProcessVertex(vertex3, vertices, indices);
                line = reader.readLine();
            }
            //close file
            reader.close();
        } catch (IOException e) { //Catch error
            System.err.println("Error reading the file");
        }
        //Creating array and populating lists full of modelData
        removeUnusedVertices(vertices);
        float[] verticesArray = new float[vertices.size() * 3];
        float[] texturesArray = new float[vertices.size() * 2];
        float[] normalsArray = new float[vertices.size() * 3];
        float furthest = ConvertDataToArrays(vertices, textures, normals, verticesArray,
                texturesArray, normalsArray);
        int[] indicesArray = ConvertIndicesListToArray(indices);
        //Create modelData object and return it
        ModelData data = new ModelData(verticesArray, texturesArray, normalsArray, indicesArray,
                furthest);
        return data;
    }
 
    //Processes vertex
    private static void ProcessVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices) {
    	//Converts data to integer
        int index = Integer.parseInt(vertex[0]) - 1;
        Vertex currentVertex = vertices.get(index);
        int textureIndex = Integer.parseInt(vertex[1]) - 1;
        int normalIndex = Integer.parseInt(vertex[2]) - 1;
        //Checks if data has been set, then adds to indices list
        if (!currentVertex.IsSet()) {
            currentVertex.SetTextureIndex(textureIndex);
            currentVertex.SetNormalIndex(normalIndex);
            indices.add(index);
        } else {
            DealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices,
                    vertices);
        }
    }
 
    //Converts indices list to array
    private static int[] ConvertIndicesListToArray(List<Integer> indices) {
        int[] indicesArray = new int[indices.size()];
        //For each element in indices list, place into array
        for (int i = 0; i < indicesArray.length; i++) {
            indicesArray[i] = indices.get(i);
        }
        return indicesArray;
    }
 
    //Converts all data to arrays
    private static float ConvertDataToArrays(List<Vertex> vertices, List<Vector2f> textures,
            List<Vector3f> normals, float[] verticesArray, float[] texturesArray,
            float[] normalsArray) {
        float furthestPoint = 0;
        //For each vertex
        for (int i = 0; i < vertices.size(); i++) {
            Vertex currentVertex = vertices.get(i);
            if (currentVertex.GetLength() > furthestPoint) {
                furthestPoint = currentVertex.GetLength();
            }
            Vector3f position = currentVertex.GetPosition();
            Vector2f textureCoord = textures.get(currentVertex.GetTextureIndex());
            Vector3f normalVector = normals.get(currentVertex.GetNormalIndex());
            //Sets positions and data inside of arrays
            verticesArray[i * 3] = position.x;
            verticesArray[i * 3 + 1] = position.y;
            verticesArray[i * 3 + 2] = position.z;
            texturesArray[i * 2] = textureCoord.x;
            texturesArray[i * 2 + 1] = 1 - textureCoord.y;
            normalsArray[i * 3] = normalVector.x;
            normalsArray[i * 3 + 1] = normalVector.y;
            normalsArray[i * 3 + 2] = normalVector.z;
        }
        return furthestPoint;
    }
 
    private static void DealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex,
            int newNormalIndex, List<Integer> indices, List<Vertex> vertices) {
    	//Checks if already has same data
        if (previousVertex.HasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            indices.add(previousVertex.GetIndex());
        } else {
            Vertex anotherVertex = previousVertex.GetDuplicateVertex();
            if (anotherVertex != null) {
                DealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex,
                        indices, vertices);
            } else {
            	//Set duplicate vertex data
                Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.GetPosition());
                duplicateVertex.SetTextureIndex(newTextureIndex);
                duplicateVertex.SetNormalIndex(newNormalIndex);
                previousVertex.SetDuplicateVertex(duplicateVertex);
                vertices.add(duplicateVertex);
                indices.add(duplicateVertex.GetIndex());
            }
 
        }
    }
    
    //Remove vertices that are unused in data
    private static void removeUnusedVertices(List<Vertex> vertices){
        for(Vertex vertex:vertices){
            if(!vertex.IsSet()){
                vertex.SetTextureIndex(0);
                vertex.SetNormalIndex(0);
            }
        }
    }
 
}