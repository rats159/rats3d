package dev.rats159.rats3d.particle;

import dev.rats159.rats3d.entities.Camera;
import dev.rats159.rats3d.entities.Entity;
import org.joml.Matrix4f;

import java.util.*;

public class ParticleMaster {
   private static final Map<TextureAtlas,List<Particle>> particles = new HashMap<>();
   private static ParticleRenderer renderer;

   public static void init(Matrix4f projectionMatrix){
      renderer = new ParticleRenderer(projectionMatrix);
   }

   public static void update(Camera camera){
      Iterator<Map.Entry<TextureAtlas, List<Particle>>> mapIterator = particles.entrySet().iterator();

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
      if(particle.getPosition().distanceSquared(viewer.getPosition()) > 1000000){
         return;
      }
      List<Particle> particleList = particles.computeIfAbsent(particle.getTexture(), _ -> new ArrayList<>());
      particleList.add(particle);
   }
}
