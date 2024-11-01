package dev.rats159.rats3d.terrain;

import dev.rats159.rats3d.assets.Texture;
import dev.rats159.rats3d.models.Model;
import dev.rats159.rats3d.models.ModelData;
import dev.rats159.rats3d.models.OBJModelData;
import dev.rats159.rats3d.renderer.Loader;
import dev.rats159.rats3d.util.MathHelper;
import org.joml.Vector2f;
import org.joml.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Terrain {
   public final static float SIZE = 800;
   private static final float MAX_HEIGHT = 40;
   private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;

   private final float x;
   private final float z;
   private final Model model;
   private final TerrainMultiTexture textures;
   private float[][] heights;

   public Terrain(int gridX, int gridZ, TerrainMultiTexture textures, String heightmap){
      this.x = gridX * SIZE;
      this.z = gridZ * SIZE;
      this.model = generateTerrain(heightmap);
      this.textures = textures;
   }

   private Model generateTerrain(String heightmap){
      BufferedImage image;
      try {
         image = ImageIO.read(new File("res/textures/"+heightmap+".png"));
      }catch (IOException e){
         throw new RuntimeException(e);
      }

      final int VERTEX_COUNT = image.getHeight();
      this.heights = new float[VERTEX_COUNT][VERTEX_COUNT];

      int count = VERTEX_COUNT * VERTEX_COUNT;
      float[] vertices = new float[count * 3];
      float[] normals = new float[count * 3];
      float[] textureCoords = new float[count*2];
      int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
      int vertexPointer = 0;
      for(int i=0;i<VERTEX_COUNT;i++){
         for(int j=0;j<VERTEX_COUNT;j++){
            vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
            float height = readHeightmap(j,i,image);
            heights[j][i] = height;
            vertices[vertexPointer*3+1] = height;
            vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
            Vector3f normal = calculateNormal(j,i,image);
            normals[vertexPointer*3] = normal.x;
            normals[vertexPointer*3+1] = normal.y;
            normals[vertexPointer*3+2] = normal.z;
            textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
            textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
            vertexPointer++;
         }
      }
      int pointer = 0;
      for(int gz=0;gz<VERTEX_COUNT-1;gz++){
         for(int gx=0;gx<VERTEX_COUNT-1;gx++){
            int topLeft = (gz*VERTEX_COUNT)+gx;
            int topRight = topLeft + 1;
            int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
            int bottomRight = bottomLeft + 1;
            indices[pointer++] = topLeft;
            indices[pointer++] = bottomLeft;
            indices[pointer++] = topRight;
            indices[pointer++] = topRight;
            indices[pointer++] = bottomLeft;
            indices[pointer++] = bottomRight;
         }
      }
      ModelData data = new OBJModelData(vertices, textureCoords, normals, indices);
      int vaoData = Loader.modelDataToVAO(data);
      return new Model(vaoData, data);
   }


   public Model getModel() {
      return model;
   }

   public TerrainMultiTexture getTextures() {
      return textures;
   }

   public Texture getBlendMap() {
      return textures.blendMap();
   }

   public float getX() {
      return x;
   }

   public float getZ() {
      return z;
   }

   private Vector3f calculateNormal(int x, int y, BufferedImage image){
      float height1 = readHeightmap(x-1,y,image);
      float height2 = readHeightmap(x+1,y,image);
      float height3 = readHeightmap(x,y-1,image);
      float height4 = readHeightmap(x,y+1,image);

      Vector3f normal = new Vector3f(height1 - height2, 2, height3 - height4);
      normal.normalize();
      return normal;
   }

   private float readHeightmap(int x, int y, BufferedImage image){
      if(x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()){
         return 0;
      }

      float height = image.getRGB(x,y);
      height += MAX_PIXEL_COLOR / 2f;
      height /= MAX_PIXEL_COLOR / 2f;
      height *= MAX_HEIGHT;
      return  height;
   }

   public float getHeight(float worldX, float worldZ){
      float terrainX = worldX - this.x;
      float terrainZ = worldZ - this.z;

      float cellCount = heights.length - 1;

      float gridSquareSize = SIZE / cellCount;

      int gridX = (int) Math.floor(terrainX / gridSquareSize);
      int gridZ = (int) Math.floor(terrainZ / gridSquareSize);

      if(gridX >= cellCount || gridX < 0 || gridZ >= cellCount || gridZ < 0) {
         return 0;
      }

      float xCoord = terrainX % gridSquareSize / gridSquareSize;
      float zCoord = terrainZ % gridSquareSize / gridSquareSize;

      float answer;

      if(xCoord <= (1-zCoord)){
         answer = MathHelper.interpolateAcrossTriangle(
           new Vector3f(0,heights[gridX][gridZ],0),
           new Vector3f(1,heights[gridX+1][gridZ],0),
           new Vector3f(0,heights[gridX][gridZ+1],1),
           new Vector2f(xCoord,zCoord)
         );
      }else {
         answer = MathHelper.interpolateAcrossTriangle(
           new Vector3f(1,heights[gridX+1][gridZ],0),
           new Vector3f(1,heights[gridX+1][gridZ+1],1),
           new Vector3f(0,heights[gridX][gridZ+1],1),
           new Vector2f(xCoord,zCoord)
         );
      }

      return answer;
   }
}
