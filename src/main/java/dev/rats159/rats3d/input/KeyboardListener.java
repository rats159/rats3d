package dev.rats159.rats3d.input;

import dev.rats159.rats3d.util.structures.TwoTuple;
import org.lwjgl.glfw.GLFWKeyCallbackI;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public final class KeyboardListener {
   public static final GLFWKeyCallbackI keyCallback = KeyboardListener::keyCallback;

   private static final boolean[] keys = new boolean[360];

   private KeyboardListener() {
   }


   public static void keyCallback(long handle, int key, int scancode, int action, int mods) {
      if (key > keys.length) {
         System.err.printf("key %d is not supported%n", key);
         return;
      }

      if (action == GLFW_PRESS) {
         keys[key] = true;
      } else if (action == GLFW_RELEASE) {
         keys[key] = false;
      }
   }

   public static boolean isKeyPressed(int keyCode) {
      if (keyCode > keys.length || keyCode < 0) {
         System.err.printf("key %d is not supported%n", keyCode);
         return false;
      }

      return keys[keyCode];
   }

   public static void match(List<Integer> keys, List<Runnable> funcs) {
      for (int i = 0; i < keys.size(); i++) {
         if (isKeyPressed(keys.get(i))) {
            funcs.get(i).run();
         }
      }
   }


   @SafeVarargs
   public static void match(TwoTuple<Integer,Runnable>... pairs){
      List<Integer> keys = new ArrayList<>();
      List<Runnable> funcs =  new ArrayList<>();

      for(var pair : pairs){
         keys.add(pair.a());
         funcs.add(pair.b());
      }

      match(keys,funcs);
   }
}
