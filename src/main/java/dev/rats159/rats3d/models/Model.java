package dev.rats159.rats3d.models;

import dev.rats159.rats3d.assets.Asset;

public record Model(int vaoID, ModelData data) implements Asset {
   public int vertexCount() {
      return data.vertexCount();
   }
}
