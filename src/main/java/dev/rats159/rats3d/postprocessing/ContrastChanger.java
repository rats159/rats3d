package dev.rats159.rats3d.postprocessing;

import dev.rats159.rats3d.assets.Texture;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class ContrastChanger {
   private ImageRenderer renderer;
   private ContrastShader shader;

   public ContrastChanger(){
      this.shader = new ContrastShader();
      this.renderer = new ImageRenderer();
   }

   public void render(Texture texture){
      shader.start();
      glActiveTexture(GL_TEXTURE0);
      glBindTexture(GL_TEXTURE_2D,texture.getID());
      renderer.renderQuad();
      shader.stop();
   }

   public void destroy(){
      this.renderer.destroy();
      this.shader.destroy();
   }
}
