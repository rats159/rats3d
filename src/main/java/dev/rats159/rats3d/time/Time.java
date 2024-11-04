package dev.rats159.rats3d.time;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public final class Time {

   private static Duration lastFrameTime;
   private static Duration delta;

   private Time(){}

   static{
      lastFrameTime = runtime();
   }

   public static void tick(){
      Duration currentFrameTime = runtime();
      delta = currentFrameTime.sub(lastFrameTime);
      lastFrameTime = currentFrameTime;
   }

   public static Duration delta(){
      return delta;
   }

   public static double delta(TimeUnit unit){
      return delta.getNanos() / unit.nanosecondFactor();
   }

   public static Duration runtime(){
      return Duration.ofMillis(glfwGetTime());
   }
}
