package dev.rats159.rats3d.time;

public enum TimeUnit {
   SECONDS(1e+9),
   MILLISECONDS(1e+6),
   MICROSECONDS(1e+3),
   ;

   private final double nanosecondFactor;

   TimeUnit(double nanosecondFactor){
      this.nanosecondFactor = nanosecondFactor;
   }


   public double nanosecondFactor(){
      return this.nanosecondFactor;
   }
}
