package dev.rats159.rats3d.models;

import dev.rats159.rats3d.renderer.Loader;

public record OBJModelData(float[] vertices, float[] textureCoords, float[] normals, int[] indices) implements ModelData{
   @Override
   public int vertexCount() {
      return indices.length;
   }

   @Override
   public void store() {
      Loader.bindIndexBuffer(this.indices());
      Loader.storeData(0,this.vertices(),3);
      Loader.storeData(1,this.textureCoords(),2);
      Loader.storeData(2,this.normals(),3);
   }
}
