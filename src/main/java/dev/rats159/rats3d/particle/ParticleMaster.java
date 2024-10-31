package dev.rats159.rats3d.particle;

import dev.rats159.rats3d.entities.Camera;
import dev.rats159.rats3d.entities.Entity;
import dev.rats159.rats3d.renderer.Loader;
import org.joml.Matrix4f;

import java.util.*;

public class ParticleMaster {
   private static final Map<ParticleTexture,List<Particle>> particles = new HashMap<>();
   private static ParticleRenderer renderer;

   public static void init(Loader loader, Matrix4f projectionMatrix){
      renderer = new ParticleRenderer(loader,projectionMatrix);
   }

   public static void update(Camera camera){
      Iterator<Map.Entry<ParticleTexture, List<Particle>>> mapIterator = particles.entrySet().iterator();

      while(mapIterator.hasNext()){
         List<Particle> list = mapIterator.next().getValue();

         Iterator<Particle> iterator = list.iterator();
         while(iterator.hasNext()){
            Particle p = iterator.next();
            boolean stillAlive = p.tick(camera);
            if(!stillAlive){
               iterator.remove();
               if(list.isEmpty()){
                  mapIterator.remove();
               }
            }
         }

          list.sort(Comparator.comparing(Particle::getCloseness));
      }


   }

   public static void renderParticles(Camera camera){
      renderer.render(particles,camera);
   }

   public static void destroy(){
      renderer.destroy();
   }

   public static void addParticle(Particle particle, Entity viewer){
      if(particle.getPosition().distanceSquared(viewer.getPosition()) > 10000){
         return;
      }
      List<Particle> particleList = particles.computeIfAbsent(particle.getTexture(), _ -> new ArrayList<>());
      particleList.add(particle);
   }
}
