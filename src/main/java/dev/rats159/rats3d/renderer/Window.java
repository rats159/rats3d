package dev.rats159.rats3d.renderer;

import dev.rats159.rats3d.input.KeyboardListener;
import dev.rats159.rats3d.input.MouseListener;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class Window {

   public static final int DEFAULT_WIDTH = 1280;
   public static final int DEFAULT_HEIGHT = 720;
   public static String title;

   private static long lastFrameTime;
   private static float delta;

   private static long handle;

   public static void create(String title){
      Window.title = title;

      Window.init();
   }

   private static void init(){
      GLFWErrorCallback.createPrint(System.err).set();

      if(!glfwInit()){
         throw new IllegalStateException("Unable to initialize GLFW");
      }

      glfwDefaultWindowHints();
      glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
      glfwWindowHint(GLFW_RESIZABLE,GLFW_TRUE);
      glfwWindowHint(GLFW_MAXIMIZED,GLFW_TRUE);

      handle = glfwCreateWindow(DEFAULT_WIDTH, DEFAULT_HEIGHT,title,NULL,NULL);

      if(handle == NULL){
         throw new IllegalStateException("Failed to create window");
      }

      glfwSetCursorPosCallback(handle, MouseListener.posCallback);
      glfwSetMouseButtonCallback(handle, MouseListener.buttonCallback);
      glfwSetScrollCallback(handle, MouseListener.scrollCallback);

      glfwSetKeyCallback(handle, KeyboardListener.keyCallback);

      glfwMakeContextCurrent(handle);
      glfwSwapInterval(1);

      glfwShowWindow(handle);

      GL.createCapabilities();
      lastFrameTime = getCurrentTimeMillis();
   }

   public static boolean shouldClose(){
      return glfwWindowShouldClose(handle);
   }

   public static void tick(){
      glfwPollEvents();
      glfwSwapBuffers(handle);
      long currentFrameTime = getCurrentTimeMillis();
      delta = currentFrameTime - lastFrameTime;
      lastFrameTime = currentFrameTime;
   }

   public static void destroy(){
      glfwDestroyWindow(handle);
   }

   public static int getWidth(){
      int[] width = new int[1];
      int[] height = new int[1];
      glfwGetWindowSize(handle,width,height);
      return width[0];
   }

   public static int getHeight(){
      int[] width = new int[1];
      int[] height = new int[1];
      glfwGetWindowSize(handle,width,height);
      return height[0];
   }

   private static long getCurrentTimeMillis(){
      return (long) (glfwGetTime() * 1000);
   }

   public static float getDelta(){
      return delta;
   }
}
