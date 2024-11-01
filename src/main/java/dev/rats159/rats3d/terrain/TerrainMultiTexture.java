package dev.rats159.rats3d.terrain;

import dev.rats159.rats3d.assets.Texture;
import dev.rats159.rats3d.renderer.Loader;

public final class TerrainMultiTexture {
   private final Texture background;
   private final Texture r;
   private final Texture g;
   private final Texture b;
   private final Texture blendMap;

   public TerrainMultiTexture(String background, String r, String g, String b, String blendMap) {
      this.background = Loader.getTexture(background);
      this.r = Loader.getTexture(r);
      this.g = Loader.getTexture(g);
      this.b = Loader.getTexture(b);
      this.blendMap = Loader.getTexture(blendMap);
   }

   public Texture background() {
      return background;
   }

   public Texture r() {
      return r;
   }

   public Texture g() {
      return g;
   }

   public Texture b() {
      return b;
   }

   public Texture blendMap() {
      return blendMap;
   }
}
