package dev.rats159.rats3d.shaders;

import dev.rats159.rats3d.entities.Camera;
import dev.rats159.rats3d.entities.Light;
import dev.rats159.rats3d.util.MathHelper;
import org.joml.Matrix4f;
import dev.rats159.rats3d.util.math.Vector3f;

import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

public class StaticShader extends Shader{
   public static final int MAX_LIGHTS_PER_VERT = 29;

   private static final String VERTEX_PATH = "res/shaders/vertex.glsl";
   private static final String FRAGMENT_PATH = "res/shaders/fragment.glsl";

   public StaticShader() {
      super(VERTEX_PATH, FRAGMENT_PATH);
   }

   @Override
   protected void bindAttributes() {
      super.bindAttribute(0,"aPos");
      super.bindAttribute(1,"aUV");
      super.bindAttribute(2,"aNormal");
   }

   @Override
   public void enableAttributes() {
      glEnableVertexAttribArray(0);
      glEnableVertexAttribArray(1);
      glEnableVertexAttribArray(2);
   }

   public void loadSkyColor(float r, float g, float b){
      super.loadVector3f("uSkyColor", new Vector3f(r,g,b));
   }

   public void loadTransformationMatrix(Matrix4f matrix){
      super.loadMatrix4f("uTransformation",matrix);
   }

   public void loadLights(Light[] lights){
      for(int i = 0; i < MAX_LIGHTS_PER_VERT; i++){
         if(i < lights.length){
            super.loadVector3f("uLightPos["+i+"]",lights[i].getPosition());
            super.loadVector3f("uLightColor["+i+"]",lights[i].getColor());
            super.loadVector3f("uAttenuation["+i+"]",lights[i].getAttenuation());
         }else{
            super.loadVector3f("uLightPos["+i+"]", new Vector3f(0));
            super.loadVector3f("uLightColor["+i+"]", new Vector3f(0));
            super.loadVector3f("uAttenuation["+i+"]", new Vector3f(1,0,0));
         }
      }
   }

   public void loadProjectionMatrix(Matrix4f matrix){
      super.loadMatrix4f("uProjection",matrix);
   }

   public void loadViewMatrix(Camera camera){
      super.loadMatrix4f("uView", MathHelper.createViewMatrix(camera));
   }

   public void loadShine(float damper, float reflectivity){
      super.loadFloat("uShineDamper", damper);
      super.loadFloat("uReflectivity", reflectivity);
   }
}
