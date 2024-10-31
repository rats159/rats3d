package dev.rats159.rats3d.textures;

public class ModelTexture {
   private final int textureId;

   private float shineDamper = 1;
   private float reflectivity;

   public ModelTexture(int id){
      this.textureId = id;
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
