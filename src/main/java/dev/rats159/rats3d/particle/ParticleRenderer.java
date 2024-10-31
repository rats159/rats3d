package dev.rats159.rats3d.particle;

import dev.rats159.rats3d.entities.Camera;
import dev.rats159.rats3d.models.Model;
import dev.rats159.rats3d.renderer.Loader;
import dev.rats159.rats3d.util.MathHelper;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;


public class ParticleRenderer {

   private static final float[] QUAD_VERTICES = {-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f};
   private static final int MAX_INSTANCES = 50_000;
   private static final int INSTANCE_DATA_LENGTH = 21;

   private static final FloatBuffer buffer = BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH);

   private final Model quad;
   private final ParticleShader shader;

   private final int vboID;

   private int pointer = 0;

   protected ParticleRenderer(Matrix4f projectionMatrix){
      this.vboID = Loader.createEmptyVBO(INSTANCE_DATA_LENGTH * MAX_INSTANCES);

      quad = Loader.loadToVAO(QUAD_VERTICES,2);

      Loader.addInstanceAttribute(quad.vaoID(),vboID,1,4,INSTANCE_DATA_LENGTH,0);
      Loader.addInstanceAttribute(quad.vaoID(),vboID,2,4,INSTANCE_DATA_LENGTH,4);
      Loader.addInstanceAttribute(quad.vaoID(),vboID,3,4,INSTANCE_DATA_LENGTH,8);
      Loader.addInstanceAttribute(quad.vaoID(),vboID,4,4,INSTANCE_DATA_LENGTH,12);
      Loader.addInstanceAttribute(quad.vaoID(),vboID,5,4,INSTANCE_DATA_LENGTH,16);
      Loader.addInstanceAttribute(quad.vaoID(),vboID,6,1,INSTANCE_DATA_LENGTH,20);


      shader = new ParticleShader();
      shader.start();
      shader.loadProjectionMatrix(projectionMatrix);
      shader.stop();
   }

   protected void render(Map<ParticleTexture,List<Particle>> particles, Camera camera){
      Matrix4f viewMatrix = MathHelper.createViewMatrix(camera);
      prepare();
      for(ParticleTexture texture : particles.keySet()) {
         bindTexture(texture);
         List<Particle> particleList = particles.get(texture);

         pointer = 0;

         float[] vboData = new float[Math.min(particleList.size(),MAX_INSTANCES) * INSTANCE_DATA_LENGTH];

         for (Particle particle : particleList) {
            if(pointer >= vboData.length) break;
            updateModelViewMatrix(particle.getPosition(), particle.getRotation(), particle.getScale(), viewMatrix,vboData);
            updateTextureInfo(particle,vboData);
         }
         Loader.updateVBO(vboID,vboData,buffer);
         glDrawArraysInstanced(GL_TRIANGLE_STRIP,0,quad.vertexCount(),particleList.size());
      }
      finishRendering();
   }

   private void updateTextureInfo(Particle particle, float[] data){
      data[pointer++] = particle.getTexOffset1().x;
      data[pointer++] = particle.getTexOffset1().y;
      data[pointer++] = particle.getTexOffset2().x;
      data[pointer++] = particle.getTexOffset2().y;
      data[pointer++] = particle.getBlendFactor();
   }

   private void bindTexture(ParticleTexture texture){
      glActiveTexture(GL_TEXTURE0);
      glBindTexture(GL_TEXTURE_2D,texture.getID());
      shader.loadRowCount(texture.rowCount());
   }

   private void updateModelViewMatrix(Vector3f position, float rotation, float scale, Matrix4f viewMatrix, float[] vboData){
      Matrix4f modelMatrix = new Matrix4f();
      modelMatrix.translate(position);

      modelMatrix.m00(viewMatrix.m00());
      modelMatrix.m01(viewMatrix.m10());
      modelMatrix.m02(viewMatrix.m20());
      modelMatrix.m10(viewMatrix.m01());
      modelMatrix.m11(viewMatrix.m11());
      modelMatrix.m12(viewMatrix.m21());
      modelMatrix.m20(viewMatrix.m02());
      modelMatrix.m21(viewMatrix.m12());
      modelMatrix.m22(viewMatrix.m22());

      modelMatrix.rotate((float)Math.toRadians(rotation), MathHelper.POSITIVE_Z);
      modelMatrix.scale(scale);

      Matrix4f modelViewMatrix = new Matrix4f();
      viewMatrix.mul(modelMatrix,modelViewMatrix);
      storeMatrixData(modelViewMatrix,vboData);
   }

   private void storeMatrixData(Matrix4f matrix, float[] vboData){
      vboData[pointer++] = matrix.m00();
      vboData[pointer++] = matrix.m01();
      vboData[pointer++] = matrix.m02();
      vboData[pointer++] = matrix.m03();
      vboData[pointer++] = matrix.m10();
      vboData[pointer++] = matrix.m11();
      vboData[pointer++] = matrix.m12();
      vboData[pointer++] = matrix.m13();
      vboData[pointer++] = matrix.m20();
      vboData[pointer++] = matrix.m21();
      vboData[pointer++] = matrix.m22();
      vboData[pointer++] = matrix.m23();
      vboData[pointer++] = matrix.m30();
      vboData[pointer++] = matrix.m31();
      vboData[pointer++] = matrix.m32();
      vboData[pointer++] = matrix.m33();
   }

   protected void destroy(){
      shader.destroy();
   }

   private void prepare(){
      shader.start();
      glBindVertexArray(quad.vaoID());
      glEnableVertexAttribArray(0);
      glEnableVertexAttribArray(1);
      glEnableVertexAttribArray(2);
      glEnableVertexAttribArray(3);
      glEnableVertexAttribArray(4);
      glEnableVertexAttribArray(5);
      glEnableVertexAttribArray(6);
      glEnable(GL_BLEND);
      glBlendFunc(GL_SRC_ALPHA,GL_ONE);
      glDepthMask(false);
   }

   private void finishRendering(){
      glDepthMask(true);
      glDisable(GL_BLEND);
      glDisableVertexAttribArray(0);
      glDisableVertexAttribArray(1);
      glDisableVertexAttribArray(2);
      glDisableVertexAttribArray(3);
      glDisableVertexAttribArray(4);
      glDisableVertexAttribArray(5);
      glDisableVertexAttribArray(6);
      glBindVertexArray(0);
      shader.stop();
   }
}
