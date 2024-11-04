package dev.rats159.rats3d.entities;

import dev.rats159.rats3d.models.TexturedModel;
import dev.rats159.rats3d.util.math.Vector3f;

public class Entity {
   protected TexturedModel model;
   protected final Vector3f position;
   protected final Vector3f rotation;
   protected float scale;


   public Entity(TexturedModel model, Vector3f position, Vector3f rotation, float scale) {
      this.model = model;
      this.position = position;
      this.rotation = rotation;
      this.scale = scale;
   }

   public void move(float dx, float dy, float dz){
      this.position.addAssign(dx,dy,dz);
   }

   public void move(Vector3f delta){
      this.position.addAssign(delta);
   }

   public void rotate(float dx, float dy, float dz){
      this.rotation.add(dx,dy,dz);
   }

   public TexturedModel getModel() {
      return model;
   }

   public Vector3f getPosition() {
      return position;
   }

   public Vector3f getRotation() {
      return rotation;
   }

   public float getScale() {
      return scale;
   }

   public void setModel(TexturedModel model) {
      this.model = model;
   }

   public void setPosition(Vector3f position) {
      this.position.set(position);
   }

   public void setRotation(Vector3f rotation) {
      this.rotation.set(rotation);
   }

   public void setRotation(float x, float y, float z) {
      this.rotation.set(x,y,z);
   }

   public void setScale(float scale) {
      this.scale = scale;
   }

   public float x(){
      return this.position.x();
   }

   public float y(){
      return this.position.y();
   }

   public float z(){
      return this.position.z();
   }
}
