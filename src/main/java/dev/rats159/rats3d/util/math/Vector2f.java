package dev.rats159.rats3d.util.math;

import java.util.Objects;

public final class Vector2f {
   private float x;
   private float y;

   public Vector2f(float x, float y) {
      this.x = x;
      this.y = y;
   }

   public Vector2f() {
      this(0);
   }

   public Vector2f(float n) {
      this(n, n);
   }

   public float x() {
      return x;
   }

   public float y() {
      return y;
   }

   public void x(float x) {
      this.x = x;
   }

   public void y(float y) {
      this.y = y;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) return true;
      if (obj == null || obj.getClass() != this.getClass()) return false;
      var that = (Vector2f) obj;
      return Float.floatToIntBits(this.x) == Float.floatToIntBits(that.x) &&
        Float.floatToIntBits(this.y) == Float.floatToIntBits(that.y);
   }

   @Override
   public int hashCode() {
      return Objects.hash(x, y);
   }

   @Override
   public String toString() {
      return "<" +
        x + ", " +
        y + '>';
   }

}
