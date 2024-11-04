package dev.rats159.rats3d.entities;

import dev.rats159.rats3d.input.KeyboardListener;
import dev.rats159.rats3d.models.TexturedModel;
import dev.rats159.rats3d.terrain.Chunk;
import dev.rats159.rats3d.time.Time;
import dev.rats159.rats3d.time.TimeUnit;
import dev.rats159.rats3d.util.math.Vector3f;
import dev.rats159.rats3d.util.structures.TwoTuple;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity {
   private static final float RUN_SPEED = .08f;
   public static final float GRAVITY = -.91f;
   private static final float JUMP_POWER = .5f;

   private final Vector3f velocity = new Vector3f(0, 0, 0);

   private Camera camera;

   private boolean isGrounded = true;

   public Player(TexturedModel model, Vector3f position) {
      super(model, position, new Vector3f(0, 0, 0), 1);
   }

   public void setCamera(Camera camera) {
      this.camera = camera;
   }

   public void move(Chunk chunk) {
      this.velocity.x(0);
      this.velocity.z(0);
      checkInputs();

      float dx = this.velocity.x();
      float dz = -this.velocity.z();

      if(dx != 0 || dz != 0) {
         float horizontalLength = (float) Math.sqrt(dx * dx + dz * dz);
         this.velocity.divAssignX(horizontalLength);
         this.velocity.divAssignZ(horizontalLength);
         float rot = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;
         super.setRotation(0, rot, 0);
      }

      this.velocity.addAssignY((float) (GRAVITY * (Time.delta(TimeUnit.MILLISECONDS))));
      System.out.println(this.velocity.y());
      super.move(this.velocity);

      checkGrounded(chunk);
   }

   private void moveInDir(float offset) {
      float direction = this.camera.getYaw() + offset;
      float distance = (float) (RUN_SPEED * Time.delta(TimeUnit.MILLISECONDS));
      float dx = (float) (Math.sin(Math.toRadians(direction)));
      float dz = (float) (-Math.cos(Math.toRadians(direction)));

      this.velocity.addAssignX(distance * dx);
      this.velocity.addAssignZ(distance * dz);
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

   private void checkGrounded(Chunk chunk) {
      float terrainHeight = chunk.getHeight(this.position.xz());

      if (position.y() < terrainHeight) {
         this.velocity.y(0);
         this.position.y(terrainHeight);
         isGrounded = true;
      }
   }

   private void jump() {
      if (!isGrounded) return;

      this.velocity.y(JUMP_POWER);
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
