package dev.rats159.rats3d.particle;

import dev.rats159.rats3d.assets.Asset;
import dev.rats159.rats3d.assets.Texture;

public record TextureAtlas(Texture texture, int rowCount) implements Asset {
   public int getID() {
      return texture.getID();
   }
}
