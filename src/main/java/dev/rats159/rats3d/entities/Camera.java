package dev.rats159.rats3d.entities;

import dev.rats159.rats3d.input.MouseListener;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {
   private final Vector3f position = new Vector3f(0,0,0);
   private float pitch;
   private float yaw;

   private float zoom = 50;
   private float angleAround = 0;

   private final Player player;

   public Camera(Player player){
      this.player = player;
      player.setCamera(this);
   }

   public void tick(){
      calculateZoom();
      calculatePitch();
      calculateAngleAround();

      float horizontalDistance = calculateHorizontalDistance();
      float verticalDistance = calculateVerticalDistance();

      calculatePosition(horizontalDistance,verticalDistance);
      this.yaw = 180 - angleAround;
   }

   public float getPitch() {
      return pitch;
   }

   public float getYaw() {
      return yaw;
   }

   public Vector3f getPosition() {
      return position;
   }

   public Player getPlayer() {
      return player;
   }

   private void calculateZoom(){
      float zoomLevel = (float) (MouseListener.getScrollY() * 2);
      this.zoom -= zoomLevel;
   }

   private void calculatePitch(){
      if(MouseListener.isButtonDown(GLFW_MOUSE_BUTTON_RIGHT)){
         float pitchChange = (float) (MouseListener.getDy() * 0.5f);
         pitch += pitchChange;
         pitch = Math.clamp(pitch,-90,90);
      }
   }

   private void calculateAngleAround(){
      if(MouseListener.isButtonDown(GLFW_MOUSE_BUTTON_RIGHT)){
         float angleChange = (float) (MouseListener.getDx() / 4);
         this.angleAround += angleChange;
      }
   }

   private float calculateHorizontalDistance() {
      return (float) (this.zoom * Math.cos(Math.toRadians(this.pitch)));
   }

   private float calculateVerticalDistance(){
      return (float) (this.zoom * Math.sin(Math.toRadians(this.pitch)));
   }

   private void calculatePosition(float horizontalDistance, float verticalDistance){
      this.position.y = player.getPosition().y + verticalDistance;
      this.position.x = this.player.getPosition().x - (float) (horizontalDistance * Math.sin(Math.toRadians(angleAround)));
      this.position.z = this.player.getPosition().z - (float) (horizontalDistance * Math.cos(Math.toRadians(angleAround)));
   }
}
