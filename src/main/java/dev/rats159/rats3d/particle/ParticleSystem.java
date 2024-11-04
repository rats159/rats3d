package dev.rats159.rats3d.particle;

import dev.rats159.rats3d.entities.Camera;
import dev.rats159.rats3d.time.Time;
import dev.rats159.rats3d.time.TimeUnit;
import dev.rats159.rats3d.util.math.Vector3f;

public abstract class ParticleSystem {
   private final TextureAtlas texture;
   private final float particlesPerSecond;
   private final float particleSpeed;
   private final float gravityFactor;
   private final float lifespan;

   public ParticleSystem(TextureAtlas texture, float particlesPerSecond, float particleSpeed, float gravityFactor, float lifespan) {
      this.texture = texture;
      this.particlesPerSecond = particlesPerSecond;
      this.particleSpeed = particleSpeed;
      this.gravityFactor = gravityFactor;
      this.lifespan = lifespan;
   }

   public void generateParticles(Vector3f systemCenter, Camera camera){
      float delta = (float) Time.delta(TimeUnit.SECONDS);
      float particlesToCreate = particlesPerSecond * delta;
      int count = (int) Math.floor(particlesToCreate);
      float partialParticle = particlesToCreate % 1;
      for(int i=0;i<count;i++){
         emitParticle(systemCenter, camera);
      }
      if(Math.random() < partialParticle){
         emitParticle(systemCenter, camera);
      }
   }

   protected void emitParticle(Vector3f center, Camera camera){
      Particle p = new Particle(texture,new Vector3f(center), new Vector3f(particleSpeed), gravityFactor, lifespan, 0, 1);
      ParticleMaster.addParticle(p,camera.getPlayer());
   }
}
