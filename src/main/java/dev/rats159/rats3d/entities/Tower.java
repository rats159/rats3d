package dev.rats159.rats3d.entities;

import dev.rats159.rats3d.models.TexturedModel;
import dev.rats159.rats3d.terrain.Chunk;
import dev.rats159.rats3d.util.math.Vector3f;

public class Tower extends Entity{
   private final float speed;

   private int bounceX = 1;
   private int bounceZ = 1;

   public Tower(TexturedModel model, Vector3f position) {
      super(model, position, new Vector3f(0, (float) (Math.random() * 360),0),1);
      this.speed = (float) (Math.random() * .2f) +.8f;
   }

   public void tick(Chunk chunk){
      if(this.x() <= 2){
         bounceX*=-1;
         this.position.x(2);
      }

      if(this.x() > Chunk.SIZE-2) {
         bounceX *= -1;
         this.position.x(Chunk.SIZE - 2);
      }

      if (this.z() <= 2) {
         bounceZ *= -1;
         this.position.z(2);
      }

      if (this.z() > Chunk.SIZE - 2) {
         bounceZ *= -1;
         this.position.z(Chunk.SIZE - 2);
      }

      float dx = (float) (speed * Math.sin(Math.toRadians(this.getRotation().y())));
      float dz = (float) (speed * Math.cos(Math.toRadians(this.getRotation().y())));

      super.move(dx * bounceX,0,dz * bounceZ);
      this.position.y(chunk.getHeight(this.x(),this.z()));

   }
}
