package dev.rats159.rats3d.terrain;

/*
 * Adapted from https://gist.github.com/KdotJPG/b1270127455a94ac5d19
 */
public class OpenSimplex2S {

   private static final long PRIME_X = 0x5205402B9270C86FL;
   private static final long PRIME_Y = 0x598CD327003817B5L;
   private static final long HASH_MULTIPLIER = 0x53A3F72DEEC546F5L;

   private static final float SKEW_2D = 0.366025403784439f;
   private static final float UNSKEW_2D = -0.21132486540518713f;


   private static final int N_GRADS_2D_EXPONENT = 7;
   private static final int N_GRADS_2D = 1 << N_GRADS_2D_EXPONENT;

   private static final float NORMALIZER_2D = 0.05481866495625118f;

   private static final float RSQUARED_2D = 2.0f / 3.0f;

   /*
    * Noise Evaluators
    */

   /**
    * 2D OpenSimplex2S/SuperSimplex noise, standard lattice orientation.
    */
   public static float noise2(long seed, float x, float y) {
      // Get points for A2* lattice
      float s = SKEW_2D * (x + y);
      float xs = x + s;
      float ys = y + s;

      return noise2_UnskewedBase(seed, xs, ys);
   }
   /**
    * 2D  OpenSimplex2S/SuperSimplex noise base.
    */
   private static float noise2_UnskewedBase(long seed, float skewedX, float skewedY) {

      // Get base points and offsets.
      int flooredX = fastFloor(skewedX);
      int flooredY = fastFloor(skewedY);
      float fractionalX = (skewedX - flooredX);
      float fractionalY = (skewedY - flooredY);

      // Prime pre-multiplication for hash.
      long primeFlooredX = flooredX * PRIME_X;
      long primeFlooredY = flooredY * PRIME_Y;

      // Unskew.
      float unskewFactor = (fractionalX + fractionalY) * UNSKEW_2D;
      float dx0 = fractionalX + unskewFactor;
      float dy0 = fractionalY + unskewFactor;

      // First vertex.
      float a0 = RSQUARED_2D - dx0 * dx0 - dy0 * dy0;
      float value = (a0 * a0) * (a0 * a0) * grad(seed, primeFlooredX, primeFlooredY, dx0, dy0);

      // Second vertex.
      float a1 = 2 * (1 + 2 * UNSKEW_2D) * (1 / UNSKEW_2D + 2) * unskewFactor + ((-2 * (1 + 2 * UNSKEW_2D) * (1 + 2 * UNSKEW_2D)) + a0);
      float dx1 = dx0 - (1 + 2 * UNSKEW_2D);
      float dy1 = dy0 - (1 + 2 * UNSKEW_2D);
      value += (a1 * a1) * (a1 * a1) * grad(seed, primeFlooredX + PRIME_X, primeFlooredY + PRIME_Y, dx1, dy1);

      // Third and fourth vertices.
      // Nested conditionals were faster than compact bit logic/arithmetic.
      float xmyi = fractionalX - fractionalY;
      if (unskewFactor < UNSKEW_2D) {
         if (fractionalX + xmyi > 1) {
            float dx2 = dx0 - (3 * UNSKEW_2D + 2);
            float dy2 = dy0 - (3 * UNSKEW_2D + 1);
            float a2 = RSQUARED_2D - dx2 * dx2 - dy2 * dy2;
            if (a2 > 0) {
               value += (a2 * a2) * (a2 * a2) * grad(seed, primeFlooredX + (PRIME_X << 1), primeFlooredY + PRIME_Y, dx2, dy2);
            }
         }
         else
         {
            float dx2 = dx0 - UNSKEW_2D;
            float dy2 = dy0 - (UNSKEW_2D + 1);
            float a2 = RSQUARED_2D - dx2 * dx2 - dy2 * dy2;
            if (a2 > 0) {
               value += (a2 * a2) * (a2 * a2) * grad(seed, primeFlooredX, primeFlooredY + PRIME_Y, dx2, dy2);
            }
         }

         if (fractionalY - xmyi > 1) {
            float dx3 = dx0 - (3 * UNSKEW_2D + 1);
            float dy3 = dy0 - (3 * UNSKEW_2D + 2);
            float a3 = RSQUARED_2D - dx3 * dx3 - dy3 * dy3;
            if (a3 > 0) {
               value += (a3 * a3) * (a3 * a3) * grad(seed, primeFlooredX + PRIME_X, primeFlooredY + (PRIME_Y << 1), dx3, dy3);
            }
         }
         else
         {
            float dx3 = dx0 - (UNSKEW_2D + 1);
            float dy3 = dy0 - UNSKEW_2D;
            float a3 = RSQUARED_2D - dx3 * dx3 - dy3 * dy3;
            if (a3 > 0) {
               value += (a3 * a3) * (a3 * a3) * grad(seed, primeFlooredX + PRIME_X, primeFlooredY, dx3, dy3);
            }
         }
      }
      else
      {
         if (fractionalX + xmyi < 0) {
            float dx2 = dx0 + (1 + UNSKEW_2D);
            float dy2 = dy0 + UNSKEW_2D;
            float a2 = RSQUARED_2D - dx2 * dx2 - dy2 * dy2;
            if (a2 > 0) {
               value += (a2 * a2) * (a2 * a2) * grad(seed, primeFlooredX - PRIME_X, primeFlooredY, dx2, dy2);
            }
         }
         else
         {
            float dx2 = dx0 - (UNSKEW_2D + 1);
            float dy2 = dy0 - UNSKEW_2D;
            float a2 = RSQUARED_2D - dx2 * dx2 - dy2 * dy2;
            if (a2 > 0) {
               value += (a2 * a2) * (a2 * a2) * grad(seed, primeFlooredX + PRIME_X, primeFlooredY, dx2, dy2);
            }
         }

         if (fractionalY < xmyi) {
            float dx2 = dx0 + UNSKEW_2D;
            float dy2 = dy0 + (UNSKEW_2D + 1);
            float a2 = RSQUARED_2D - dx2 * dx2 - dy2 * dy2;
            if (a2 > 0) {
               value += (a2 * a2) * (a2 * a2) * grad(seed, primeFlooredX, primeFlooredY - PRIME_Y, dx2, dy2);
            }
         }
         else
         {
            float dx2 = dx0 - UNSKEW_2D;
            float dy2 = dy0 - (UNSKEW_2D + 1);
            float a2 = RSQUARED_2D - dx2 * dx2 - dy2 * dy2;
            if (a2 > 0) {
               value += (a2 * a2) * (a2 * a2) * grad(seed, primeFlooredX, primeFlooredY + PRIME_Y, dx2, dy2);
            }
         }
      }

      return value;
   }
   /*
    * Utility
    */

   private static float grad(long seed, long xsvp, long ysvp, float dx, float dy) {
      long hash = seed ^ xsvp ^ ysvp;
      hash *= HASH_MULTIPLIER;
      hash ^= hash >> (64 - N_GRADS_2D_EXPONENT + 1);
      int gi = (int)hash & ((N_GRADS_2D - 1) << 1);
      return GRADIENTS_2D[gi] * dx + GRADIENTS_2D[gi | 1] * dy;
   }

   private static int fastFloor(double n) {
      int intN = (int)n;
      return n < intN ? intN - 1 : intN;
   }

   /*
    * Lookup Tables & Gradients
    */

   private static final float[] GRADIENTS_2D;

   static {
      GRADIENTS_2D = new float[N_GRADS_2D * 2];
      float[] grad2 = {
        0.38268343236509f,   0.923879532511287f,
        0.923879532511287f,  0.38268343236509f,
        0.923879532511287f, -0.38268343236509f,
        0.38268343236509f,  -0.923879532511287f,
        -0.38268343236509f,  -0.923879532511287f,
        -0.923879532511287f, -0.38268343236509f,
        -0.923879532511287f,  0.38268343236509f,
        -0.38268343236509f,   0.923879532511287f,
        //-------------------------------------//
        0.130526192220052f,  0.99144486137381f,
        0.608761429008721f,  0.793353340291235f,
        0.793353340291235f,  0.608761429008721f,
        0.99144486137381f,   0.130526192220051f,
        0.99144486137381f,  -0.130526192220051f,
        0.793353340291235f, -0.60876142900872f,
        0.608761429008721f, -0.793353340291235f,
        0.130526192220052f, -0.99144486137381f,
        -0.130526192220052f, -0.99144486137381f,
        -0.608761429008721f, -0.793353340291235f,
        -0.793353340291235f, -0.608761429008721f,
        -0.99144486137381f,  -0.130526192220052f,
        -0.99144486137381f,   0.130526192220051f,
        -0.793353340291235f,  0.608761429008721f,
        -0.608761429008721f,  0.793353340291235f,
        -0.130526192220052f,  0.99144486137381f,
      };

      for (int i = 0; i < grad2.length; i++) {
         grad2[i] = grad2[i] / NORMALIZER_2D;
      }
      for (int i = 0, j = 0; i < GRADIENTS_2D.length; i++, j++) {
         if (j == grad2.length) j = 0;
         GRADIENTS_2D[i] = grad2[j];
      }
   }
}
