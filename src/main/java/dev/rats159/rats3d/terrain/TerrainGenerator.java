package dev.rats159.rats3d.terrain;

import java.util.Random;

public class TerrainGenerator {
   private static final float AMPLITUDE = 70;
   private Random random = new Random();
   private int seed;

   public TerrainGenerator(){
      this.seed = random.nextInt();
   }

   public float getHeight(int x, int z){
      float height = 0;
      for(float i = 1f; i < 64f; i*=2){
         height += getNoise(x*i / 64,z*i / 64) * AMPLITUDE/i;
      }

      return height;
   }

   private float getNoise(float x, float z) {
      return OpenSimplex2S.noise2(seed,x,z);
   }
}
