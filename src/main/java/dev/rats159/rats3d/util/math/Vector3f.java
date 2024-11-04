package dev.rats159.rats3d.util.math;

import org.joml.Math;

import java.util.Objects;

public final class Vector3f {
   private float x;
   private float y;
   private float z;

   public Vector3f(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Vector3f(float n) {
      this(n, n, n);
   }

   public Vector3f(){
      this(0);
   }

   public Vector3f(Vector3f vec) {
      this(vec.x,vec.y,vec.z);
   }

   public float x() {
      return x;
   }

   public float y() {
      return y;
   }

   public float z() {
      return z;
   }

   public Vector2f xy(){
      return new Vector2f(this.x,this.y);
   }

   public Vector2f yz(){
      return new Vector2f(this.y,this.z);
   }

   public Vector2f xz(){
      return new Vector2f(this.x,this.z);
   }

   public Vector2f yx(){
      return new Vector2f(this.y,this.x);
   }

   public Vector2f zy(){
      return new Vector2f(this.z,this.y);
   }

   public Vector2f zx(){
      return new Vector2f(this.z,this.x);
   }

   public Vector3f xyz(){
      return new Vector3f(this);
   }

   public void x(float newVal) {
      this.x = newVal;
   }

   public void y(float newVal) {
      this.y = newVal;
   }

   public void z(float newVal) {
      this.z = newVal;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) return true;
      if (obj == null || obj.getClass() != this.getClass()) return false;
      var that = (Vector3f) obj;
      return Float.floatToIntBits(this.x) == Float.floatToIntBits(that.x) &&
        Float.floatToIntBits(this.y) == Float.floatToIntBits(that.y) &&
        Float.floatToIntBits(this.z) == Float.floatToIntBits(that.z);
   }

   @Override
   public int hashCode() {
      return Objects.hash(x, y, z);
   }

   @Override
   public String toString() {
      return "<" +
        x + ", " +
        y + ", " +
        z + '>';
   }

   public void set(Vector3f vec){
      this.x = vec.x();
      this.y = vec.y();
      this.z = vec.z();
   }

   public void set(float x, float y, float z){
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Vector3f normalize(){
      float mag = this.length();
      return new Vector3f(
        this.x / mag,
        this.y / mag,
        this.z / mag
      );
   }

   public float length() {
      return Math.sqrt(Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z)));
   }

   public float lengthSquared(){
      return Math.fma(this.x, this.x, Math.fma(this.y, this.y, this.z * this.z));
   }

   public void normalizeAssign(){
      this.set(this.normalize());
   }

   public void addAssign(float dx, float dy, float dz) {
      this.set(this.add(dx,dy,dz));
   }

   public void addAssign(Vector3f delta) {
      this.set(this.add(delta));
   }

   public Vector3f add(float x, float y, float z){
      return new Vector3f(this.x + x, this.y + y, this.z + z);
   }

   public Vector3f add(Vector3f delta){
      return new Vector3f(this.x + delta.x, this.y + delta.y, this.z + delta.z);
   }

   public void divAssignX(float n) {
      this.x /= n;
   }

   public void divAssignY(float n) {
      this.y /= n;
   }

   public void divAssignZ(float n) {
      this.z /= n;
   }

   public void addAssignX(float n) {
      this.x += n;
   }

   public void addAssignY(float n) {
      this.y += n;
   }

   public void addAssignZ(float n) {
      this.z += n;
   }

   public void mulAssign(float n) {
      this.mulAssign(n,n,n);
   }

   public void mulAssign(float x, float y, float z){
      this.x *= x;
      this.z *= y;
      this.z *= z;
   }

   public Vector3f sub(Vector3f vec) {
      return this.sub(vec.x,vec.y,vec.z);
   }

   public Vector3f sub(float x, float y, float z){
      return new Vector3f(this.x - x, this.y - y, this.z - z);
   }

   public float distanceSquared(Vector3f v) {
      float dx = this.x - v.x();
      float dy = this.y - v.y();
      float dz = this.z - v.z();
      return Math.fma(dx, dx, Math.fma(dy, dy, dz * dz));
   }

   public org.joml.Vector3f toJOML() {
      return new org.joml.Vector3f(this.x,this.y,this.z);
   }

   public Vector3f negate() {
      return new Vector3f(-this.x,-this.y,-this.z);
   }

   public void negateAssign(){
      this.set(this.negate());
   }
}
