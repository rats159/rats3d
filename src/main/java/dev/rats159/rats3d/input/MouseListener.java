package dev.rats159.rats3d.input;

import dev.rats159.rats3d.renderer.Window;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;

import static org.lwjgl.glfw.GLFW.*;

public final class MouseListener {
   public static final GLFWCursorPosCallbackI posCallback = MouseListener::mousePosCallback;
   public static final GLFWMouseButtonCallbackI buttonCallback = MouseListener::mouseButtonCallback;
   public static final GLFWScrollCallbackI scrollCallback = MouseListener::mouseScrollCallback;

   private static double scrollX = 0, scrollY = 0;
   private static float xPos = 0, yPos = 0, lastXPos = 0, lastYPos = 0;

   private static final boolean[] buttons = new boolean[3];

   private static boolean isDragging;

   public static void mousePosCallback(long handle, double x, double y){
      lastXPos = xPos;
      lastYPos = yPos;
      xPos = (float) x;
      yPos = (float)(Window.getHeight() - y);

      isDragging = buttons[0] || buttons[1] || buttons[2];
   }

   public static void mouseButtonCallback(long handle, int button, int action, int mods){
      if(button > 2){
         System.err.printf("mouse button %d is not supported",button);
         return;
      }
      if(action == GLFW_PRESS) {
         buttons[button] = true;
         if(button == GLFW_MOUSE_BUTTON_2) glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
      }else if(action == GLFW_RELEASE){
         if(button == GLFW_MOUSE_BUTTON_2) glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
         buttons[button] = false;
         isDragging =false;
      }
   }

   public static void mouseScrollCallback(long handle, double x, double y){
      scrollX = x;
      scrollY = y;
   }

   public static void endFrame(){
      scrollX = 0;
      scrollY = 0;
      lastXPos = xPos;
      lastYPos = yPos;
   }

   public static double getX(){
      return xPos;
   }

   public static float getY(){
      return yPos;
   }

   public static double getDx() {
      return lastXPos - xPos;
   }

   public static double getDy() {
      return lastYPos - yPos;
   }

   public static double getScrollX() {
      return scrollX;
   }

   public static double getScrollY() {
      return scrollY;
   }

   public static boolean isDragging(){
      return isDragging;
   }

   public static boolean isButtonDown(int button){
      if(button > 2 || button < 0){
         System.err.printf("mouse button %d is not supported%n", button);
         return false;
      }

      return buttons[button];
   }

   public static Vector2f getPos(){
      return new Vector2f((float) getX(), getY());
   }
}
