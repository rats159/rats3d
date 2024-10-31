package dev.rats159.rats3d.entities;

import dev.rats159.rats3d.models.TexturedModel;
import dev.rats159.rats3d.terrain.Terrain;
import org.joml.Vector3f;

public class Tower extends Entity{
   private final float speed;

   private final Light light;

   private int bounceX = 1;
   private int bounceZ = 1;

   public Tower(TexturedModel model, Vector3f position) {
      super(model, position, new Vector3f(0, (float) (Math.random() * 360),0),1);
      this.speed = (float) (Math.random() * .2f) +.8f;
      this.light = new Light(this.position,new Vector3f(1,0.8f,0.2f),new Vector3f(0,0.1f,0));
   }

   public void tick(Terrain terrain){
      if(this.x() <= 2){
         bounceX*=-1;
         this.position.x = 2;
      }

      if(this.x() > Terrain.SIZE-2) {
         bounceX *= -1;
         this.position.x = Terrain.SIZE - 2;
      }

      if (this.z() <= 2) {
         bounceZ *= -1;
         this.position.z = 2;
      }

      if (this.z() > Terrain.SIZE - 2) {
         bounceZ *= -1;
         this.position.z = Terrain.SIZE - 2;
      }

      float dx = (float) (speed * Math.sin(Math.toRadians(this.getRotation().y)));
      float dz = (float) (speed * Math.cos(Math.toRadians(this.getRotation().y)));

      this.move(dx * bounceX,0,dz * bounceZ);
      this.position.y = terrain.getHeight(this.x(),this.z());

      this.light.setPosition(this.position);
      this.light.getPosition().y += 2;
   }

   public Light getLight() {
      return light;
   }
}
