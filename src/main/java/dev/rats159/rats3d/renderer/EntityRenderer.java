package dev.rats159.rats3d.renderer;

import dev.rats159.rats3d.entities.Entity;
import dev.rats159.rats3d.models.Model;
import dev.rats159.rats3d.models.TexturedModel;
import dev.rats159.rats3d.shaders.StaticShader;
import dev.rats159.rats3d.textures.ModelTexture;
import dev.rats159.rats3d.util.MathHelper;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class EntityRenderer {



   private final StaticShader shader;

   public EntityRenderer(StaticShader shader, Matrix4f projection){
      this.shader = shader;
      shader.start();
      shader.loadProjectionMatrix(projection);
      shader.stop();
   }

   public void render(Map<TexturedModel, List<Entity>> entities){
      for(TexturedModel model : entities.keySet()){
         prepareTexturedModel(model);
         List<Entity> batch = entities.get(model);

         for(Entity ent : batch){
            prepareInstance(ent);
            glDrawElements(GL_TRIANGLES,model.model().vertexCount(),GL_UNSIGNED_INT,0);
         }

         unbindTexturedModel();
      }
   }

   private void prepareTexturedModel(TexturedModel texturedModel){
      Model model = texturedModel.model();
      glBindVertexArray(model.vaoID());
      glEnableVertexAttribArray(0);
      glEnableVertexAttribArray(1);
      glEnableVertexAttribArray(2);
      ModelTexture texture = texturedModel.texture();
      glActiveTexture(GL_TEXTURE0);
      glBindTexture(GL_TEXTURE_2D,texturedModel.texture().getTextureId());

      shader.loadShine(texture.getShineDamper(),texture.getReflectivity());

   }

   private void unbindTexturedModel(){
      glDisableVertexAttribArray(0);
      glDisableVertexAttribArray(1);
      glDisableVertexAttribArray(2);
      glBindVertexArray(0);
   }

   private void prepareInstance(Entity entity){
      Matrix4f transformation = MathHelper.createTransformationMatrix(entity.getPosition(),entity.getRotation(),entity.getScale());
      shader.loadTransformationMatrix(transformation);
   }


}
