package dev.rats159.rats3d.particle;

import dev.rats159.rats3d.entities.Camera;
import dev.rats159.rats3d.renderer.Window;
import org.joml.Vector3f;

public class ParticleSystem {
   private final TextureAtlas texture;
   private final float pps = 100;
   private final float speed = .02f;
   private final float gravityCompliant = 0;
   private final float lifeLength = 1000;

   public ParticleSystem(TextureAtlas texture) {
      this.texture = texture;
   }

   public void generateParticles(Vector3f systemCenter, Camera camera){
      float delta = Window.getDelta() / 1000;
      float particlesToCreate = pps * delta;
      int count = (int) Math.floor(particlesToCreate);
      float partialParticle = particlesToCreate % 1;
      for(int i=0;i<count;i++){
         emitParticle(systemCenter, camera);
      }
      if(Math.random() < partialParticle){
         emitParticle(systemCenter, camera);
      }
   }

   private void emitParticle(Vector3f center, Camera camera){
      Vector3f offset = new Vector3f((float) (Math.random() * 2-1), 0, (float) (Math.random()* 2-1));
      Particle p = new Particle(texture,new Vector3f(center).add(offset), new Vector3f(0, (float) (Math.random() /2 + .5f), 0).mul(speed), gravityCompliant, (float) (lifeLength * Math.random() + .5f) * 2, 0, 1);
      ParticleMaster.addParticle(p,camera.getPlayer());
   }


}
