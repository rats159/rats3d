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

public class Loader {
   private final List<Integer> vaoIDs = new ArrayList<>();
   private final List<Integer> vboIDs = new ArrayList<>();
   public List<Integer> textureIDs = new ArrayList<>();

   public Model loadToVAO(float[] positions, float[] UV, float[] normals, int[] indices){
      int vaoID = createVAO();
      bindIndexBuffer(indices);
      storeData(0,positions,3);
      storeData(1,UV,2);
      storeData(2,normals,3);
      unbindVAO();
      return new Model(vaoID,indices.length);
   }

   public Model loadToVAO(float[] positions, int dimensions) {
      int vaoID = createVAO();
      storeData(0, positions, dimensions);
      unbindVAO();
      return new Model(vaoID, positions.length / dimensions);
   }

   public int loadTexture(String path){
      Texture texture = new Texture("res/%s.png".formatted(path));
      glGenerateMipmap(GL_TEXTURE_2D);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,GL_LINEAR_MIPMAP_LINEAR);
      int id = texture.getID();
      textureIDs.add(id);
      return id;
   }

   private int createVAO(){
      int vaoID = glGenVertexArrays();
      vaoIDs.add(vaoID);
      glBindVertexArray(vaoID);
      return vaoID;
   }

   private void bindIndexBuffer(int[] indices){
      int eboID = glGenBuffers();
      vboIDs.add(eboID); // EBOs are VBOs
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,eboID);
      IntBuffer buffer = arrToBuffer(indices);
      glBufferData(GL_ELEMENT_ARRAY_BUFFER,buffer,GL_STATIC_DRAW);
   }

   public int createEmptyVBO(int floatCount){
      int vboID = glGenBuffers();
      vboIDs.add(vboID);
      glBindBuffer(GL_ARRAY_BUFFER,vboID);
      glBufferData(GL_ARRAY_BUFFER, (long) floatCount * Float.BYTES, GL_STREAM_DRAW);
      glBindBuffer(GL_ARRAY_BUFFER,0);
      return vboID;
   }

   public void updateVBO(int vboID, float[] data, FloatBuffer buffer){
      buffer.clear();
      buffer.put(data);
      buffer.flip();
      glBindBuffer(GL_ARRAY_BUFFER,vboID);
      glBufferData(GL_ARRAY_BUFFER, (long) buffer.capacity() * Float.BYTES, GL_STREAM_DRAW);
      glBufferSubData(GL_ARRAY_BUFFER,0,buffer);
      glBindBuffer(GL_ARRAY_BUFFER,0);
   }

   public void addInstanceAttribute(int vaoID, int vboID, int attribute, int size, int stride, int offset){
      glBindBuffer(GL_ARRAY_BUFFER, vboID);
      glBindVertexArray(vaoID);
      glVertexAttribPointer(attribute,size,GL_FLOAT,false,stride * Float.BYTES, (long) offset * Float.BYTES);
      glVertexAttribDivisor(attribute,1);
      glBindBuffer(GL_ARRAY_BUFFER,0);
      glBindVertexArray(0);
   }

   private void storeData(int num, float[] data, int size){
      int vboID = glGenBuffers();
      vboIDs.add(vboID);
      glBindBuffer(GL_ARRAY_BUFFER,vboID);
      FloatBuffer buffer = arrToBuffer(data);

      glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
      glVertexAttribPointer(num,size,GL_FLOAT,false,0,0);
      glBindBuffer(GL_ARRAY_BUFFER,0);
   }

   private FloatBuffer arrToBuffer(float[] arr){
      FloatBuffer buffer = BufferUtils.createFloatBuffer(arr.length);
      buffer.put(arr);
      buffer.flip();
      return buffer;
   }

   private IntBuffer arrToBuffer(int[] arr){
      IntBuffer buffer = BufferUtils.createIntBuffer(arr.length);
      buffer.put(arr);
      buffer.flip();
      return buffer;
   }

   private void unbindVAO(){
      glBindVertexArray(0);
   }

   public void destroy() {
      for(int id : this.vaoIDs){
         glDeleteVertexArrays(id);
      }

      for(int id : this.vboIDs){
         glDeleteBuffers(id);
      }

      for(int id : this.textureIDs){
         glDeleteTextures(id);
      }
   }

   public Model loadModel(ModelData data) {
      return loadToVAO(data.vertices(),data.textureCoords(),data.normals(),data.indices());
   }
}
