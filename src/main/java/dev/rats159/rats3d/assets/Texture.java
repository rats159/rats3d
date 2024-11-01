package dev.rats159.rats3d.assets;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Texture extends Asset{

   private int width, height;
   private final int id;

   public Texture(String path) {
      try{
         this.id = load(path);
      }catch (IOException e){
         throw new RuntimeException(e);
      }
   }

   private int load(String path) throws IOException {
      BufferedImage image = ImageIO.read(new FileInputStream(path));

      this.width = image.getWidth();
      this.height = image.getHeight();

      int[] pixels = image.getRGB(0, 0, this.width, this.height, null, 0, this.width);

      for (int i = 0; i < this.width * this.height; i++) {
         pixels[i] = convertPixel(pixels[i]);
      }

      int id = glGenTextures();

      glBindTexture(GL_TEXTURE_2D, id);

      setDefaultTexParameters();

      IntBuffer buffer = BufferUtils.createIntBuffer(pixels.length).put(pixels).flip();

      glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
      return id;
   }

   private void setDefaultTexParameters(){
      glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_REPEAT);
      glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_REPEAT);

      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
   }

   private int convertPixel(int pixel){
      int a = (pixel & 0xff000000) >> 24;
      int r = (pixel & 0x00ff0000) >> 16;
      int g = (pixel & 0x0000ff00) >> 8;
      int b = (pixel & 0x000000ff);

      return a << 24 | b << 16 | g << 8 | r;
   }

   public void bind() {
      glBindTexture(GL_TEXTURE_2D, id);
   }

   public void unbind() {
      glBindTexture(GL_TEXTURE_2D, 0);
   }

   public int getID() {
      return id;
   }

}