package dev.rats159.rats3d.models;

import dev.rats159.rats3d.assets.Asset;

public final class Model extends Asset {
   private final int vaoID;
   private final ModelData data;

   public Model(int vaoID, ModelData data) {
      this.vaoID = vaoID;
      this.data = data;
   }

   public int vaoID() {
      return vaoID;
   }

   public ModelData data() {
      return data;
   }

   public int vertexCount() {
      return data.vertexCount();
   }
}
