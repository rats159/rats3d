package dev.rats159.rats3d.particle;

public class ParticleTexture {
   private final int textureID;
   private final int rowCount;

   public ParticleTexture(int textureID, int rowCount){
      this.textureID = textureID;
      this.rowCount = rowCount;
   }

   public int getID() {
      return textureID;
   }

   public int rowCount() {
      return rowCount;
   }
}
