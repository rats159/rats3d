package dev.rats159.rats3d.renderer;


import dev.rats159.rats3d.assets.Texture;
import dev.rats159.rats3d.models.Model;
import dev.rats159.rats3d.models.ModelData;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

public final class Loader {
   private static final List<Integer> vaoIDs = new ArrayList<>();
   private static final List<Integer> vboIDs = new ArrayList<>();
   private static final List<Integer> textureIDs = new ArrayList<>();

   private Loader(){}

   public static Model loadToVAO(float[] positions, float[] UV, float[] normals, int[] indices){
      int vaoID = createVAO();
      bindIndexBuffer(indices);
      storeData(0,positions,3);
      storeData(1,UV,2);
      storeData(2,normals,3);
      unbindVAO();
      return new Model(vaoID,indices.length);
   }

   public static Model loadToVAO(float[] positions, int dimensions) {
      int vaoID = createVAO();
      storeData(0, positions, dimensions);
      unbindVAO();
      return new Model(vaoID, positions.length / dimensions);
   }

   public static int loadTexture(String path){
      Texture texture = new Texture("res/textures/%s.png".formatted(path));
      glGenerateMipmap(GL_TEXTURE_2D);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,GL_LINEAR_MIPMAP_LINEAR);
      int id = texture.getID();
      textureIDs.add(id);
      return id;
   }

   private static int createVAO(){
      int vaoID = glGenVertexArrays();
      vaoIDs.add(vaoID);
      glBindVertexArray(vaoID);
      return vaoID;
   }

   private static void bindIndexBuffer(int[] indices){
      int eboID = glGenBuffers();
      vboIDs.add(eboID); // EBOs are VBOs
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,eboID);
      IntBuffer buffer = arrToBuffer(indices);
      glBufferData(GL_ELEMENT_ARRAY_BUFFER,buffer,GL_STATIC_DRAW);
   }

   public static int createEmptyVBO(int floatCount){
      int vboID = glGenBuffers();
      vboIDs.add(vboID);
      glBindBuffer(GL_ARRAY_BUFFER,vboID);
      glBufferData(GL_ARRAY_BUFFER, (long) floatCount * Float.BYTES, GL_STREAM_DRAW);
      glBindBuffer(GL_ARRAY_BUFFER,0);
      return vboID;
   }

   public static void updateVBO(int vboID, float[] data, FloatBuffer buffer){
      buffer.clear();
      buffer.put(data);
      buffer.flip();
      glBindBuffer(GL_ARRAY_BUFFER,vboID);
      glBufferData(GL_ARRAY_BUFFER, (long) buffer.capacity() * Float.BYTES, GL_STREAM_DRAW);
      glBufferSubData(GL_ARRAY_BUFFER,0,buffer);
      glBindBuffer(GL_ARRAY_BUFFER,0);
   }

   public static void addInstanceAttribute(int vaoID, int vboID, int attribute, int size, int stride, int offset){
      glBindBuffer(GL_ARRAY_BUFFER, vboID);
      glBindVertexArray(vaoID);
      glVertexAttribPointer(attribute,size,GL_FLOAT,false,stride * Float.BYTES, (long) offset * Float.BYTES);
      glVertexAttribDivisor(attribute,1);
      glBindBuffer(GL_ARRAY_BUFFER,0);
      glBindVertexArray(0);
   }

   private static void storeData(int num, float[] data, int size){
      int vboID = glGenBuffers();
      vboIDs.add(vboID);
      glBindBuffer(GL_ARRAY_BUFFER,vboID);
      FloatBuffer buffer = arrToBuffer(data);

      glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
      glVertexAttribPointer(num,size,GL_FLOAT,false,0,0);
      glBindBuffer(GL_ARRAY_BUFFER,0);
   }

   private static FloatBuffer arrToBuffer(float[] arr){
      FloatBuffer buffer = BufferUtils.createFloatBuffer(arr.length);
      buffer.put(arr);
      buffer.flip();
      return buffer;
   }

   private static IntBuffer arrToBuffer(int[] arr){
      IntBuffer buffer = BufferUtils.createIntBuffer(arr.length);
      buffer.put(arr);
      buffer.flip();
      return buffer;
   }

   private static void unbindVAO(){
      glBindVertexArray(0);
   }

   public static void destroy() {
      for(int id : vaoIDs){
         glDeleteVertexArrays(id);
      }

      for(int id : vboIDs){
         glDeleteBuffers(id);
      }

      for(int id : textureIDs){
         glDeleteTextures(id);
      }
   }

   public static Model loadModel(ModelData data) {
      return loadToVAO(data.vertices(),data.textureCoords(),data.normals(),data.indices());
   }
}
