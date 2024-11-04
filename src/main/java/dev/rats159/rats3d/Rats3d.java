package dev.rats159.rats3d;

import dev.rats159.rats3d.input.MouseListener;
import dev.rats159.rats3d.renderer.Window;
import dev.rats159.rats3d.time.Time;

public final class Rats3d {
   private Rats3d(){}

   public static void step(){
      MouseListener.endFrame(); // Reset mouse deltas
      Window.tick(); // Poll events and swap buffers
      Time.tick(); // Update Delta Time
   }
}
