package dev.rats159.rats3d.models;

import dev.rats159.rats3d.renderer.Loader;

public record QuadModelData(float[] positions) implements ModelData{
   @Override
   public int vertexCount() {
      return 4;
   }

   @Override
   public void store() {
      Loader.storeData(0,positions,2);
   }
}
