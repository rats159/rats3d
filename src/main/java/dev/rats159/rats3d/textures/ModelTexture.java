package dev.rats159.rats3d.textures;

import dev.rats159.rats3d.assets.Texture;

public class ModelTexture {
   private final int textureId;

   private float shineDamper = 1;
   private float reflectivity;

   public ModelTexture(Texture texture){
      this.textureId = texture.getID();
   }

   public int getTextureId() {
      return textureId;
   }

   public float getReflectivity() {
      return reflectivity;
   }

   public float getShineDamper() {
      return shineDamper;
   }

   public void setReflectivity(float reflectivity) {
      this.reflectivity = reflectivity;
   }

   public void setShineDamper(float shineDamper) {
      this.shineDamper = shineDamper;
   }
}
