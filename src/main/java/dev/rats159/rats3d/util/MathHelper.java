package dev.rats159.rats3d.util;

import dev.rats159.rats3d.entities.Camera;
import org.joml.Matrix4f;
import dev.rats159.rats3d.util.math.Vector2f;
import dev.rats159.rats3d.util.math.Vector3f;

public final class MathHelper {
   private MathHelper(){}

   public static final Vector3f POSITIVE_X = new Vector3f(1,0,0);
   public static final Vector3f POSITIVE_Y = new Vector3f(0,1,0);
   public static final Vector3f POSITIVE_Z = new Vector3f(0,0,1);

   public static final Vector3f NEGATIVE_X = new Vector3f(-1,0,0);
   public static final Vector3f NEGATIVE_Y = new Vector3f(0,-1,0);
   public static final Vector3f NEGATIVE_Z = new Vector3f(0,0,-1);

   // Barycentric Interpolation
   public static float interpolateAcrossTriangle(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos){
      float det = (p2.z() - p3.z()) * (p1.x() - p3.x()) + (p3.x() - p2.x()) * (p1.z() - p3.z());
      float l1 = ((p2.z() - p3.z()) * (pos.x() - p3.x()) + (p3.x() - p2.x()) * (pos.y() - p3.z())) / det;
      float l2 = ((p3.z() - p1.z()) * (pos.x() - p3.x()) + (p1.x() - p3.x()) * (pos.y() - p3.z())) / det;
      float l3 = 1 - l1 - l2;

      return l1 * p1.y() + l2 * p2.y() + l3 * p3.y();
   }

   public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, float scale){
      Matrix4f mat = new Matrix4f();
      mat.identity();
      mat.translate(translation.toJOML());
      mat.rotate((float)Math.toRadians(rotation.x()),POSITIVE_X.toJOML());
      mat.rotate((float)Math.toRadians(rotation.y()),POSITIVE_Y.toJOML());
      mat.rotate((float)Math.toRadians(rotation.z()),POSITIVE_Z.toJOML());
      mat.scale(scale);
      return mat;
   }

   public static Matrix4f createViewMatrix(Camera camera){
      Matrix4f mat = new Matrix4f();
      mat.identity();
      mat.rotate((float)Math.toRadians(camera.getPitch()), POSITIVE_X.toJOML());
      mat.rotate((float)Math.toRadians(camera.getYaw()), POSITIVE_Y.toJOML());
      Vector3f pos = camera.getPosition();
      mat.translate(new Vector3f(pos).negate().toJOML());
      return mat;
   }
}
