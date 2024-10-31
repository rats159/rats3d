package dev.rats159.rats3d.particle;

import dev.rats159.rats3d.shaders.Shader;
import org.joml.Matrix4f;

public class ParticleShader extends Shader {


   private static final String VERTEX_FILE = "res/shaders/particleVShader.glsl";
   private static final String FRAGMENT_FILE = "res/shaders/particleFShader.glsl";

   public ParticleShader() {
      super(VERTEX_FILE, FRAGMENT_FILE);
   }


   @Override
   protected void bindAttributes() {
      super.bindAttribute(0, "aPosition");
      super.bindAttribute(1, "iModelView");
      super.bindAttribute(5, "iTexOffsets");
      super.bindAttribute(6, "iBlendFactor");
   }

   protected void loadRowCount(int rowCount){
      super.loadFloat("uRowCount", rowCount);
   }

   protected void loadProjectionMatrix(Matrix4f projectionMatrix) {
      super.loadMatrix4f("uProj", projectionMatrix);
   }



}
