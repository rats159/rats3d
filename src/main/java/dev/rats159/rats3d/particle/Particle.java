package dev.rats159.rats3d.particle;

import dev.rats159.rats3d.entities.Camera;
import dev.rats159.rats3d.entities.Player;
import dev.rats159.rats3d.time.Time;
import dev.rats159.rats3d.time.TimeUnit;
import dev.rats159.rats3d.util.math.Vector2f;
import dev.rats159.rats3d.util.math.Vector3f;

public class Particle {
   private final Vector3f position;
   private final Vector3f velocity;
   private final float massFactor;
   private final float lifespan;
   private final float rotation;
   private final float scale;

   private final TextureAtlas texture;

   private final Vector2f texOffset1 = new Vector2f(0);
   private final Vector2f texOffset2 = new Vector2f(0);
   private float blendFactor;

   private float life = 0;
   private float distance;

   public Particle(TextureAtlas texture, Vector3f position, Vector3f velocity, float massFactor, float lifespan, float rotation, float scale) {
      this.position = position;
      this.velocity = velocity;
      this.massFactor = massFactor;
      this.lifespan = lifespan;
      this.rotation = rotation;
      this.scale = scale;

      this.texture = texture;
   }

   public Vector3f getPosition() {
      return position;
   }

   public float getRotation() {
      return rotation;
   }

   public float getScale() {
      return scale;
   }

   public TextureAtlas getTexture() {
      return texture;
   }

   protected boolean tick(Camera camera){
      velocity.addAssignY((float) (Player.GRAVITY * massFactor * Time.delta(TimeUnit.SECONDS)));
      Vector3f deltaPos = new Vector3f(velocity);
      deltaPos.mulAssign((float) Time.delta(TimeUnit.MILLISECONDS));
      position.addAssign(deltaPos);
      distance = camera.getPosition().sub(position).lengthSquared();
      updateTexCoords();
      life += (float) Time.delta(TimeUnit.SECONDS);
      return life < lifespan;
   }

   private void updateTexCoords(){
      float lifeFactor = life / lifespan;
      int stages = texture.rowCount() * texture.rowCount();
      float atlasProgression = lifeFactor * stages;

      int index1 = (int) Math.floor(atlasProgression);
      int index2 = index1 < stages - 1? index1 +1 : index1;
      this.blendFactor = atlasProgression % 1;
      setTextureOffset(texOffset1,index1);
      setTextureOffset(texOffset2,index2);
   }

   private  void setTextureOffset(Vector2f offset, int index){
      int column = index % texture.rowCount();
      int row = index / texture.rowCount();
      offset.x((float) column / texture.rowCount());
      offset.y((float) row / texture.rowCount());
   }

   public float getBlendFactor() {
      return blendFactor;
   }

   public Vector2f getTexOffset1() {
      return texOffset1;
   }

   public Vector2f getTexOffset2() {
      return texOffset2;
   }

   // For sorting
   public float getCloseness() {
      return -distance;
   }
}
