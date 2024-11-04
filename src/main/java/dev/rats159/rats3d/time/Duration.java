package dev.rats159.rats3d.time;

public final class Duration {
   public static double MILLIS_TO_NANOS = 1e+6;

   private final long nanos;

   private Duration(long nanos){
      this.nanos = nanos;
   }

   public static Duration ofMillis(double millis) {
      return new Duration((long) (millis * MILLIS_TO_NANOS));
   }

   public static Duration ofNanos(long nanos) {
      return new Duration(nanos);
   }

   public long getNanos(){
      return nanos;
   }

   public Duration sub(Duration other){
      return new Duration(this.nanos - other.nanos );
   }
}
