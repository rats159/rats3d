package dev.rats159.rats3d.entities;

import org.joml.Vector3f;

public class Light {
   private final Vector3f position;
   private final Vector3f color;
   private final Vector3f attenuation;

   public Light(Vector3f position, Vector3f color){
      this(position,color,new Vector3f(1,0,0));
   }

   public Light(Vector3f position, Vector3f color, Vector3f attenuation){
      this.position = position;
      this.color = color;
      this.attenuation = attenuation;
   }

   public Vector3f getPosition() {
      return position;
   }

   public Vector3f getColor() {
      return color;
   }

   public Vector3f getAttenuation() {
      return attenuation;
   }

   public void setPosition(Vector3f position) {
      this.position.set(position);
   }

   public void setColor(Vector3f color){
      this.color.set(color);
   }

   public void setAttenuation(Vector3f attenuation){
      this.attenuation.set(attenuation);
   }
}
