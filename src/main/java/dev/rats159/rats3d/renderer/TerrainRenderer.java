package dev.rats159.rats3d.renderer;

import dev.rats159.rats3d.models.Model;
import dev.rats159.rats3d.shaders.TerrainShader;
import dev.rats159.rats3d.terrain.Terrain;
import dev.rats159.rats3d.terrain.TerrainMultiTexture;
import dev.rats159.rats3d.util.MathHelper;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class TerrainRenderer {
   private final TerrainShader shader;

   public TerrainRenderer(TerrainShader shader, Matrix4f projection){
      this.shader = shader;
      shader.start();
      shader.loadProjectionMatrix(projection);
      shader.connectTextureUnits();
      shader.stop();
   }

   public void render(List<Terrain> terrains){
      for(Terrain terrain : terrains){
         prepareTerrain(terrain);
         loadModelMatrix(terrain);

         glDrawElements(GL_TRIANGLES, terrain.getModel().vertexCount(), GL_UNSIGNED_INT, 0);

         unbindTerrain();
      }
   }

   private void prepareTerrain(Terrain terrain){
      Model model = terrain.getModel();
      glBindVertexArray(model.vaoID());
      glEnableVertexAttribArray(0);
      glEnableVertexAttribArray(1);
      glEnableVertexAttribArray(2);
      bindTextures(terrain);
      shader.loadShine(1,0);

   }

   private void bindTextures(Terrain terrain){
      TerrainMultiTexture textures = terrain.getTextures();
      glActiveTexture(GL_TEXTURE0);
      glBindTexture(GL_TEXTURE_2D,textures.background().textureID());
      glActiveTexture(GL_TEXTURE1);
      glBindTexture(GL_TEXTURE_2D,textures.r().textureID());
      glActiveTexture(GL_TEXTURE2);
      glBindTexture(GL_TEXTURE_2D,textures.g().textureID());
      glActiveTexture(GL_TEXTURE3);
      glBindTexture(GL_TEXTURE_2D,textures.b().textureID());
      glActiveTexture(GL_TEXTURE4);
      glBindTexture(GL_TEXTURE_2D,terrain.getBlendMap().textureID());
   }

   private void unbindTerrain(){
      glDisableVertexAttribArray(0);
      glDisableVertexAttribArray(1);
      glDisableVertexAttribArray(2);
      glBindVertexArray(0);
      glActiveTexture(GL_TEXTURE0);
      glBindTexture(GL_TEXTURE_2D,0);
      glActiveTexture(GL_TEXTURE1);
      glBindTexture(GL_TEXTURE_2D,0);
      glActiveTexture(GL_TEXTURE2);
      glBindTexture(GL_TEXTURE_2D,0);
      glActiveTexture(GL_TEXTURE3);
      glBindTexture(GL_TEXTURE_2D,0);
      glActiveTexture(GL_TEXTURE4);
      glBindTexture(GL_TEXTURE_2D,0);
   }

   private void loadModelMatrix(Terrain terrain){
      Matrix4f transformation = MathHelper.createTransformationMatrix(
        new Vector3f(terrain.getX(),0,terrain.getZ()), new Vector3f(0), 1);
      shader.loadTransformationMatrix(transformation);
   }
}
