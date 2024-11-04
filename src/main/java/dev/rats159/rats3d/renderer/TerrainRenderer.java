package dev.rats159.rats3d.renderer;

import dev.rats159.rats3d.assets.Texture;
import dev.rats159.rats3d.models.Model;
import dev.rats159.rats3d.shaders.TerrainShader;
import dev.rats159.rats3d.terrain.Chunk;
import dev.rats159.rats3d.util.MathHelper;
import dev.rats159.rats3d.util.math.Vector3f;
import org.joml.Matrix4f;

import java.util.List;

import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
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

   public void render(List<Chunk> chunks){
      for(Chunk chunk : chunks){
         prepareTerrain(chunk);
         loadModelMatrix(chunk);

         glDrawElements(GL_TRIANGLES, chunk.getModel().vertexCount(), GL_UNSIGNED_INT, 0);

         unbindTerrain();
      }
   }

   private void prepareTerrain(Chunk chunk){
      Model model = chunk.getModel();
      glBindVertexArray(model.vaoID());
      shader.enableAttributes();
      bindTextures(chunk);
      shader.loadShine(1,0f);

   }

   private void bindTextures(Chunk chunk){
      Texture texture = chunk.getTexture();
      glActiveTexture(GL_TEXTURE0);
      glBindTexture(GL_TEXTURE_2D,texture.getID());
   }

   private void unbindTerrain(){
      glDisableVertexAttribArray(0);
      glDisableVertexAttribArray(1);
      glDisableVertexAttribArray(2);
      glBindVertexArray(0);
      glActiveTexture(GL_TEXTURE0);
      glBindTexture(GL_TEXTURE_2D,0);
   }

   private void loadModelMatrix(Chunk chunk){
      Matrix4f transformation = MathHelper.createTransformationMatrix(
        new Vector3f(chunk.getX(),0, chunk.getZ()), new Vector3f(0), 1);
      shader.loadTransformationMatrix(transformation);
   }
}
