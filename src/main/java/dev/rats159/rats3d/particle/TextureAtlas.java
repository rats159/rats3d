package dev.rats159.rats3d.particle;

import dev.rats159.rats3d.assets.Texture;

public record TextureAtlas(Texture texture, int rowCount) {
   public int getID() {
      return texture.getID();
   }
}
