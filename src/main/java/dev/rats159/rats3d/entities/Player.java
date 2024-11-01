package dev.rats159.rats3d.entities;

import dev.rats159.rats3d.input.KeyboardListener;
import dev.rats159.rats3d.models.TexturedModel;
import dev.rats159.rats3d.renderer.Window;
import dev.rats159.rats3d.terrain.Terrain;
import dev.rats159.rats3d.util.structures.TwoTuple;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity {
   private static final float RUN_SPEED = .08f;
   public static final float GRAVITY = -.00025f;
   private static final float JUMP_POWER = .05f;

   private final Vector3f motion = new Vector3f(0, 0, 0);

   private Camera camera;

   private float upwardsVelocity = 0;

   private boolean isGrounded = true;

   public Player(TexturedModel model, Vector3f position) {
      super(model, position, new Vector3f(0, 0, 0), 1);
   }

   public void setCamera(Camera camera) {
      this.camera = camera;
   }

   public void move(Terrain terrain) {
      this.motion.x = 0;
      this.motion.z = 0;
      checkInputs();

      float dx = this.motion.x;
      float dz = -this.motion.z;

      if(dx != 0 || dz != 0) {
         float horizontalLength = (float) Math.sqrt(dx * dx + dz * dz);
         this.motion.x /= horizontalLength;
         this.motion.z /= horizontalLength;
         float rot = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;
         super.setRotation(0, rot, 0);
      }

      this.upwardsVelocity += GRAVITY * Window.getDelta();
      this.motion.y = upwardsVelocity * Window.getDelta();
      super.move(this.motion.x, this.motion.y, this.motion.z);

      checkGrounded(terrain);
   }

   private void moveInDir(float offset) {
      float direction = this.camera.getYaw() + offset;
      float distance = RUN_SPEED * Window.getDelta();
      float dx = (float) (Math.sin(Math.toRadians(direction)));
      float dz = (float) (-Math.cos(Math.toRadians(direction)));

      this.motion.x += distance * (dx);
      this.motion.z += distance * (dz);
   }

   private void forward() {
      moveInDir(0);
   }

   private void backward() {
      moveInDir(180);
   }

   private void left() {
      moveInDir(-90);
   }

   private void right() {
      moveInDir(90);
   }

   private void checkGrounded(Terrain terrain) {
      float terrainHeight = terrain.getHeight(this.position.x, this.position.z);

      if (super.getPosition().y < terrainHeight) {
         this.upwardsVelocity = 0;
         this.getPosition().y = terrainHeight;
         isGrounded = true;
      }
   }

   private void jump() {
      if (!isGrounded) return;

      this.upwardsVelocity = JUMP_POWER;
      isGrounded = false;
   }

   private void checkInputs() {
      KeyboardListener.match(
        new TwoTuple<>(GLFW_KEY_W, this::forward),
        new TwoTuple<>(GLFW_KEY_S, this::backward),
        new TwoTuple<>(GLFW_KEY_A, this::left),
        new TwoTuple<>(GLFW_KEY_D, this::right),
        new TwoTuple<>(GLFW_KEY_SPACE, this::jump)
      );
   }
}
